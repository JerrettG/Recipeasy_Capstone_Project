package com.kenzie.appserver.service.util;

import com.kenzie.appserver.repositories.model.FridgeItemEntity;
import com.kenzie.appserver.repositories.model.IngredientEntity;
import com.kenzie.appserver.repositories.model.InstructionEntity;
import com.kenzie.appserver.repositories.model.SavedRecipeEntity;
import com.kenzie.appserver.service.model.Ingredient;
import com.kenzie.appserver.service.model.Instruction;
import com.kenzie.appserver.service.model.SavedRecipe;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyIngredientResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyInstructionResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyRecipeResponse;
import net.andreinc.mockneat.MockNeat;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    private static final MockNeat mockNeat = MockNeat.threadLocal();

    public static List<ProxyRecipeResponse> createMockListOfProxyRecipeResponse(int size) {
        List<ProxyRecipeResponse> responses = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            responses.add(createMockProxyRecipeResponse());
        }

        return responses;
    }
    public static ProxyRecipeResponse createMockProxyRecipeResponse() {

        return new ProxyRecipeResponse(
                mockNeat.ints().val(),
                mockNeat.strings().valStr(),
                createMockListOfProxyIngredientResponse(),
                mockNeat.ints().val(),
                createMockListOfProxyInstructionResponses(),
                mockNeat.urls().valStr(),
                mockNeat.strings().valStr()

        );
    }
    public static List<ProxyIngredientResponse> createMockListOfProxyIngredientResponse() {
        List<ProxyIngredientResponse> responses = new ArrayList<>();

        for (int i = 0; i < mockNeat.ints().range(0,5).val();i++) {
            responses.add(new ProxyIngredientResponse(
                    mockNeat.ints().val(),
                    mockNeat.strings().valStr(),
                    mockNeat.doubles().range(0.05, 10000.0).val(),
                    mockNeat.strings().valStr(),
                    mockNeat.strings().valStr()
            ));
        }
        return responses;
    }
    public static List<ProxyInstructionResponse> createMockListOfProxyInstructionResponses() {
        List<ProxyInstructionResponse> responses = new ArrayList<>();

        for (int i = 0; i < mockNeat.ints().range(0,5).val();i++) {
            responses.add(new ProxyInstructionResponse(
                    mockNeat.ints().val(),
                    mockNeat.strings().valStr()
            ));
        }
        return responses;
    }

    public static List<FridgeItemEntity> createMockListOfFridgeItemEntity(String userId) {
        List<FridgeItemEntity> entities = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
           entities.add(createMockFridgeItemEntity(userId));
        }
        return entities;
    }

    public static FridgeItemEntity createMockFridgeItemEntity(String userId) {
        return new FridgeItemEntity(
                userId,
                mockNeat.strings().valStr(),
                mockNeat.localDates().valStr(),
                mockNeat.localDates().valStr(),
                mockNeat.doubles().range(0.01, 10.0).val(),
                mockNeat.strings().valStr(),
                mockNeat.urls().valStr());
    }
    public static FridgeItemEntity createMockCustomerFridgeEntity(String userId, String lastUpdated) {
        return new FridgeItemEntity(
                userId,
                mockNeat.strings().valStr(),
                mockNeat.localDates().valStr(),
                lastUpdated,
                mockNeat.doubles().range(0.01, 10.0).val(),
                mockNeat.strings().valStr(),
                mockNeat.urls().valStr()
        );
    }

    public static List<SavedRecipe> createMockListOfSavedRecipe(String userId) {
        List<SavedRecipe> savedRecipes = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
            savedRecipes.add(createMockSavedRecipe(userId));
        }
        return savedRecipes;
    }

    public static List<SavedRecipeEntity> createMockListOfSavedRecipeEntity(String userId) {
        List<SavedRecipeEntity> savedRecipes = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
            savedRecipes.add(createMockSavedRecipeEntity(userId));
        }
        return savedRecipes;
    }

    public static SavedRecipe createMockSavedRecipe(String userId) {
        return new SavedRecipe(
                userId,
                mockNeat.ints().lowerBound(1).val(),
                mockNeat.names().valStr(),
                createMockListOfIngredient(),
                mockNeat.ints().lowerBound(1).val(),
                createMockListOfInstruction(),
                mockNeat.urls().valStr(),
                mockNeat.urls().valStr(),
                mockNeat.strings().valStr()
        );
    }
    public static SavedRecipeEntity createMockSavedRecipeEntity(String userId) {
        return new SavedRecipeEntity(
                userId,
                mockNeat.ints().lowerBound(1).val(),
                mockNeat.names().valStr(),
                createMockListOfIngredientEntity(),
                mockNeat.ints().lowerBound(1).val(),
                createMockListOfInstructionEntity(),
                mockNeat.urls().valStr(),
                mockNeat.urls().valStr(),
                mockNeat.strings().valStr()
        );
    }

    private static List<Instruction> createMockListOfInstruction() {
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
            instructions.add(createMockInstruction());
        }
        return instructions;
    }
    private static List<InstructionEntity> createMockListOfInstructionEntity() {
        List<InstructionEntity> instructions = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
            instructions.add(createMockInstructionEntity());
        }
        return instructions;
    }
    private static Instruction createMockInstruction() {
        return new Instruction(
                mockNeat.ints().val(),
                mockNeat.strings().valStr()
        );
    }
    private static InstructionEntity createMockInstructionEntity() {
        return new InstructionEntity(
                mockNeat.ints().val(),
                mockNeat.strings().valStr()
        );
    }

    private static List<Ingredient> createMockListOfIngredient() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
            ingredients.add(createMockIngredient());
        }
        return ingredients;
    }
    private static List<IngredientEntity> createMockListOfIngredientEntity() {
        List<IngredientEntity> ingredients = new ArrayList<>();
        for (int i = 0; i < mockNeat.ints().range(0, 5).val(); i++) {
            ingredients.add(createMockIngredientEntity());
        }
        return ingredients;
    }


    private static Ingredient createMockIngredient() {
        return new Ingredient(
                mockNeat.ints().val(),
                mockNeat.strings().valStr(),
                mockNeat.doubles().range(0.05, 10000.0).val(),
                mockNeat.strings().valStr(),
                mockNeat.strings().valStr()
        );
    }
    private static IngredientEntity createMockIngredientEntity() {
        return new IngredientEntity(
                mockNeat.ints().val(),
                mockNeat.strings().valStr(),
                mockNeat.doubles().range(0.05, 10000.0).val(),
                mockNeat.strings().valStr(),
                mockNeat.strings().valStr()
        );
    }

}
