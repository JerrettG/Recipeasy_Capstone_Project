package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class InstructionEntity {
    private Integer number;
    private String step;

    public InstructionEntity() {}

    public InstructionEntity(Integer number, String step) {
        this.number = number;
        this.step = step;
    }

    @DynamoDBAttribute(attributeName = "number")
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @DynamoDBAttribute(attributeName = "step")
    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
