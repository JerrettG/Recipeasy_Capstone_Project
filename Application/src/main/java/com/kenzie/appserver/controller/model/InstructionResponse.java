package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstructionResponse {

    @JsonProperty("number")
    private Integer number;
    @JsonProperty("step")
    private String step;

    public InstructionResponse() {

    }

    public InstructionResponse(Integer number, String step) {
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
        return "InstructionResponse{" +
                "number=" + number +
                ", step='" + step + '\'' +
                '}';
    }
}
