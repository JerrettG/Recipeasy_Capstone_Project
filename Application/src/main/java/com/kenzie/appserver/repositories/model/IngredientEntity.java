package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class IngredientEntity {
    private Integer id;
    private String name;
    private Double quantity;
    private String unitMeasurement;
    private String imageFileName;

    public IngredientEntity() {}

    public IngredientEntity(Integer id, String name, Double quantity, String unitMeasurement, String imageFileName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.imageFileName = imageFileName;
    }

    @DynamoDBAttribute(attributeName = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @DynamoDBAttribute(attributeName = "quantity")
    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    @DynamoDBAttribute(attributeName = "unitMeasurement")
    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    @DynamoDBAttribute(attributeName = "imageFileName")
    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
