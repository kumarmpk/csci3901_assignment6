package db;

import plainobjects.PurchaseOrder;

import java.sql.Date;
import java.util.List;

public class Queries {

    private final String UPDATE_SHIP_DATE = "update orders set shippeddate = SYSDATE() where shippeddate is null and orderid = '";

    public String updateShipDate(int orderNumber){
        String query;

        query = UPDATE_SHIP_DATE.concat(String.valueOf(orderNumber)).concat("';");
        return query;
    }

    public String checkInventoryForShipping(int orderNumber){
        String query = null;

        query = "select prod.unitsinstock-dtls.quantity as quantity, prod.productname from products prod, orderdetails dtls "
                .concat(" where prod.productid = dtls.productid and dtls.orderid = '")
                .concat(String.valueOf(orderNumber)).concat("'; ");

        return query;
    }

    public String updateShipInventory(int orderNumber){
        String query;

        query = "update products prod, orderdetails dtls set prod.unitsinstock = prod.unitsinstock-dtls.quantity "
                .concat(" where prod.productid = dtls.productid and orderid = '")
                .concat(String.valueOf(orderNumber)).concat("' ; ");

        return query;
    }

    public String getReorderDetails(){
        String query = null;

        query = "select prod.productid, prod.supplierid, (prod.unitprice * 0.85) as unitBuyPrice, "
                .concat(" DECODE(prod.reorderlevel, 0, 5, prod.reorderlevel) as quantity from products prod where ")
                .concat(" prod.discontinued = 0 and prod.unitsinstock+prod.unitsonorder <= prod.reorderlevel);");

        return query;
    }

    public String insertPurchaseOrder(List<PurchaseOrder> purchaseOrderList){
        String query = null;

        query = "insert into purchases (purchaseid, productid, supplierid, purchasedate, unitprice, quantity) values ";

        for(PurchaseOrder purchaseOrder : purchaseOrderList){
            query = query.concat("(").concat(String.valueOf(purchaseOrder.getPurchaseId())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getProductId())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getSupplierId())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getPurchaseDate())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getUnitBuyPrice())).concat(" , ")
                    .concat(String.valueOf(purchaseOrder.getQuantity())).concat(" ), ");
        }

        int length = query.length();

        query = query.substring(0, length - 1);

        return query ;
    }

    public String updateReorderInventory(Date date){
        String query = null;

        query = "update purchases pur, products prod set prod.unitsonorder = prod.unitsonorder + pur.quantity "
                .concat(" where pur.productid = prod.productid and pur.supplierid = prod.supplierid")
                .concat(" and date(pur.purchasedate) = date(").concat(String.valueOf(date)).concat(");");

        return query;
    }

    public String updateReceiveOrderInventory(int purchaseId){
        String query = null;

        query = "update purchases pur, products prod set prod.unitsonorder = prod.unitsonorder - pur.quantity, "
                .concat(" prod.unitsinstock = prod.unitsinstock + pur.quantity where ")
                .concat("pur.productid = prod.productid and pur.supplierid = prod.supplierid ")
                .concat("and pur.purchaseid = '").concat(String.valueOf(purchaseId).concat("';")) ;

        return query;
    }

    public String updateReceiveDtlInPurchase(int purchaseId){
        String query = null;

        query = "update purchases set receivedate = sysdate() where purchaseid = ".concat(String.valueOf(purchaseId));

        return query;
    }

    public String checkReorderStatusForDate(Date date){
        String query= null;

        query = "select count(1) as count from purchases where trunc(purchasedate) = trunc("+date+");";

        return query;
    }

    public String getSuppliersCount(Date date){
        String query = null;

        query = "select count(distinct supplierid) as count from purchases where date(purchasedate) = date("+date+");";

        return query;
    }

}
