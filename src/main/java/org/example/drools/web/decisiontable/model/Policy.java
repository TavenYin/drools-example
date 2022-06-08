package org.example.drools.web.decisiontable.model;


/**
 * @author tianwen.yin
 */
public class Policy {

    private String type;
    private boolean approved;
    private int discountPercent;
    private int basePrice;

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public int getDiscountPercent() {
        return discountPercent;
    }
    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void applyDiscount(int discount) {
        discountPercent += discount;
    }
    public int getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }
}
