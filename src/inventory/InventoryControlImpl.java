package inventory;

import constant.ConstantsClass;
import db.DBHandler;
import db.DBHandlerImpl;
import exception.OrderException;

import java.sql.Date;


public class InventoryControlImpl implements InventoryControl {

    private void printString(String input){
        System.out.println(input);
    }

    @Override
    public void Ship_order(int orderNumber) throws OrderException {

        DBHandler db = new DBHandlerImpl();

        db.shipOrder(orderNumber);

    }

    private boolean dateValidator(int year, int month, int day){
        boolean isValid = true;

        Date currentDate = new Date(System.currentTimeMillis());

        if(year < ConstantsClass.ONE){
            printString("Given year is invalid.");
            isValid = false;
        }

        if(year > currentDate.getYear()){
            printString("Given year belongs to future date.");
            isValid = false;
        }

        if(month < ConstantsClass.ONE || month > ConstantsClass.TWELVE ){
            printString("Given month is invalid.");
            isValid = false;
        }

        if(year == currentDate.getYear() && month > currentDate.getMonth()){
            printString("Given month belongs to future date.");
            isValid = false;
        }

        if(day < ConstantsClass.ONE || day > ConstantsClass.THIRTYONE){
            printString("Given day is invalid.");
            isValid = false;
        }

        if((month == 2 || month == 4 || month == 6 || month == 9 || month == 11) && day > 30){
            printString("Given day is invalid.");
            isValid = false;
        }

        if( !(((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) ) {
            if(month == 2 && day > 28){
                printString("Given day is invalid.");
                isValid = false;
            }
        } else {
            if(month == 2 && day > 29){
                printString("Given day is invalid.");
                isValid = false;
            }
        }

        if(year == currentDate.getYear() && month == currentDate.getMonth()
                && day > currentDate.getDay()) {
            printString("Given day belongs to future date.");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public int Issue_reorders(int year, int month, int day) {

        boolean isValid = dateValidator(year, month, day);
        int noOfSuppliers = 0;

        if(!isValid){
            return ConstantsClass.ZERO;
        }

        Date reorderDate = new Date(year, month, day);

        DBHandler db = new DBHandlerImpl();
        try {
            noOfSuppliers = db.issueReorder(reorderDate);
        } catch (Exception e){
            printString(e.getMessage());
        }

        return noOfSuppliers;
    }


    @Override
    public void Receive_order(int internal_order_reference) throws OrderException {

        DBHandler db = new DBHandlerImpl();

        db.receiveOrder(internal_order_reference);

    }
}
