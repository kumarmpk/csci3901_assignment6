package db;

import common.Utility;
import constant.ConnectionProperties;
import exception.OrderException;
import plainobjects.PurchaseOrder;
import java.sql.*;
import java.util.*;
import java.util.Date;

//Database implementation class
//connects with database for all database activities

public class DBHandlerImpl extends Utility implements DBHandler{

    /*
    getConnection method
    method is used to create a database connection
     */
    public Statement getConnection() throws OrderException {

        //the link to the database
        Connection connect = null;

        //a place to build up an SQL query
        Statement statement = null;

        //Using a properties structure, just to hide info from other users.
        Properties identity = new Properties();

        //storing the db credentials and loading as properties
        ConnectionProperties connProp = new ConnectionProperties();

        String user;
        String password;
        String dbName;

        // Filling the properties structure with my information
        connProp.setIdentity(identity);
        user =identity.getProperty("user");
        password =identity.getProperty("password");
        dbName =identity.getProperty("database");

        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false", user, password);

            //transaction will not commit itself
            //once all actions are done, it requires a manual commit
            connect.setAutoCommit(false);

            // Statements send queries to the database.
            statement = connect.createStatement();

            //set the database name
            statement.executeQuery("use " + dbName + ";");

        } catch (ClassNotFoundException e){
            //class not found exception is caught in this block
            throw new OrderException("Class not found exception is caught in getConnection method of DBHandler.");
        } catch (SQLException e) {
            //all sql related exceptions are caught in this block
            throw new OrderException("System faced SQLException in getConnection method of DBHandler.");
        }catch (Exception e){
            //other unexpected exceptions are caught in this block
            throw new OrderException("System faced unexcepted exception in getConnection method of DBHandler.");
        }
        return statement;
    }

    /*
    shipOrder method
    gets the orderNumber as input
    executes sequence of database activities to ship the order
     */
    @Override
    public void shipOrder(int orderNumber) throws OrderException {

        //a data structure to receive results from an SQL query
        ResultSet resultSet = null;
        Queries queries = new Queries();
        int output;

        //getting database from connection
        Statement statement = getConnection();

        try {

            //update the shippedDate column in order table first
            output = statement.executeUpdate(queries.updateShipDate(orderNumber));

            //one record should be updated otherwise exception is thrown
            if(output != 1){
                throw new OrderException("Ship order failed. Order# "+orderNumber
                        +" is already shipped or order number is not available in the database.", orderNumber);
            }

            //query to fetch the existing inventory details
            resultSet = statement.executeQuery(queries.checkInventoryForShipping(orderNumber));

            while (resultSet.next()){
                int stockAfterShipping = resultSet.getInt("quantity");

                //checking whether we have sufficient inventory to ship
                if(stockAfterShipping < 0){

                    PurchaseOrder purchaseOrder = new PurchaseOrder();
                    purchaseOrder.setProductId(resultSet.getInt("productid"));
                    purchaseOrder.setQuantity(stockAfterShipping);
                    purchaseOrder.setSupplierId(resultSet.getInt("supplierid"));
                    purchaseOrder.setShipOrderRef(orderNumber);

                    output = issueReorder(convertDateToString(new Date()), purchaseOrder);

                    //if the reorder is success
                    if(output > 0){
                        throw new OrderException("Not enough stock to ship the product \""
                                +resultSet.getString("productname")+"\". Shipping failed. " +
                                "Reorder has been placed with currentDate to that supplier alone.", orderNumber);
                    } else {
                        //when stock is not available and waiting to receive the order
                        throw new OrderException("Not enough stock to ship the product \""
                                +resultSet.getString("productname")+"\". Shipping failed. " +
                                "We are waiting to receive the product from supplier. So reorder is not placed.", orderNumber);
                    }
                }
            }

            //updating the inventory after shipping
            output = statement.executeUpdate(queries.updateShipInventory(orderNumber));

            //number of records updated should be greater than one
            if(output < 1){
                throw new OrderException("Ordered products is not in our system kindly check.", orderNumber);
            }

            statement.getConnection().commit();

        } catch (SQLException e) {
            //all sql exceptions are caught in this block
            throw new OrderException("System faced SQLException in ship order method of DBHandler.", orderNumber);
        } catch (OrderException e){
            //all user defined order exceptions are caught in this block
            throw e;
        } catch (Exception e){
            //other unexpected exceptions are caught in this block
            throw new OrderException("System faced unexcepted exception in shipOrder method of DBHandler.", orderNumber);
        }finally {
            try{
                //closes the connection
                if(statement.getConnection() != null){
                    statement.getConnection().close();
                }
                //closes the statement
                if(statement != null){
                    statement.close();
                }
                //closes the resultset
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e){
                //exceptions from finally block is caught here
                throw new OrderException("System faced unexcepted exception in finally block of ship order method of DBHandler.", orderNumber);
            }
        }
    }

    /*
    issueReorder method
    gets the reorder date as input
    returns the number of suppliers the reorder placed
     */
    @Override
    public int issueReorder(String date, PurchaseOrder purOrder) throws Exception {
        int noOfSuppliers = 0;

        ResultSet resultSet = null;
        Queries queries = new Queries();
        int output = 0;
        List<PurchaseOrder> purchaseOrderList = null;
        List<Integer> purchaseIdList = null;
        Set<Integer> supplierList = null;

        //gets the database connection
        Statement statement = getConnection();
        try {
            //check whether the reorder is already done for the date
            resultSet = statement.executeQuery(queries.checkReorderStatusForDate(date));

            purchaseIdList = new ArrayList<>();

            while (resultSet.next()){
                output = resultSet.getInt("purchaseId");
                if(output > 0){
                    purchaseIdList.add(output);
                }
            }

            //fetch all the order details for reorder
            if(purOrder == null) {
                resultSet = statement.executeQuery(queries.getReorderDetails());
            } else {
                resultSet = statement.executeQuery(queries.getReorderDetails(purOrder));
            }

            purchaseOrderList = new ArrayList<>();
            supplierList = new HashSet<>();

            while (resultSet.next()){
                PurchaseOrder purchaseOrder = new PurchaseOrder();
                purchaseOrder.setProductId(resultSet.getInt("productid"));
                purchaseOrder.setSupplierId(resultSet.getInt("supplierid"));
                purchaseOrder.setUnitBuyPrice(resultSet.getDouble("unitBuyPrice"));
                purchaseOrder.setPurchaseDate(date);

                int purchaseId = Integer.valueOf(generatePurchaseId(purchaseOrder));

                // unexpected exception while generating purchase id is caught
                if(purchaseId == 0){
                    throw new Exception("System faced unexpected exception in issue reorder method in DBHandler.");
                }
                //reorder is already placed for the supplier, even then stock is less than reorder level
                else if(purchaseIdList.contains(purchaseId) && purOrder == null){
                    printString("The below product is out of stock, but the reorder is already placed for the date and supplier combination.");
                    printString("   Supplier Id: "+purchaseOrder.getSupplierId());
                    printString("   Product name: "+resultSet.getString("productname"));
                    printString("User can either receive the order if not received yet" +
                            " or manually place reorder in a different date if required.");
                    continue;
                }
                //ship order flow reorder call - reorder is already placed and waiting to receive the order
                else if(purchaseIdList.contains(purchaseId) && purOrder != null){
                    throw new OrderException("Not enough stock to ship the product \""
                            +resultSet.getString("productname")+"\". Shipping failed. " +
                            "We are waiting to receive the product from supplier. So reorder is not placed.",
                            purOrder.getShipOrderRef());
                }

                purchaseOrder.setPurchaseId(purchaseId);

                //setting the reorder level as ordering quantity
                purchaseOrder.setQuantity(resultSet.getInt("quantity"));

                supplierList.add(purchaseOrder.getSupplierId());
                purchaseOrderList.add(purchaseOrder);
            }

            //direct reorder call - in case of no products to reorder
            if((purchaseOrderList == null || purchaseOrderList.isEmpty()) && purOrder == null){
                throw new OrderException("No products to reorder. Reorder failed.");
            }
            //ship order flow reorder call - in case of no products to reorder
            else if((purchaseOrderList == null || purchaseOrderList.isEmpty()) && purOrder != null){
                throw new OrderException("Not enough stock to ship the product. Shipping failed. " +
                        "We are waiting to receive the product from supplier. So reorder is not placed.",
                        purOrder.getShipOrderRef());
            }

            //inserting the purchase order details into the new table
            output = statement.executeUpdate(queries.insertPurchaseOrder(purchaseOrderList));

            //in case of any failure in insert query
            if(output < 1){
                throw new OrderException("No products to reorder. Reorder failed.");
            }

            //updating the reorder inventory details in products table
            output = statement.executeUpdate(queries.updateReorderInventory(date));

            //in case of any failure in update query
            if(output < 1){
                throw new OrderException("Reorder inventory update failed. Kindly check.");
            }

            noOfSuppliers = supplierList.size();

            //committing all the changes once all are success
            statement.getConnection().commit();

        }
        catch (SQLException e){
            //all sql exceptions are caught in this block
            throw new OrderException("System faced sql exception in issue reorder method in DBHandler.");
        } catch (OrderException e){
            //all user defined exceptions are caught in this block
            throw e;
        } catch (Exception e){
            //all other unexpected exceptions are caught in this block
            throw new OrderException("System faced unexpected exception in issue reorder method in DBHandler.");
        }finally {
            try{
                //closes the connection
                if(statement.getConnection() != null){
                    statement.getConnection().close();
                }
                //closes the statement
                if(statement != null){
                    statement.close();
                }
                //closes the resultset
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e){
                //all exceptions thrown from finally block is caught here
                throw new OrderException("System faced unexcepted exception in finally block of ship order method of DBHandler.");
            }
        }

        return noOfSuppliers;
    }

    /*
    generatePurchaseId method
    receives the purchase order object as input
    returns the unique purchase id to the calling method
     */
    private String generatePurchaseId(PurchaseOrder purchaseOrder){
        String purchaseId = null;

        //purchase id is formed with date and supplier id
        //as the combination is unique for a given day
        //and can be used as primary key when combined with product id
        if(purchaseOrder.getPurchaseDate() != null) {
            purchaseId = purchaseOrder.getPurchaseDate().substring(0, 4)
                    + purchaseOrder.getPurchaseDate().substring(5, 7)
                    + purchaseOrder.getPurchaseDate().substring(8, 10)
                    + purchaseOrder.getSupplierId();
        }

        return purchaseId;
    }

    /*
    receiveOrder method
    gets the purchaseId as input
    executes sequence database queries to receive the order from supplier
     */
    @Override
    public void receiveOrder(int purchaseId) throws OrderException {

        ResultSet resultSet = null;
        Queries queries = new Queries();
        int count = 0;

        //creates a database connection
        Statement statement = getConnection();
        try {

            //update the received date in the new table
            count = statement.executeUpdate(queries.updateReceiveDtlsInPurchase(purchaseId));

            //if the update failed then throwing the message to the user
            if(count < 1){
                throw new OrderException("Purchase order is already received or purchase order reference is invalid.", purchaseId);
            }

            //updating the inventory after receiving the order
            count = statement.executeUpdate(queries.updateReceiveOrderInventory(purchaseId));

            //in case of any failure in query, it is thrown
            if(count < 1){
                throw new OrderException("System faced exception while updating receive order inventory.", purchaseId);
            }

            //system commiting after all actions
            statement.getConnection().commit();

        } catch (SQLException e) {
            //all sql exceptions are caught in this block
            throw new OrderException("System faced SQL exception in receive order method in DBHandler.", purchaseId);
        } catch (OrderException e){
            //all user defined exceptions are caught in this block
            throw e;
        } catch (Exception e) {
            //all other unexpected exceptions are caught in this block
            throw new OrderException("System faced unexpected exception in receive order method in DBHandler.", purchaseId);
        }finally {
            try{
                //closes the connection
                if(statement.getConnection() != null){
                    statement.getConnection().close();
                }
                //closes the statement
                if(statement != null){
                    statement.close();
                }
                //closes the resultset
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e){
                //all exceptions of finally block are caught in this block
                throw new OrderException("System faced unexcepted exception in finally block of ship order method of DBHandler.", purchaseId);
            }
        }

    }

}
