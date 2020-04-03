package db;

import constant.ConnectionProperties;
import exception.OrderException;
import plainobjects.Product;
import plainobjects.PurchaseOrder;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DBHandlerImpl implements DBHandler{

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

        // Fill the properties structure with my information
        connProp.setIdentity(identity);
        user =identity.getProperty("user");
        password =identity.getProperty("password");
        dbName =identity.getProperty("database");

        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false", user, password);

            connect.setAutoCommit(false);

            // Statements send queries to the database.
            statement = connect.createStatement();

            //set the database name
            statement.executeQuery("use " + dbName + ";");

        }catch (ClassNotFoundException e){
            throw new OrderException("Class not found exception is caught in getConnection method of DBHandler.");
        }
        catch (SQLException e) {
            throw new OrderException("System faced SQLException in getConnection method of DBHandler.");
        }catch (Exception e){
            throw new OrderException("System faced unexcepted exception in getConnection method of DBHandler.");
        }
        return statement;
    }


    @Override
    public void shipOrder(int orderNumber) throws OrderException {

        //a data structure to receive results from an SQL query
        ResultSet resultSet = null;
        Queries queries = new Queries();
        int output;
        Statement statement = getConnection();
        List<Product> productList = null;

        try {

            //update the shippedDate column in order table first
            output = statement.executeUpdate(queries.updateShipDate(orderNumber));

            if(output != 1){
                throw new OrderException("Ship order failed. Order# "+orderNumber+" is already shipped or order number is not available in the database.");
            }

            resultSet = statement.executeQuery(queries.checkInventoryForShipping(orderNumber));

            while (resultSet.next()){
                int stockAfterShipping = resultSet.getInt("quantity");
                if(stockAfterShipping < 0){
                    throw new OrderException("Not enough stock to ship the product "+resultSet.getString("productname")
                            +". Shipping failed.");
                }
            }

            output = statement.executeUpdate(queries.updateShipInventory(orderNumber));

            if(output < 1){
                throw new OrderException("Ordered products is not in our system kindly check.");
            }

            statement.getConnection().commit();

        } catch (SQLException e) {
            throw new OrderException("System faced SQLException in ship order method of DBHandler.");
        } catch (Exception e){
            throw new OrderException("System faced unexcepted exception in shipOrder method of DBHandler.");
        }finally {
            try{
                //closes the connection
                if(statement.getConnection() != null){
                    statement.getConnection().close();
                }
                if(statement != null){
                    statement.close();
                }
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e){
                throw new OrderException("System faced unexcepted exception in finally block of ship order method of DBHandler.");
            }
        }
    }

    @Override
    public int issueReorder(java.sql.Date date) throws Exception {
        int noOfSuppliers = 0;

        ResultSet resultSet = null;
        Queries queries = new Queries();
        int output = 0;
        Statement statement = getConnection();
        List<PurchaseOrder> purchaseOrderList = null;

        try {
            resultSet = statement.executeQuery(queries.checkReorderStatusForDate(date));

            while (resultSet.next()){
                output = resultSet.getInt("count");
                if(output > 0){
                    throw new OrderException("Reorder for the date is already done. Reorder failed");
                }
            }

            resultSet = statement.executeQuery(queries.getReorderDetails());

            purchaseOrderList = new ArrayList<>();

            while (resultSet.next()){
                PurchaseOrder purchaseOrder = new PurchaseOrder();
                purchaseOrder.setProductId(resultSet.getInt("productid"));
                purchaseOrder.setSupplierId(resultSet.getInt("supplierid"));
                purchaseOrder.setUnitBuyPrice(resultSet.getInt("unitBuyPrice"));
                purchaseOrder.setQuantity(resultSet.getInt("quantity"));
                purchaseOrder.setPurchaseDate(date);

                int purchaseId = Integer.valueOf(generatePurchaseId(purchaseOrder));
                purchaseOrder.setPurchaseId(purchaseId);

                purchaseOrderList.add(purchaseOrder);
            }

            output = statement.executeUpdate(queries.insertPurchaseOrder(purchaseOrderList));

            if(output < 1){
                throw new OrderException("No products to reorder. Reorder failed.");
            }

            output = statement.executeUpdate(queries.updateReorderInventory(date));

            if(output < 1){
                throw new OrderException("Reorder inventory update failed. Kindly check.");
            }

            resultSet = statement.executeQuery(queries.getSuppliersCount(date));

            while (resultSet.next()){
                noOfSuppliers = resultSet.getInt("count");
            }

            statement.getConnection().commit();

        }
        catch (SQLException e){
            throw new OrderException("System faced sql exception in issue reorder method in DBHandler.");
        }
        catch (Exception e){
            throw new OrderException("System faced unexpected exception in issue reorder method in DBHandler.");
        }finally {
            try{
                //closes the connection
                if(statement.getConnection() != null){
                    statement.getConnection().close();
                }
                if(statement != null){
                    statement.close();
                }
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e){
                throw new OrderException("System faced unexcepted exception in finally block of ship order method of DBHandler.");
            }
        }

        return noOfSuppliers;
    }

    private String generatePurchaseId(PurchaseOrder purchaseOrder){
        String purchaseId = null;

        purchaseId = String.valueOf(purchaseOrder.getPurchaseDate().getYear())
                        +String.valueOf(purchaseOrder.getPurchaseDate().getMonth())
                        +String.valueOf(purchaseOrder.getPurchaseDate().getDay())
                        +String.valueOf(purchaseOrder.getSupplierId());

        return purchaseId;
    }

    @Override
    public void receiveOrder(int purchaseId) throws OrderException {

        ResultSet resultSet = null;
        Queries queries = new Queries();
        Statement statement = getConnection();
        int count = 0;

        try {

            count = statement.executeUpdate(queries.updateReceiveDtlInPurchase(purchaseId));

            if(count < 1){
                throw new OrderException("Purchase order is already received or purchase order reference is invalid.");
            }

            count = statement.executeUpdate(queries.updateReceiveOrderInventory(purchaseId));

            if(count < 1){
                throw new OrderException("System faced exception while updating receive order inventory.");
            }

            statement.getConnection().commit();

        } catch (SQLException e) {
            throw new OrderException("System faced SQL exception in receive order method in DBHandler.");
        } catch (Exception e) {
            throw new OrderException("System faced unexpected exception in receive order method in DBHandler.");
        }finally {
            try{
                //closes the connection
                if(statement.getConnection() != null){
                    statement.getConnection().close();
                }
                if(statement != null){
                    statement.close();
                }
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e){
                throw new OrderException("System faced unexcepted exception in finally block of ship order method of DBHandler.");
            }
        }

    }

}
