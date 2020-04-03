import inventory.InventoryControl;
import inventory.InventoryControlImpl;

import java.util.Scanner;

public class MainClass {

    public static void main(String[] args){

        String shipOrder = "shipOrder";
        String issueReorder = "issueReorder";
        String receiveOrder = "receiveOrder";
        String quit = "quit";

        String userCommand = "";
        int userArgument = 0;
        Scanner userInput = new Scanner( System.in );

        InventoryControl obj = new InventoryControlImpl();

        System.out.println("Operations available to perform: ");
        System.out.println("    "+shipOrder+"<order number>");
        System.out.println("    "+issueReorder+"<year> <month> <day>");
        System.out.println("    "+receiveOrder+"<internal order reference>");
        System.out.println("    "+quit);

        try {
            do {

                userCommand = userInput.next();

                if (userCommand.equalsIgnoreCase(shipOrder)) {

                    userArgument = userInput.nextInt();

                    obj.Ship_order(userArgument);

                }
                else if (userCommand.equalsIgnoreCase(issueReorder)) {

                    int year = userInput.nextInt();
                    int month = userInput.nextInt();
                    int day = userInput.nextInt();

                    int noOfSuppliers = obj.Issue_reorders(year, month, day);
                    System.out.println("No of suppliers to reorder : "+ noOfSuppliers);
                }
                else if(userCommand.equalsIgnoreCase(receiveOrder)){

                    int purchaseId = userInput.nextInt();

                    obj.Receive_order(purchaseId);
                }

            } while (!userCommand.equalsIgnoreCase(quit));

        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            userInput.close();
        }


    }



}
