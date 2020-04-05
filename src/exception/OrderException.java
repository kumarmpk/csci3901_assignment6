package exception;

//user defined exceptions to print the custom messages and details to user

public class OrderException extends Exception{

    //field used to store the reference for which exception is thrown/generated
    private int reference;

    //constructor to store the error message
    public OrderException(String message){
        super(message);
    }

    //constructor to store both the error message and reference
    public OrderException(String message, int reference){
        super(message);
        this.reference = reference;
    }

    //method to get the reference from exception object
    public int getReference() {
        return reference;
    }
}
