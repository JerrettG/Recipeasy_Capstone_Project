package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName = "SavedRecipes")
public class SavedRecipeEntity {

    @Id
    @DynamoDBIgnore
    private SavedRecipeKey primaryKey;
    private String name;
    private List<IngredientEntity> ingredients;
    private Integer readyInMinutes;
    private List<InstructionEntity> instructions;
    private String imageUrl;
    private String imageSourceUrl;
    private String summary;

    public SavedRecipeEntity() {}
    public SavedRecipeEntity(String userId, Integer recipeId, String name, List<IngredientEntity> ingredients, Integer readyInMinutes, List<InstructionEntity> instructions, String imageUrl, String imageSourceUrl, String summary) {
        this.primaryKey = new SavedRecipeKey(userId, recipeId);
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.readyInMinutes = readyInMinutes;
        this.instructions = new ArrayList<>(instructions);
        this.imageUrl = imageUrl;
        this.imageSourceUrl = imageSourceUrl;
        this.summary = summary;
    }

    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return primaryKey != null ? primaryKey.getUserId() : null;
    }
    public void setUserId(String userId) {
        if (primaryKey == null)
            primaryKey = new SavedRecipeKey();
        primaryKey.setUserId(userId);
    }
    @DynamoDBRangeKey(attributeName = "recipeId")
    public Integer getRecipeId() {
        return primaryKey != null ? primaryKey.getRecipeId() : null;
    }
    public void setRecipeId(Integer recipeId) {
        if (primaryKey == null)
            primaryKey = new SavedRecipeKey();
        primaryKey.setRecipeId(recipeId);
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "ingredients")
    public List<IngredientEntity> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientEntity> ingredients) {
        this.ingredients = ingredients;
    }

    @DynamoDBAttribute(attributeName = "readyInMinutes")
    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    @DynamoDBAttribute(attributeName = "instructions")
    public List<InstructionEntity> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<InstructionEntity> instructions) {
        this.instructions = instructions;
    }

    @DynamoDBAttribute(attributeName = "imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @DynamoDBAttribute(attributeName = "imageSourceUrl")
    public String getImageSourceUrl() {
        return imageSourceUrl;
    }

    public void setImageSourceUrl(String imageSourceUrl) {
        this.imageSourceUrl = imageSourceUrl;
    }

    @DynamoDBAttribute(attributeName = "summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
