package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;

public class IngredientResponse {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("quantity")
    private Double quantity;
    @JsonProperty("unitMeasurement")
    private String unitMeasurement;
    @JsonProperty("imageFileName")
    private String imageFileName;

    public IngredientResponse() {}

    public IngredientResponse(Integer id, String name, Double quantity, String unitMeasurement, String imageFileName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.imageFileName = imageFileName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
