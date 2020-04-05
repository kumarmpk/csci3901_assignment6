package control;

import common.Utility;
import constant.ConstantsClass;
import db.DBHandler;
import db.DBHandlerImpl;
import exception.OrderException;
import java.util.Calendar;
import java.util.Date;

//the implementation class of the control layer
//Connecting the main class and the rest of the program

public class InventoryControlImpl extends Utility implements InventoryControl {

    /*
    Ship_order method
    gets the orderNumber as input
     */
    @Override
    public void Ship_order(int orderNumber) throws OrderException {

        DBHandler db = new DBHandlerImpl();

        //passes the orderNumber to the database layer if it is valid
        db.shipOrder(orderNumber);

        printString("Order is shipped successfully.");
    }

    /*
    dateValidator method
    gets the year, month, day as inputs
    returns whether the date is valid
     */
    private boolean dateValidator(int year, int month, int day){
        boolean isValid = true;

        //current date details
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = 1 + now.get(Calendar.MONTH);
        int currentDate = now.get(Calendar.DATE);

        //year cannot be negative
        if(year < ConstantsClass.ONE){
            printString("Given year is invalid.");
            isValid = false;
        }

        //year cannot be future
        if(year > currentYear){
            printString("Given year belongs to future date.");
            isValid = false;
        }

        //month cannot be negative or more than twelve
        if(month < ConstantsClass.ONE || month > ConstantsClass.TWELVE ){
            printString("Given month is invalid.");
            isValid = false;
        }

        //month cannot be future
        if(year == currentYear && month > currentMonth){
            printString("Given month belongs to future date.");
            isValid = false;
        }

        //day cannot be negative or more than 31
        if(day < ConstantsClass.ONE || day > ConstantsClass.THIRTYONE){
            printString("Given day is invalid.");
            isValid = false;
        }

        //day cannot be 31 for these months
        if((month == 2 || month == 4 || month == 6 || month == 9 || month == 11) && day > 30){
            printString("Given day is invalid.");
            isValid = false;
        }

        //for non leap year feb cannot have 29
        if( !(((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) ) {
            if(month == 2 && day > 28){
                printString("Given day is invalid.");
                isValid = false;
            }
        } else {
            //even for leap year feb cannot have 30
            if(month == 2 && day > 29){
                printString("Given day is invalid.");
                isValid = false;
            }
        }

        //day cannot be future
        if(year == currentYear && month == currentMonth
                && day > currentDate) {
            printString("Given day belongs to future date.");
            isValid = false;
        }

        return isValid;
    }

    /*
    Issue_reorders method
    gets the year, month, day as inputs
    returns the number of suppliers the reorder is placed
     */
    @Override
    public int Issue_reorders(int year, int month, int day) {

        //validates the input
        boolean isValid = dateValidator(year, month, day);
        int noOfSuppliers = 0;

        //invalid date then return ZERO
        if(!isValid){
            return ConstantsClass.ZERO;
        }

        //converts the input into a date
        Date reorderDate = new Date((year-1900), (month-1), day);


        DBHandler db = new DBHandlerImpl();
        try {
            //calling the database layer to fetch the information
            noOfSuppliers = db.issueReorder(convertDateToString(reorderDate), null);
        } catch (Exception e){
            //printing the exception faced
            printString(e.getMessage());
        }

        return noOfSuppliers;
    }

    /*
    Receive_order method
    gets the internal_order_reference (purchaseId) as input
     */
    @Override
    public void Receive_order(int internal_order_reference) throws OrderException {

        DBHandler db = new DBHandlerImpl();

        //passes the internal order reference to database layer to process the receive order
        db.receiveOrder(internal_order_reference);

        printString("Order is received successfully");
    }
}
