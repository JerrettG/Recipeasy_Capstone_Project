package com.kenzie.appserver.service.model;

public class Ingredient {

    private Integer id;
    private String name;
    private Double quantity;
    private String unitMeasurement;
    private String imageFileName;

    public Ingredient(Integer id, String name, Double quantity, String unitMeasurement, String imageFileName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.imageFileName = imageFileName;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}
