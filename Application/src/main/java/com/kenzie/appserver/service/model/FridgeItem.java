package com.kenzie.appserver.service.model;

import java.util.List;
import java.util.Objects;

public class FridgeItem {
    private String userId;
    private String name;
    private String purchaseDate;
    private String lastUpdated;
    private Double quantity;
    private String unitMeasurement;
    private String imageFileName;

    public FridgeItem(){

    }
    public FridgeItem(String userId, String name, String purchaseDate, String lastUpdated, Double quantity, String unitMeasurement, String imageFileName) {
        this.userId = userId;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.lastUpdated = lastUpdated;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.imageFileName = imageFileName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FridgeItem that = (FridgeItem) o;
        return Objects.equals(userId, that.userId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

    @Override
    public String toString() {
        return "FridgeItem{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", quantity=" + quantity +
                ", unitMeasurement='" + unitMeasurement + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                '}';
    }
}
