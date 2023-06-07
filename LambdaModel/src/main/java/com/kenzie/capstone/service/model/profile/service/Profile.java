package com.kenzie.capstone.service.model.profile.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile {

    private String userId;
    private String name;
    private String email;
    private String createdDate;
    private String lastUpdated;
    private List<Diet> diets;

    public Profile() {}

    public Profile(String userId, String name, String email, String createdDate, String lastUpdated, List<Diet> diets) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.diets = new ArrayList<>(diets);
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }
    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }
    @JsonProperty("createdDate")
    public String getCreatedDate() {
        return createdDate;
    }
    @JsonProperty("createdDate")
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    @JsonProperty("lastUpdated")
    public String getLastUpdated() {
        return lastUpdated;
    }
    @JsonProperty("lastUpdated")
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    @JsonProperty("diets")
    public List<Diet> getDiets() {
        return new ArrayList<>(diets);
    }
    @JsonProperty("diets")
    public void setDiets(List<Diet> diets) {
        this.diets = new ArrayList<>(diets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(userId, profile.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", diets=" + diets +
                '}';
    }
}

