package com.kenzie.appserver.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.*;
import com.kenzie.appserver.service.model.*;
import com.kenzie.appserver.util.QueryUtility;
import com.kenzie.capstone.service.model.recipe.proxy.ValidIngredientQueryParam;
import com.kenzie.capstone.service.model.recipe.proxy.ValidRecipeQueryParam;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.*;

@IntegrationTest

public class RecipeControllerTest {
    @Autowired
    private MockMvc mvc;
    private QueryUtility queryUtility;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();
    @BeforeEach
    public void setup() {
        this.queryUtility = new QueryUtility(mvc);
    }
    /*****************************
     * GET /recipes              *
     ****************************/
    @Test
    public void getRecipes_withValidFilterCriteria_returnsRecipesList() throws Exception {
        Map<String, String> validQueryParams = new HashMap<>();
        validQueryParams.put(ValidRecipeQueryParam.QUERY.getValidQueryParamKey(), "pizza");
        validQueryParams.put(ValidRecipeQueryParam.CUISINE.getValidQueryParamKey(), "African");
        validQueryParams.put(ValidRecipeQueryParam.DIET.getValidQueryParamKey(), "Ketogenic");
        validQueryParams.put(ValidRecipeQueryParam.ADD_RECIPE_INFO.getValidQueryParamKey(), "true");
        validQueryParams.put(ValidRecipeQueryParam.FILL_INGREDIENTS.getValidQueryParamKey(), "false");
        validQueryParams.put(ValidRecipeQueryParam.SORT.getValidQueryParamKey(), "meta-score");
        validQueryParams.put(ValidRecipeQueryParam.SORT_DIRECTION.getValidQueryParamKey(), "asc");

        queryUtility.recipeServiceClient.getRecipes(validQueryParams)
                .andExpect(status().isOk());



    }
    @Test
    public void getRecipes_withInvalidFilterCriteria_badRequest() throws Exception {
        Map<String, String> invalidQueryParams = new HashMap<>();
        invalidQueryParams.put("invalidFilterCriteria", "pizza");
        invalidQueryParams.put(ValidRecipeQueryParam.CUISINE.getValidQueryParamKey(), "African");
        invalidQueryParams.put(ValidRecipeQueryParam.DIET.getValidQueryParamKey(), "Ketogenic");
        invalidQueryParams.put(ValidRecipeQueryParam.ADD_RECIPE_INFO.getValidQueryParamKey(), "true");
        invalidQueryParams.put(ValidRecipeQueryParam.FILL_INGREDIENTS.getValidQueryParamKey(), "false");
        invalidQueryParams.put(ValidRecipeQueryParam.SORT.getValidQueryParamKey(), "meta-score");
        invalidQueryParams.put(ValidRecipeQueryParam.SORT_DIRECTION.getValidQueryParamKey(), "asc");

        queryUtility.recipeServiceClient.getRecipes(invalidQueryParams)
                .andExpect(status().isBadRequest());
    }

    /*****************************
     * GET /ingredients          *
     ****************************/
    @Test
    public void getIngredients_withValidFilterCriteria_returnsRecipesList() throws Exception {
        Map<String, String> validQueryParams = new HashMap<>();
        validQueryParams.put(ValidIngredientQueryParam.QUERY.getValidQueryParamKey(), "apple");
        validQueryParams.put(ValidIngredientQueryParam.ADD_CHILDREN.getValidQueryParamKey(), "true");
        validQueryParams.put(ValidIngredientQueryParam.META_INFORMATION.getValidQueryParamKey(), "true");

        queryUtility.recipeServiceClient.getIngredients(validQueryParams)
                .andExpect(status().isOk());



    }
    @Test
    public void getIngredients_withInvalidFilterCriteria_badRequest() throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidIngredientQueryParam.QUERY.getValidQueryParamKey(), "apple");
        queryParams.put("invalidKey", "true");
        queryParams.put(ValidIngredientQueryParam.META_INFORMATION.getValidQueryParamKey(), "true");

        queryUtility.recipeServiceClient.getIngredients(queryParams)
                .andExpect(status().isBadRequest());
    }
    /*****************************
     * GET /recipes              *
     ****************************/
    @Test
    public void getRecipeById_validRecipeId_returnsRecipe() throws Exception{
        String validRecipeFromDocumentation = "716429";

        queryUtility.recipeServiceClient.getRecipeById(validRecipeFromDocumentation)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("id").exists(),
                        jsonPath("name").exists(),
                        jsonPath("ingredients").exists(),
                        jsonPath("readyInMinutes").exists(),
                        jsonPath("instructions").exists(),
                        jsonPath("imageUrl").exists(),
                        jsonPath("imageSourceUrl").exists(),
                        jsonPath("summary").exists()
                );
    }
    @Test
    public void getRecipeById_invalidId_notFound() throws Exception {
        String invalidId = "0";

        queryUtility.recipeServiceClient.getRecipeById(invalidId)
                .andExpect(status().isNotFound());
    }
    /*********************************************
     * GET /recipes/fridgeItems/{userId}         *
     ********************************************/

    @Test
    public void getRecipesUsingFridgeItems_withValidUserIdMaximizeIngredientsTrue_returnsRecipeList() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();

        String itemName = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();

        queryUtility.profileServiceClient.createProfile(userId, name, email, new ArrayList<>());

        AddFridgeItemRequest addFridgeItemRequest = new AddFridgeItemRequest(
                userId,
                itemName,
                purchaseDate,
                quantity,
                unitMeasurement,
                imageFileName);

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(itemName),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(quantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );

        queryUtility.recipeServiceClient.getRecipesUsingFridgeItems(userId, true)
                .andExpectAll(
                        status().isOk()
                );

    }

    @Test
    public void getRecipesUsingFridgeItems_maximizeIngredientsFalse_returnsRecipeList() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();

        String itemName = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();

        queryUtility.profileServiceClient.createProfile(userId, name, email, new ArrayList<>());

        AddFridgeItemRequest addFridgeItemRequest = new AddFridgeItemRequest(
                userId,
                itemName,
                purchaseDate,
                quantity,
                unitMeasurement,
                imageFileName);

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(itemName),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(quantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );

        queryUtility.recipeServiceClient.getRecipesUsingFridgeItems(userId, false)
                .andExpectAll(
                        status().isOk()
                );
    }
    /*************************************
     * GET /fridgeItems/{userId}         *
     ************************************/
    @Test
    public void getAllFridgeItemsByUserId_validUserId_returnsFridgeItems() throws Exception {
        // Create test data
        String userId = mockNeat.users().valStr();

        AddFridgeItemRequest request1 = new AddFridgeItemRequest(userId, "Sugar",
                LocalDateTime.now().toString(), 3.0, "cup", mockNeat.urls().valStr());
        AddFridgeItemRequest request2 = new AddFridgeItemRequest(userId, "Honey",
                LocalDateTime.now().toString(), 2.0, "ounces", mockNeat.urls().valStr());

        // Add fridge items to the server
        queryUtility.recipeServiceClient.addFridgeItem(request1)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(request1.getName()),
                        jsonPath("purchaseDate").value(request1.getPurchaseDate()),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(request1.getQuantity()),
                        jsonPath("unitMeasurement").value(request1.getUnitMeasurement()),
                        jsonPath("imageFileName").value(request1.getImageFileName())
                );
        queryUtility.recipeServiceClient.addFridgeItem(request2)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(request2.getName()),
                        jsonPath("purchaseDate").value(request2.getPurchaseDate()),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(request2.getQuantity()),
                        jsonPath("unitMeasurement").value(request2.getUnitMeasurement()),
                        jsonPath("imageFileName").value(request2.getImageFileName())
                );
        // Get all fridge items for the user and verify response status is ok
        ResultActions result = queryUtility.recipeServiceClient.getAllFridgeItemsByUserId(userId)
                .andExpect(status().isOk());

        // retrieve the body from the response as a json string
        String responseBody = result.andReturn().getResponse().getContentAsString();

        // convert the json string to a List of FridgeItemResponses
        List<FridgeItemResponse> fridgeItemResponses = mapper.readValue(responseBody,
                new TypeReference<List<FridgeItemResponse>>() {});

        // Iterate through the list of FridgeItemResponses
        for (FridgeItemResponse response : fridgeItemResponses) {
            // verify that all items belong to the given user
            Assertions.assertEquals(response.getUserId(), userId);
            // verify that each item has the correct quantity and unit of measurement
            if (response.getName().equals(request1.getName())) {
                Assertions.assertEquals(request1.getQuantity().intValue(), response.getQuantity().intValue());
                Assertions.assertEquals(request1.getUnitMeasurement(), response.getUnitMeasurement());
                Assertions.assertEquals(request1.getPurchaseDate(), response.getPurchaseDate());
                Assertions.assertEquals(request1.getImageFileName(), response.getImageFileName());
            } else if (response.getName().equals(request2.getName())) {
                Assertions.assertEquals(request2.getQuantity().intValue(), response.getQuantity().intValue());
                Assertions.assertEquals(request2.getUnitMeasurement(), response.getUnitMeasurement());
                Assertions.assertEquals(request2.getPurchaseDate(), response.getPurchaseDate());
                Assertions.assertEquals(request2.getImageFileName(), response.getImageFileName());
            }
        }
    }
    /*****************************
     * POST /fridgeItems         *
     ****************************/
    @Test
    public void addFridgeItem_itemDoesNotExist_addsItem() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();

        AddFridgeItemRequest addFridgeItemRequest = new AddFridgeItemRequest(
                userId,
                name,
                purchaseDate,
                quantity,
                unitMeasurement,
                imageFileName);

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(quantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );

    }
    @Test
    public void addFridgeItem_fridgeItemAlreadyExists_sendsBadRequest() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();

        AddFridgeItemRequest addFridgeItemRequest = new AddFridgeItemRequest(
                userId,
                name,
                purchaseDate,
                quantity,
                unitMeasurement,
                imageFileName);

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(quantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isBadRequest()
                );

    }
    /*****************************
     * PUT /fridgeItems          *
     ****************************/
    @Test
    public void updateFridgeItem_existingItem_updatesItem() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();

        AddFridgeItemRequest addFridgeItemRequest = new AddFridgeItemRequest(
                userId,
                name,
                purchaseDate,
                quantity,
                unitMeasurement,
                imageFileName);

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(quantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );

        Double updatedQuantity = mockNeat.doubles().range(0.01, 10.0).val();
        UpdateFridgeItemRequest updateFridgeItemRequest = new UpdateFridgeItemRequest(
                userId,
                name,
                purchaseDate,
                updatedQuantity,
                unitMeasurement,
                imageFileName
        );

        queryUtility.recipeServiceClient.updateFridgeItem(updateFridgeItemRequest)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(updatedQuantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );
    }

    @Test
    public void updateFridgeItem_notExistingItem_notFound() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();
        Double updatedQuantity = mockNeat.doubles().range(0.01, 10.0).val();
        UpdateFridgeItemRequest updateFridgeItemRequest = new UpdateFridgeItemRequest(
                userId,
                name,
                purchaseDate,
                updatedQuantity,
                unitMeasurement,
                imageFileName
        );

        queryUtility.recipeServiceClient.updateFridgeItem(updateFridgeItemRequest)
                .andExpectAll(
                        status().isNotFound()
                );
    }
    /*****************************
     * DELETE /fridgeItems       *
     ****************************/

    @Test
    public void removeFridgeItem_validUserId_removesItem() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.strings().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().val();
        String imageFileName = mockNeat.urls().valStr();

        AddFridgeItemRequest addFridgeItemRequest = new AddFridgeItemRequest(
                userId,
                name,
                purchaseDate,
                quantity,
                unitMeasurement,
                imageFileName);

        queryUtility.recipeServiceClient.addFridgeItem(addFridgeItemRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("purchaseDate").value(purchaseDate),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("quantity").value(quantity),
                        jsonPath("unitMeasurement").value(unitMeasurement),
                        jsonPath("imageFileName").value(imageFileName)
                );

        RemoveFridgeItemRequest removeFridgeItemRequest = new RemoveFridgeItemRequest(
                userId,
                name
        );

        queryUtility.recipeServiceClient.removeFridgeItem(removeFridgeItemRequest)
                .andExpectAll(status().isOk(),
        jsonPath("userId").value(userId),
                jsonPath("name").value(name));




    }
    @Test
    public void removeFridgeItem_itemDoesNotExist_notFound() throws Exception {
FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(mockNeat.users().valStr());
        fridgeItem.setName(mockNeat.strings().valStr());
        fridgeItem.setPurchaseDate(mockNeat.localDates().valStr());
        fridgeItem.setQuantity(mockNeat.doubles().range(0.01, 10.0).val());
        fridgeItem.setUnitMeasurement(mockNeat.strings().val());
        fridgeItem.setLastUpdated(mockNeat.localDates().valStr());

        RemoveFridgeItemRequest removeFridgeItemRequest = new RemoveFridgeItemRequest(
                fridgeItem.getUserId(),
                fridgeItem.getName()
        );

        queryUtility.recipeServiceClient.removeFridgeItem(removeFridgeItemRequest)
                .andExpectAll(status().isNotFound());
    }
    /**********************************
     * GET /savedRecipes/{userId}     *
     *********************************/
    @Test
    public void getSavedRecipesByUserId_returnsSavedRecipes() throws Exception {
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        String name = mockNeat.words().valStr();
        List<Ingredient> ingredients = new ArrayList<>();
        Integer readyInMinutes = mockNeat.ints().lowerBound(1).val();
        List<Instruction> instructions = new ArrayList<>();
        String imageUrl = mockNeat.urls().valStr();
        String imageSourceUrl = mockNeat.urls().valStr();
        String summary = mockNeat.strings().valStr();

        AddSavedRecipeRequest addSavedRecipeRequest = new AddSavedRecipeRequest(
                userId,
                recipeId,
                name,
                ingredients,
                readyInMinutes,
                instructions,
                imageUrl,
                imageSourceUrl,
                summary
        );

        queryUtility.recipeServiceClient.addSavedRecipe(addSavedRecipeRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("recipeId").value(recipeId),
                        jsonPath("name").value(name),
                        jsonPath("ingredients").exists(),
                        jsonPath("ingredients.length()").value(ingredients.size()),
                        jsonPath("readyInMinutes").value(readyInMinutes),
                        jsonPath("instructions").exists(),
                        jsonPath("instructions.length()").value(instructions.size()),
                        jsonPath("imageUrl").value(imageUrl),
                        jsonPath("imageSourceUrl").value(imageSourceUrl),
                        jsonPath("summary").value(summary)
                );
        queryUtility.recipeServiceClient.getSavedRecipesByUserId(userId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].userId").value(userId),
                        jsonPath("$[0].recipeId").value(recipeId),
                        jsonPath("$[0].name").value(name),
                        jsonPath("$[0].ingredients").exists(),
                        jsonPath("$[0].ingredients.length()").value(ingredients.size()),
                        jsonPath("$[0].readyInMinutes").value(readyInMinutes),
                        jsonPath("$[0].instructions").exists(),
                        jsonPath("$[0].instructions.length()").value(instructions.size()),
                        jsonPath("$[0].imageUrl").value(imageUrl),
                        jsonPath("$[0].imageSourceUrl").value(imageSourceUrl),
                        jsonPath("$[0].summary").value(summary)
                );
    }

    /********************************************
     * GET /savedRecipes/{userId}/{recipeId}    *
     *******************************************/
    @Test
    public void getSavedRecipeForUserByRecipeId_savedRecipeExists_returnsSavedRecipe() throws Exception{
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        String name = mockNeat.words().valStr();
        List<Ingredient> ingredients = new ArrayList<>();
        Integer readyInMinutes = mockNeat.ints().lowerBound(1).val();
        List<Instruction> instructions = new ArrayList<>();
        String imageUrl = mockNeat.urls().valStr();
        String imageSourceUrl = mockNeat.urls().valStr();
        String summary = mockNeat.strings().valStr();

        AddSavedRecipeRequest addSavedRecipeRequest = new AddSavedRecipeRequest(
                userId,
                recipeId,
                name,
                ingredients,
                readyInMinutes,
                instructions,
                imageUrl,
                imageSourceUrl,
                summary
        );

        queryUtility.recipeServiceClient.addSavedRecipe(addSavedRecipeRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("recipeId").value(recipeId),
                        jsonPath("name").value(name),
                        jsonPath("ingredients").exists(),
                        jsonPath("ingredients.length()").value(ingredients.size()),
                        jsonPath("readyInMinutes").value(readyInMinutes),
                        jsonPath("instructions").exists(),
                        jsonPath("instructions.length()").value(instructions.size()),
                        jsonPath("imageUrl").value(imageUrl),
                        jsonPath("imageSourceUrl").value(imageSourceUrl),
                        jsonPath("summary").value(summary)
                );

        queryUtility.recipeServiceClient.getSavedRecipeForUserByRecipeId(userId, recipeId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("userId").value(userId),
                        jsonPath("recipeId").value(recipeId),
                        jsonPath("name").value(name),
                        jsonPath("ingredients").exists(),
                        jsonPath("ingredients.length()").value(ingredients.size()),
                        jsonPath("readyInMinutes").value(readyInMinutes),
                        jsonPath("instructions").exists(),
                        jsonPath("instructions.length()").value(instructions.size()),
                        jsonPath("imageUrl").value(imageUrl),
                        jsonPath("imageSourceUrl").value(imageSourceUrl),
                        jsonPath("summary").value(summary)
                );
    }
    @Test
    public void getSavedRecipeForUserByRecipeId_savedRecipeDoesNotExist_notFound() throws Exception{
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();

        queryUtility.recipeServiceClient.getSavedRecipeForUserByRecipeId(userId, recipeId)
                .andExpect(status().isNotFound());
    }

    /*****************************
     * POST /savedRecipes        *
     ****************************/
    @Test
    public void addSavedRecipe_savedRecipeDoesNotAlreadyExist_addsSavedRecipe() throws Exception {
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient(1, "Egg", 2.0, "pcs", "egg.jpg"),
                new Ingredient(2, "Flour", 200.0, "g", "flour.jpg")
        );
        List<Instruction> instructions = Arrays.asList(
                new Instruction(1, "Crack the eggs."),
                new Instruction(2, "Mix the flour.")
        );
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        String name = mockNeat.words().valStr();
        Integer readyInMinutes = mockNeat.ints().lowerBound(1).val();
        String imageUrl = mockNeat.urls().valStr();
        String imageSourceUrl = mockNeat.urls().valStr();
        String summary = mockNeat.strings().valStr();

        AddSavedRecipeRequest request = new AddSavedRecipeRequest(
                userId,
                recipeId,
                name,
                ingredients,
                readyInMinutes,
                instructions,
                imageUrl,
                imageSourceUrl,
                summary
        );
        // Add saved recipe for the given user and verify item was created
        ResultActions result = queryUtility.recipeServiceClient.addSavedRecipe(request)
                .andExpectAll(status().isCreated(),
                        jsonPath("userId").value(request.getUserId()),
                        jsonPath("recipeId").value(request.getRecipeId()),
                        jsonPath("name").value(request.getName()),
                        jsonPath("ingredients[0].id").value(ingredients.get(0).getId()),
                        jsonPath("ingredients[0].name").value(ingredients.get(0).getName()),
                        jsonPath("ingredients[0].quantity").value(ingredients.get(0).getQuantity()),
                        jsonPath("ingredients[0].unitMeasurement").value(ingredients.get(0).getUnitMeasurement()),
                        jsonPath("ingredients[0].imageFileName").value(ingredients.get(0).getImageFileName()),
                        jsonPath("ingredients[1].id").value(ingredients.get(1).getId()),
                        jsonPath("ingredients[1].name").value(ingredients.get(1).getName()),
                        jsonPath("ingredients[1].quantity").value(ingredients.get(1).getQuantity()),
                        jsonPath("ingredients[1].unitMeasurement").value(ingredients.get(1).getUnitMeasurement()),
                        jsonPath("ingredients[1].imageFileName").value(ingredients.get(1).getImageFileName()),
                        jsonPath("readyInMinutes").value(request.getReadyInMinutes()),
                        jsonPath("instructions[0].number").value(instructions.get(0).getNumber()),
                        jsonPath("instructions[0].step").value(instructions.get(0).getStep()),
                        jsonPath("instructions[1].number").value(instructions.get(1).getNumber()),
                        jsonPath("instructions[1].step").value(instructions.get(1).getStep()),
                        jsonPath("ingredients").exists(),
                        jsonPath("readyInMinutes").value(request.getReadyInMinutes()),
                        jsonPath("instructions").exists(),
                        jsonPath("imageUrl").value(request.getImageUrl()),
                        jsonPath("imageSourceUrl").value(request.getImageSourceUrl()),
                        jsonPath("summary").value(request.getSummary()));
    }

    @Test
    public void addSavedRecipe_savedRecipeExists_badRequest() throws Exception {
        // GIVEN : AddSavedRecipeRequest
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();

        AddSavedRecipeRequest request = new AddSavedRecipeRequest(userId, recipeId,
                "TestName", Collections.emptyList(), 15, Collections.emptyList(), "recipe1.jpg",
                "https://testSavedRecipes.com/recipe1", "A Saved recipe No. 1");

        // Add a SavedRecipe for the given user and verify item was created
        queryUtility.recipeServiceClient.addSavedRecipe(request)
                .andExpectAll(status().isCreated(),
                        jsonPath("userId").value(request.getUserId()),
                        jsonPath("recipeId").value(request.getRecipeId()));

        // WHEN: Adding the same SavedRecipe
        // THEN: A BadRequest - HttpStatus 400 is expected
        queryUtility.recipeServiceClient.addSavedRecipe(request)
                .andExpectAll(status().isBadRequest());
    }

    /*****************************
     * DELETE /savedRecipes      *
     ****************************/

    @Test
    public void removeSavedRecipe_recipeExists_removesRecipe() throws Exception {
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        String name = mockNeat.words().valStr();
        List<Ingredient> ingredients = new ArrayList<>();
        Integer readyInMinutes = mockNeat.ints().lowerBound(1).val();
        List<Instruction> instructions = new ArrayList<>();
        String imageUrl = mockNeat.urls().valStr();
        String imageSourceUrl = mockNeat.urls().valStr();
        String summary = mockNeat.strings().valStr();

        AddSavedRecipeRequest addSavedRecipeRequest = new AddSavedRecipeRequest(
                userId,
                recipeId,
                name,
                ingredients,
                readyInMinutes,
                instructions,
                imageUrl,
                imageSourceUrl,
                summary
        );

        queryUtility.recipeServiceClient.addSavedRecipe(addSavedRecipeRequest)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("recipeId").value(recipeId),
                        jsonPath("name").value(name),
                        jsonPath("ingredients").exists(),
                        jsonPath("ingredients.length()").value(ingredients.size()),
                        jsonPath("readyInMinutes").value(readyInMinutes),
                        jsonPath("instructions").exists(),
                        jsonPath("instructions.length()").value(instructions.size()),
                        jsonPath("imageUrl").value(imageUrl),
                        jsonPath("imageSourceUrl").value(imageSourceUrl),
                        jsonPath("summary").value(summary)
                );

        RemoveSavedRecipeRequest removeSavedRecipeRequest = new RemoveSavedRecipeRequest(
                userId,
                recipeId
        );

        queryUtility.recipeServiceClient.removeSavedRecipe(removeSavedRecipeRequest)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("userId").value(userId),
                        jsonPath("recipeId").value(recipeId),
                        jsonPath("name").value(name),
                        jsonPath("ingredients").exists(),
                        jsonPath("ingredients.length()").value(ingredients.size()),
                        jsonPath("readyInMinutes").value(readyInMinutes),
                        jsonPath("instructions").exists(),
                        jsonPath("instructions.length()").value(instructions.size()),
                        jsonPath("imageUrl").value(imageUrl),
                        jsonPath("imageSourceUrl").value(imageSourceUrl),
                        jsonPath("summary").value(summary)
                );

        queryUtility.recipeServiceClient.getSavedRecipesByUserId(userId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );



    }

    @Test
    public void removeSavedRecipe_recipeDoesNotExists_notFound() throws Exception {
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        RemoveSavedRecipeRequest removeSavedRecipeRequest = new RemoveSavedRecipeRequest(
                userId,
                recipeId
        );
        queryUtility.recipeServiceClient.removeSavedRecipe(removeSavedRecipeRequest)
                .andExpectAll(status().isNotFound());



    }
}