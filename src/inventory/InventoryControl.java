package inventory;


import exception.OrderException;

public interface InventoryControl {

    void Ship_order( int orderNumber ) throws OrderException;
    int Issue_reorders( int year, int month, int day );
    void Receive_order( int internal_order_reference ) throws OrderException;


}
