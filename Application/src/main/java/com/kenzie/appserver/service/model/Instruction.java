package com.kenzie.appserver.service.model;

public class Instruction {

    private Integer number;
    private String step;

    public Instruction(Integer number, String step) {
        this.number = number;
        this.step = step;
    }

    public Integer getNumber() {
        return number;
    }

    public String getStep() {
        return step;
    }
}
