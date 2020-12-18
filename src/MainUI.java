import exception.OrderException;
import control.InventoryControl;
import control.InventoryControlImpl;

import java.util.Scanner;

//main class of the program

public class MainUI {

    //main method starting point of the program

    public static void main(String[] args){

        String shipOrder = "shipOrder";
        String issueReorder = "issueReorder";
        String receiveOrder = "receiveOrder";
        String quit = "quit";

        String userCommand = "";
        int userArgument = 0;
        Scanner userInput = new Scanner( System.in );

        //object created
        InventoryControl obj = new InventoryControlImpl();

        System.out.println("Operations available to perform: ");
        System.out.println("    "+shipOrder+" <order_number>");
        System.out.println("    "+issueReorder+" <year> <month> <day>");
        System.out.println("    "+receiveOrder+" <internal_order_reference>");
        System.out.println("    "+quit);

        try {
            do {

                userCommand = userInput.next();

                //for ship order method call
                if (userCommand.equalsIgnoreCase(shipOrder)) {

                    userArgument = userInput.nextInt();

                    obj.Ship_order(userArgument);
                }
                //for issue reorder method call
                else if (userCommand.equalsIgnoreCase(issueReorder)) {

                    int year = userInput.nextInt();
                    int month = userInput.nextInt();
                    int day = userInput.nextInt();

                    int noOfSuppliers = obj.Issue_reorders(year, month, day);
                    System.out.println("Number of suppliers the reorder placed : "+ noOfSuppliers);
                }
                //for receive order method call
                else if(userCommand.equalsIgnoreCase(receiveOrder)){

                    int purchaseId = userInput.nextInt();

                    obj.Receive_order(purchaseId);
                }
                //for invalid commands
                else {
                    System.out.println ("Bad command: " + userCommand);
                }

            } while (!userCommand.equalsIgnoreCase(quit));

        } catch (OrderException e){
            //all user defined exceptions are caught in this block
            System.out.println("Error message : "+e.getMessage());
            System.out.println("Error in reference : "+e.getReference());
        } catch (Exception e){
            //all other unexpected exceptions are caught in this block
            System.out.println("Error message : "+e.getMessage());
        }
        finally {
            userInput.close();
        }

    }

}
