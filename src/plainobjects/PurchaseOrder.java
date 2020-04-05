package plainobjects;

//Purchase order object to store the information
//matches with new purchases table

public class PurchaseOrder {

    //product identifier
    private int productId;

    //quantity that we made the purchase
    private int quantity;

    //supplier identifier
    private int supplierId;

    //unit buying price from the suppliers calculated
    private double unitBuyPrice;

    //purchase placed date
    private String purchaseDate;

    //purchase id formed with purchase date and supplier id
    private int purchaseId;

    //shipping order reference number
    private int shipOrderRef;

    /*
    getShipOrderRef method
    returns the shipping order reference number of this object
     */
    public int getShipOrderRef(){
        return this.shipOrderRef;
    }

    /*
    setShipOrderRef method
    gets the shipping order reference number as input
    sets the input into the shipping order reference number of this object
     */
    public void setShipOrderRef(int shipOrderRef){
        this.shipOrderRef = shipOrderRef;
    }

    /*
     getPurchaseId method
     returns the purchase of this object
     */
    public int getPurchaseId() {
        return this.purchaseId;
    }

    /*
    setPurchaseId method
    gets the purchaseId as input
    sets the input into the purchaseId of this object
     */
    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    /*
    getPurchaseDate method
    returns the purchaseDate of this object
     */
    public String getPurchaseDate() {
        return this.purchaseDate;
    }

    /*
    setPurchaseDate method
    gets the purchaseDate as input
    sets the input into the purchaseDate of this object
     */
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /*
    getUnitBuyPrice method
    returns the unitBuyPrice of this object
     */
    public double getUnitBuyPrice() {
        return this.unitBuyPrice;
    }

    /*
    setUnitBuyPrice method
    gets the unitBuyPrice as input
    sets the input into the unitBuyPrice of this object
     */
    public void setUnitBuyPrice(double unitBuyPrice) {
        this.unitBuyPrice = unitBuyPrice;
    }

    /*
    getSupplierId method
    returns the supplierId of this object
     */
    public int getSupplierId() {
        return this.supplierId;
    }

    /*
    setSupplierId method
    gets the supplierId as input
    sets the input into the supplierId of this object
     */
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    /*
    getProductId method
    returns the productId of this object
     */
    public int getProductId() {
        return this.productId;
    }

    /*
    setProductId method
    gets the productId as input
    sets the input into the productId of this object
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /*
    getQuantity method
    retuns the quantity of this object
     */
    public int getQuantity() {
        return this.quantity;
    }

    /*
    setQuantity method
    gets the quantity as input
    sets the input into the quantity of this object
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
