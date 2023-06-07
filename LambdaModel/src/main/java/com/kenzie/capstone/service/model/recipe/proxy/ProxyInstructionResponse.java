package com.kenzie.capstone.service.model.recipe.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ProxyInstructionResponse {

    @JsonProperty("number")
    private Integer number;
    @JsonProperty("step")
    private String step;

    public ProxyInstructionResponse() {}

    public ProxyInstructionResponse(Integer number, String step) {
        this.number = number;
        this.step = step;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "ProxyInstructionResponse{" +
                "number=" + number +
                ", step='" + step + '\'' +
                '}';
    }

    // getters and setters
}