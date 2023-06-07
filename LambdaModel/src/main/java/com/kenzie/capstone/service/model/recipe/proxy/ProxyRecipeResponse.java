package com.kenzie.capstone.service.model.recipe.proxy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
@JsonDeserialize(using = ProxyRecipeResponseDeserializer.class)
public class ProxyRecipeResponse {

    private Integer id;
    private String name;
    private List<ProxyIngredientResponse> proxyIngredientResponseList;
    private Integer readyInMinutes;
    private List<ProxyInstructionResponse> proxyInstructionResponses;
    private String imageUrl;
    private String imageSourceUrl;
    private String summary;

    public ProxyRecipeResponse() {}

    public ProxyRecipeResponse(Integer id, String name, List<ProxyIngredientResponse> proxyIngredientResponseList, Integer readyInMinutes,
                               List<ProxyInstructionResponse> proxyInstructionResponses, String imageUrl, String summary) {
        this.id =id;
        this.name = name;
        this.proxyIngredientResponseList = proxyIngredientResponseList;
        this.readyInMinutes = readyInMinutes;
        this.proxyInstructionResponses = proxyInstructionResponses;
        this.imageUrl = imageUrl;
        this.summary = summary;
    }
    public Integer getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProxyIngredientResponse> getIngredients() {
        return proxyIngredientResponseList;
    }

    public void setIngredientList(List<ProxyIngredientResponse> ingredients) {
        this.proxyIngredientResponseList = ingredients;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public List<ProxyInstructionResponse> getInstructions() {
        return proxyInstructionResponses;
    }

    public void setInstructions(List<ProxyInstructionResponse> instructions) {
        this.proxyInstructionResponses = instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageSourceUrl() {
        return imageSourceUrl;
    }

    public void setImageSourceUrl(String imageSourceUrl) {
        this.imageSourceUrl = imageSourceUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "ProxyRecipeResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", proxyIngredientResponseList=" + proxyIngredientResponseList +
                ", readyInMinutes=" + readyInMinutes +
                ", proxyInstructionResponses=" + proxyInstructionResponses +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageSourceUrl='" + imageSourceUrl + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
