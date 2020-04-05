package db;

import plainobjects.PurchaseOrder;

import java.util.List;

//a class to form the queries for the database layer

public class Queries {

    private final String UPDATE_SHIP_DATE = "update orders set shippeddate = SYSDATE() where shippeddate is null and orderid = '";

    /*
    updateShipDate method
    generates query to update shipped date
     */
    public String updateShipDate(int orderNumber){
        String query;

        query = UPDATE_SHIP_DATE.concat(String.valueOf(orderNumber)).concat("';");
        return query;
    }

    /*
    checkInventoryForShip method
    generates query to check whether we have enough stock to ship
     */
    public String checkInventoryForShipping(int orderNumber){
        String query = null;

        query = "select prod.unitsinstock-dtls.quantity as quantity, prod.productname, prod.productid, prod.supplierid "
                .concat(" from products prod, orderdetails dtls where prod.productid = dtls.productid ")
                .concat(" and dtls.orderid = '").concat(String.valueOf(orderNumber)).concat("'; ");

        return query;
    }

    /*
    updateShipInventory method
    generates query to update the inventory after shipping
     */
    public String updateShipInventory(int orderNumber){
        String query;

        query = "update products prod, orderdetails dtls set prod.unitsinstock = prod.unitsinstock-dtls.quantity "
                .concat(" where prod.productid = dtls.productid and orderid = '")
                .concat(String.valueOf(orderNumber)).concat("' ; ");

        return query;
    }

    /*
    getReorderDetails method
    generates query to fetch product details for reorder
     */
    public String getReorderDetails(){
        String query = null;

        query = "select prod.productid, prod.supplierid, (prod.unitprice * 0.85) as unitBuyPrice, prod.productname, "
                .concat(" case when prod.reorderlevel = '0' then '5' else prod.reorderlevel ")
                .concat(" end as quantity from products prod where prod.discontinued = 0 ")
                .concat(" and prod.unitsinstock+prod.unitsonorder <= prod.reorderlevel; ");

        return query;
    }

    /*
    getReorderDetails method
    generates query to fetch product details for reorder
     */
    public String getReorderDetails(PurchaseOrder purchaseOrder){
        String query = null;

        query = "select prod.productid, prod.supplierid, (prod.unitprice * 0.85) as unitBuyPrice, prod.productname, "
                .concat(" case when prod.reorderlevel = '0' then '5' else prod.reorderlevel ")
                .concat(" end as quantity from products prod where prod.discontinued = 0 ")
                .concat(" and prod.supplierid = ").concat(String.valueOf(purchaseOrder.getSupplierId()))
                .concat(" and (prod.unitsinstock+prod.unitsonorder <= prod.reorderlevel or ")
                .concat(" (prod.unitsonorder = 0 and prod.productid = ")
                .concat(String.valueOf(purchaseOrder.getProductId())).concat("));");

        return query;
    }

    /*
    insertPurchaseOrder method
    generates query to insert the purchase order details into the new table
     */
    public String insertPurchaseOrder(List<PurchaseOrder> purchaseOrderList){
        String query = null;

        query = "insert into purchases (purchaseid, productid, supplierid, purchasedate, unitprice, quantity) values ";

        for(PurchaseOrder purchaseOrder : purchaseOrderList){
            query = query.concat("(").concat(String.valueOf(purchaseOrder.getPurchaseId())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getProductId())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getSupplierId())).concat(" , ")
                    .concat("'").concat(purchaseOrder.getPurchaseDate()).concat("' , ")
                    .concat(String.valueOf(purchaseOrder.getUnitBuyPrice())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getQuantity())).concat(" ), ");
        }

        int length = query.length();

        query = query.substring(0, length - 2);
        query = query.concat(";");

        return query ;
    }

    /*
    updateReorderInventory method
    generates query to update the inventory after reorder
     */
    public String updateReorderInventory(String date){
        String query = null;

        query = "update purchases pur, products prod set prod.unitsonorder = prod.unitsonorder + pur.quantity "
                .concat(" where pur.productid = prod.productid and pur.supplierid = prod.supplierid")
                .concat(" and date(pur.purchasedate) = date('").concat(date).concat("');");

        return query;
    }

    /*
    updateReceiveOrderInventory method
    generates query to update the inventory after receiving the order
     */
    public String updateReceiveOrderInventory(int purchaseId){
        String query = null;

        query = "update purchases pur, products prod set prod.unitsonorder = prod.unitsonorder - pur.quantity, "
                .concat(" prod.unitsinstock = prod.unitsinstock + pur.quantity where ")
                .concat("pur.productid = prod.productid and pur.supplierid = prod.supplierid ")
                .concat("and pur.purchaseid = '").concat(String.valueOf(purchaseId).concat("';")) ;

        return query;
    }

    /*
    updateReceiveDtlsInPurchase method
    generates query to update the received date in new purchase table
     */
    public String updateReceiveDtlsInPurchase(int purchaseId){
        String query = null;

        query = "update purchases set receivedate = sysdate() where receivedate is null and purchaseid = ".concat(String.valueOf(purchaseId));

        return query;
    }

    /*
    checkReorderStatusForDate method
    generates the query to check whether reorder is already done for the date
     */
    public String checkReorderStatusForDate(String date){
        String query= null;

        query = "select purchaseId from purchases where date(purchasedate) = date('"+date+"');";

        return query;
    }

}
