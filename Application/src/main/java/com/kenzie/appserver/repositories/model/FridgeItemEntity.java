package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DynamoDBTable(tableName = "FridgeItems")
public class FridgeItemEntity {
    @Id
    @DynamoDBIgnore

    private FridgeItemKey primaryKey;
    private String purchaseDate;
    private String lastUpdated;
    private Double quantity;

    private String unitMeasurement;
    private String imageFileName;

    public FridgeItemEntity() {}

    public FridgeItemEntity(String userId, String name, String purchaseDate, String lastUpdated, Double quantity, String unitMeasurement, String imageFileName) {
        this.primaryKey = new FridgeItemKey(userId, name);
        this.lastUpdated = lastUpdated;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.imageFileName = imageFileName;
    }

    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return primaryKey != null ? primaryKey.getUserId() : null;
    }

    @DynamoDBRangeKey(attributeName = "name")
    public String getName() {
        return primaryKey != null ? primaryKey.getName() : null;
    }
    @DynamoDBAttribute(attributeName = "purchaseDate")
    public String getPurchaseDate() {
        return purchaseDate;
    }

    @DynamoDBAttribute(attributeName = "lastUpdated")
    public String getLastUpdated() {
        return lastUpdated;
    }

    @DynamoDBAttribute(attributeName = "quantity")
    public Double getQuantity() {
        return quantity;
    }

    @DynamoDBAttribute(attributeName = "unitMeasurement")
    public String getUnitMeasurement() {
        return unitMeasurement;
    }
    @DynamoDBAttribute(attributeName = "imageFileName")
    public String getImageFileName() {
        return imageFileName;
    }

    public void setUserId(String userId) {
        if (primaryKey == null)
            primaryKey = new FridgeItemKey();
        primaryKey.setUserId(userId);
    }
    public void setName(String name) {
        if (primaryKey == null)
            primaryKey = new FridgeItemKey();
        primaryKey.setName(name);
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FridgeItemEntity that = (FridgeItemEntity) o;
        return Objects.equals(primaryKey, that.primaryKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryKey);
    }
}
