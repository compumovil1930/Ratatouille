package com.example.entities;

public class KitchenElement {
    String userId;
    String itemName;
    String brand;
    int quantity;

    public KitchenElement() {
    }

    public KitchenElement(String userId, String itemName, String brand, int quantity) {
        this.userId = userId;
        this.itemName = itemName;
        this.brand = brand;
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
