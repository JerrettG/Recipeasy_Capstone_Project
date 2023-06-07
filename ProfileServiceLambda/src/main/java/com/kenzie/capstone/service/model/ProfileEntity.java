package com.kenzie.capstone.service.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import com.kenzie.capstone.service.converter.DietsTypeConverter;

import java.util.List;
import java.util.Objects;

@DynamoDBTable(tableName = "Profiles")
public class ProfileEntity {

    private String userId;
    private String name;
    private String email;
    private String createdDate;
    private String lastUpdated;
    private List<Diet> diets;


    public ProfileEntity() {}

    public ProfileEntity(String userId, String name, String email, String createdDate, String lastUpdated, List<Diet> diets) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.diets = diets;
    }
    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @DynamoDBAttribute(attributeName = "createdDate")
    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    @DynamoDBAttribute(attributeName = "lastUpdated")
    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    @DynamoDBAttribute(attributeName = "diets")
    @DynamoDBTypeConverted(converter = DietsTypeConverter.class)
    public List<Diet> getDiets() {
        return diets;
    }

    public void setDiets(List<Diet> diets) {
        this.diets = diets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileEntity that = (ProfileEntity) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "ProfileEntity{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", diets=" + diets +
                '}';
    }
}
