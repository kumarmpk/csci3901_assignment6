package db;

import exception.OrderException;
import plainobjects.PurchaseOrder;

//Interface of database layer
//Abstract the database implementation class from rest of the program

public interface DBHandler {

    void shipOrder(int orderNumber) throws OrderException;

    int issueReorder(String date, PurchaseOrder purchaseOrder) throws Exception;

    void receiveOrder(int purchaseId) throws OrderException;
}
