package db;

import exception.OrderException;

import java.sql.Date;

public interface DBHandler {

    void shipOrder(int orderNumber) throws OrderException;

    int issueReorder(Date date) throws Exception;

    void receiveOrder(int purchaseId) throws OrderException;
}
