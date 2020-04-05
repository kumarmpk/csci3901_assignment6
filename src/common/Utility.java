package common;

import java.text.SimpleDateFormat;
import java.util.Date;

//a class to have common functionalities
//acts as a parent class to share the functionalities

public class Utility {

    /*
    convertDateToString method
    gets a date input
    returns the string formatted version of the input
     */
    public String convertDateToString(Date input){

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        //converts the date into string and in a particular format that database can accept
        String date = simpleDateFormat.format(input);

        return date;
    }

    /*
    printString method
    gets a string input
    prints the input to the user
    */
    public void printString(String input){
        System.out.println(input);
    }



}
