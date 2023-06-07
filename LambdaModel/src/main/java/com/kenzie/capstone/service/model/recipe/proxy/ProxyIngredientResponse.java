package com.kenzie.capstone.service.model.recipe.proxy;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ProxyIngredientResponse {

    private Integer id;
    private String name;
    private Double quantity;
    private String unitMeasurement;
    private String imageFileName;

    public ProxyIngredientResponse() {}

    public ProxyIngredientResponse(Integer id, String name, Double quantity, String unitMeasurement, String imageFileName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.imageFileName = imageFileName;
    }

    @JsonGetter("id")
    public Integer getId() {
        return id;
    }
    @JsonSetter("id")
    public void setId(Integer id) {
        this.id = id;
    }
    @JsonGetter("name")
    public String getName() {
        return name;
    }
    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonGetter("quantity")
    public Double getQuantity() {
        return quantity;
    }
    @JsonSetter("amount")
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    @JsonGetter("unitMeasurement")
    public String getUnitMeasurement() {
        return unitMeasurement;
    }
    @JsonSetter("unit")
    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    @JsonGetter("imageFileName")
    public String getImageFileName() {
        return imageFileName;
    }
    @JsonSetter("image")
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public String toString() {
        return "ProxyIngredientResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitMeasurement='" + unitMeasurement + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                '}';
    }
}