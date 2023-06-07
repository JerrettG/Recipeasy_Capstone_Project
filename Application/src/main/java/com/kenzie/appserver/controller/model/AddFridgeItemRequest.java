package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenzie.appserver.service.model.Ingredient;

import java.util.Objects;

public class AddFridgeItemRequest {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("purchaseDate")
    private String purchaseDate;
    @JsonProperty("quantity")
    private Double quantity;
    @JsonProperty("unitMeasurement")
    private String unitMeasurement;
    @JsonProperty("imageFileName")
    private String imageFileName;

    public AddFridgeItemRequest() {
    }

    public AddFridgeItemRequest(String userId, String name, String purchaseDate, Double quantity, String unitMeasurement, String imageFileName) {
        this.userId = userId;
        this.name = name;
        this.purchaseDate = purchaseDate;
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
        AddFridgeItemRequest that = (AddFridgeItemRequest) o;
        return Objects.equals(userId, that.userId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

    @Override
    public String toString() {
        return "AddFridgeItemRequest{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", quantity=" + quantity +
                ", unitMeasurement='" + unitMeasurement + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                '}';
    }
}
