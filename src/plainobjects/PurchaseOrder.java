package plainobjects;

import java.sql.Date;

public class PurchaseOrder {

    private int productId;
    private int quantity;
    private int supplierId;
    private double unitSellPrice;
    private double unitBuyPrice;
    private int unitsInStock;
    private int unitsOnOrder;
    private int reorderLevel;
    private Date purchaseDate;
    private int purchaseId;

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getUnitBuyPrice() {
        return unitBuyPrice;
    }

    public void setUnitBuyPrice(double unitBuyPrice) {
        this.unitBuyPrice = unitBuyPrice;
    }

    public int getUnitsInStock() {
        return unitsInStock;
    }

    public void setUnitsInStock(int unitsInStock) {
        this.unitsInStock = unitsInStock;
    }

    public int getUnitsOnOrder() {
        return unitsOnOrder;
    }

    public void setUnitsOnOrder(int unitsOnOrder) {
        this.unitsOnOrder = unitsOnOrder;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public double getUnitSellPrice() {
        return unitSellPrice;
    }

    public void setUnitSellPrice(double unitSellPrice) {
        this.unitSellPrice = unitSellPrice;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



}
