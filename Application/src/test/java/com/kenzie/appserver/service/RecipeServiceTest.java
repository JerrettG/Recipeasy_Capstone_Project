package com.kenzie.appserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.caching.CacheClient;
import com.kenzie.appserver.caching.InMemoryCacheStore;
import com.kenzie.appserver.exceptions.FridgeItemAlreadyExistsException;
import com.kenzie.appserver.exceptions.FridgeItemNotFoundException;
import com.kenzie.appserver.exceptions.SavedRecipeAlreadyExistsException;
import com.kenzie.appserver.exceptions.SavedRecipeNotFoundException;
import com.kenzie.appserver.repositories.FridgeItemRepository;

import com.kenzie.appserver.repositories.SavedRecipeRepository;
import com.kenzie.appserver.repositories.model.*;
import com.kenzie.appserver.service.model.*;
import com.kenzie.appserver.service.util.TestUtil;
import com.kenzie.appserver.util.ModelConverter;
import com.kenzie.capstone.service.client.ApiGatewayException;
import com.kenzie.capstone.service.client.ProfileServiceClient;
import com.kenzie.capstone.service.client.RecipeApiProxyClient;
import com.kenzie.capstone.service.model.recipe.proxy.*;

import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


 public class RecipeServiceTest {
    private final MockNeat mockNeat = MockNeat.threadLocal();
    private ObjectMapper objectMapper;
    private FridgeItemRepository fridgeItemRepository;
    private RecipeService recipeService;
    private RecipeApiProxyClient recipeApiProxyClient;
    private ProfileServiceClient profileServiceClient;
    private CacheClient cacheClient;
    private InMemoryCacheStore inMemoryCache;
    private SavedRecipeRepository savedRecipeRepository;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        fridgeItemRepository = mock(FridgeItemRepository.class);
        recipeApiProxyClient = mock(RecipeApiProxyClient.class);
        savedRecipeRepository = mock(SavedRecipeRepository.class);
        profileServiceClient = mock(ProfileServiceClient.class);
        cacheClient = mock(CacheClient.class);
        inMemoryCache = mock(InMemoryCacheStore.class);
        recipeService = new RecipeService(
                recipeApiProxyClient, fridgeItemRepository,
                savedRecipeRepository, profileServiceClient,
                cacheClient, inMemoryCache
        );
    }

    /***************************************************
     * recipeService.getRecipes()                      *
     **************************************************/
    @Test
    void getRecipes_withValidParameters_returnsRecipes() {
        // GIVEN valid query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidRecipeQueryParam.DIET.getValidQueryParamKey(), Diet.GLUTEN_FREE.getDiet());

        // Recipe 1
        // Ingredients to be placed on a list for Recipe 1
        ProxyIngredientResponse proxyIngredient1_1 =
                new ProxyIngredientResponse(1, "Flour", 1.0, "cup", "flour.jpg");
        ProxyIngredientResponse proxyIngredient1_2 =
                new ProxyIngredientResponse(2, "Milk",1.0, "cup", "milk.jpg");

        // Instructions to be placed on a list for Recipe 1
        ProxyInstructionResponse proxyInstruction1_1 =
                new ProxyInstructionResponse(1, "Mix flour and milk together in a bowl.");
        ProxyInstructionResponse proxyInstruction1_2 =
                new ProxyInstructionResponse(2, "Pour the mixture into a pan.");

        // List of Ingredients for Recipe 1
        List<ProxyIngredientResponse> proxyIngredientList1 =
                new ArrayList<>(Arrays.asList(proxyIngredient1_1, proxyIngredient1_2));
        // List of Instructions for Recipe 1
        List<ProxyInstructionResponse> proxyInstructions1 =
                new ArrayList<>(Arrays.asList(proxyInstruction1_1, proxyInstruction1_2));


        // ingredients to be placed on a list for Recipe 2
        ProxyIngredientResponse proxyIngredient2_1 =
                new ProxyIngredientResponse(3, "Ground Beef", 1.0, "lb","beef.jpg");
        ProxyIngredientResponse proxyIngredient2_2 =
                new ProxyIngredientResponse(4, "Tomato Sauce",1.0, "lb", "tomato-sauce.jpg");

        // Instructions to be placed on a list for Recipe 2
        ProxyInstructionResponse proxyInstruction2_1 =
                new ProxyInstructionResponse(1, "Cook the ground beef in a " +
                        "skillet until browned.");
        ProxyInstructionResponse proxyInstruction2_2 =
                new ProxyInstructionResponse(2, "Add tomato sauce and simmer for 10 minutes.");

        // List of Ingredients for Recipe 2
        List<ProxyIngredientResponse> proxyIngredientList2 = new ArrayList<>(Arrays.asList(proxyIngredient2_1,
                proxyIngredient2_2));
        // List of Instructions for Recipe 2
        List<ProxyInstructionResponse> proxyInstructions2 = new ArrayList<>(Arrays.asList(proxyInstruction2_1,
                proxyInstruction2_2));

        // Recipes returned by proxyAPI
        ProxyRecipeResponse proxyRecipe1 = new ProxyRecipeResponse(1, "Pancakes",
                proxyIngredientList1, 20, proxyInstructions1,
                "pancakes.jpg", "Pancakes are perfect for breakfast!");
        ProxyRecipeResponse proxyRecipe2 =
                new ProxyRecipeResponse(2, "Spaghetti Bolognese", proxyIngredientList2, 30,
                        proxyInstructions2, "spaghetti.jpg",
                        "This classic Italian dish is perfect for dinner!");

        List<ProxyRecipeResponse> expectedRecipes = new ArrayList<>(Arrays.asList(proxyRecipe1, proxyRecipe2));


        // WHEN getRecipesByComplexSearch method is called
        when(recipeApiProxyClient.getRecipesByComplexSearch(any())).thenReturn(expectedRecipes);


        List<Recipe> actualRecipes = recipeService.getRecipes(queryParams);
        Assertions.assertEquals(actualRecipes.size(), expectedRecipes.size());
        Assertions.assertTrue(actualRecipes.get(0).getId().equals(expectedRecipes.get(0).getId()));
        Assertions.assertTrue(actualRecipes.get(1).getId().equals(expectedRecipes.get(1).getId()));
    }

    @Test
    void getRecipes_withInvalidParameters_throwsException() {
        // GIVEN  invalid query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(null, null);

        // Configure mock object to throw exception
        when(recipeApiProxyClient.getRecipesByComplexSearch(queryParams))
                .thenThrow(new ApiGatewayException("Invalid query parameters"));

        // WHEN / THEN
        assertThrows(ApiGatewayException.class, () -> this.recipeService.getRecipes(queryParams));
    }
    /***************************************************
     * recipeService.getRecipeById()                   *
     **************************************************/
    @Test
    public void getRecipeById_validId_returnsRecipe() {
        String id = mockNeat.uuids().valStr();
        ProxyRecipeResponse expectedRecipe = TestUtil.createMockProxyRecipeResponse();
        when(recipeApiProxyClient.getRecipeById(id)).thenReturn(expectedRecipe);

        Recipe actualRecipe = recipeService.getRecipeById(id);

        assertEquals(expectedRecipe.getId(), actualRecipe.getId());
        assertEquals(expectedRecipe.getName(), actualRecipe.getName());
        assertEquals(expectedRecipe.getReadyInMinutes(), actualRecipe.getReadyInMinutes());
        assertEquals(expectedRecipe.getImageUrl(), actualRecipe.getImageUrl());
        assertEquals(expectedRecipe.getImageSourceUrl(), actualRecipe.getImageSourceUrl());
        assertEquals(expectedRecipe.getSummary(), actualRecipe.getSummary());

        for (int j = 0; j < actualRecipe.getIngredients().size(); j++) {
            ProxyIngredientResponse expectedRecipeIngredient = expectedRecipe.getIngredients().get(j);
            Ingredient actualIngredient = actualRecipe.getIngredients().get(j);
            assertEquals(expectedRecipeIngredient.getId(), actualIngredient.getId());
            assertEquals(expectedRecipeIngredient.getName(), actualIngredient.getName());
            assertEquals(expectedRecipeIngredient.getQuantity(), actualIngredient.getQuantity());
            assertEquals(expectedRecipeIngredient.getUnitMeasurement(), actualIngredient.getUnitMeasurement());
            assertEquals(expectedRecipeIngredient.getImageFileName(), actualIngredient.getImageFileName());
        }
        for (int j = 0; j < actualRecipe.getInstructions().size(); j++) {
            ProxyInstructionResponse expectedRecipeInstruction = expectedRecipe.getInstructions().get(j);
            Instruction actualRecipeInstruction = actualRecipe.getInstructions().get(j);
            assertEquals(expectedRecipeInstruction.getNumber(), actualRecipeInstruction.getNumber());
            assertEquals(expectedRecipeInstruction.getStep(), actualRecipeInstruction.getStep());
        }

    }

    @Test
    public void getRecipeById_invalidId_throwsApiGatewayException() {
        String id = mockNeat.uuids().valStr();
        when(recipeApiProxyClient.getRecipeById(id))
                .thenThrow(new ApiGatewayException("Invalid recipeId"));
    }

    /***************************************************
     * recipeService.getIngredients()                  *
     **************************************************/
    @Test
    void getIngredients_withValidParameters_returnsRecipes() {
        // GIVEN valid query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidIngredientQueryParam.QUERY.getValidQueryParamKey(), "apple");

        // Recipe 1
        // Ingredients to be placed on a list for Recipe 1
        ProxyIngredientResponse proxyIngredient1 =
                new ProxyIngredientResponse(1, "Flour", 1.0, "cup", "flour.jpg");
        ProxyIngredientResponse proxyIngredient2 =
                new ProxyIngredientResponse(2, "Milk",1.0, "cup", "milk.jpg");

        List<ProxyIngredientResponse> expectedIngredients = List.of(proxyIngredient1, proxyIngredient2);

        // WHEN getRecipesByComplexSearch method is called
        when(recipeApiProxyClient.getIngredients(any())).thenReturn(expectedIngredients);


        List<Ingredient> actual = recipeService.getIngredients(queryParams);
        Assertions.assertEquals(expectedIngredients.size(), actual.size());
    }

    @Test
    void getIngredients_withInvalidParameters_throwsApiGatewayException() {
        // GIVEN  invalid query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(null, null);

        // Configure mock object to throw exception
        when(recipeApiProxyClient.getIngredients(queryParams))
                .thenThrow(new ApiGatewayException("Invalid query parameters"));

        // WHEN / THEN
        assertThrows(ApiGatewayException.class, () -> this.recipeService.getIngredients(queryParams));
    }

    /***************************************************
     * recipeService.getRecipesUsingFridgeItems()      *
     **************************************************/
    @Test
    void getRecipesUsingFridgeItems_validUserId_returnsRecipes() throws ExecutionException, InterruptedException {
        String userId = mockNeat.users().valStr();
        boolean maximizeIngredients = true;
        List<ProxyRecipeResponse> proxyRecipeResponses = TestUtil.createMockListOfProxyRecipeResponse(3);

        doReturn(Collections.emptyList()).when(fridgeItemRepository).findAllByUserId(userId);
        doReturn(proxyRecipeResponses).when(recipeApiProxyClient).getRecipesByComplexSearch(any());
        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());
        when(inMemoryCache.getFridgeItemsForUser(anyString())).thenReturn(Optional.empty());

        List<Recipe> recipes = recipeService.getRecipesUsingFridgeItems(userId, maximizeIngredients);

        assertEquals(proxyRecipeResponses.size(), recipes.size());

        for (int i = 0; i < proxyRecipeResponses.size(); i++) {
            ProxyRecipeResponse proxyRecipeResponse = proxyRecipeResponses.get(i);
            Recipe recipe = recipes.get(i);
            assertEquals(proxyRecipeResponse.getId(), recipe.getId());
            assertEquals(proxyRecipeResponse.getName(), recipe.getName());
            assertEquals(proxyRecipeResponse.getReadyInMinutes(), recipe.getReadyInMinutes());
            assertEquals(proxyRecipeResponse.getImageUrl(), recipe.getImageUrl());
            assertEquals(proxyRecipeResponse.getImageSourceUrl(), recipe.getImageSourceUrl());
            assertEquals(proxyRecipeResponse.getSummary(), recipe.getSummary());
            List<ProxyIngredientResponse> proxyIngredientResponses = proxyRecipeResponse.getIngredients();
            List<ProxyInstructionResponse> proxyInstructionResponses = proxyRecipeResponse.getInstructions();
            List<Ingredient> ingredients = recipe.getIngredients();
            List<Instruction> instructions = recipe.getInstructions();

            assertEquals(proxyIngredientResponses.size(), ingredients.size());
            assertEquals(proxyInstructionResponses.size(), instructions.size());

            for (int j = 0; j < proxyIngredientResponses.size(); j++) {
                ProxyIngredientResponse proxyIngredientResponse = proxyIngredientResponses.get(j);
                Ingredient ingredient = ingredients.get(j);
                assertEquals(proxyIngredientResponse.getId(), ingredient.getId());
                assertEquals(proxyIngredientResponse.getName(), ingredient.getName());
                assertEquals(proxyIngredientResponse.getQuantity(), ingredient.getQuantity());
                assertEquals(proxyIngredientResponse.getUnitMeasurement(), ingredient.getUnitMeasurement());
                assertEquals(proxyIngredientResponse.getImageFileName(), ingredient.getImageFileName());
            }
            for (int j = 0; j < proxyInstructionResponses.size(); j++) {
                ProxyInstructionResponse proxyInstructionResponse = proxyInstructionResponses.get(j);
                Instruction instruction = instructions.get(j);
                assertEquals(proxyInstructionResponse.getNumber(), instruction.getNumber());
                assertEquals(proxyInstructionResponse.getStep(), instruction.getStep());
            }
        }

    }

    @Test
    public void getRecipesUsingFridgeItems_nullUserId_throwsIllegalArgumentException() {
        String userId = null;
        boolean maximizeIngredients = true;

        assertThrows(IllegalArgumentException.class, () -> recipeService.getRecipesUsingFridgeItems(userId, maximizeIngredients));

    }

    @Test
    public void getRecipesUsingFridgeItems_blankUserId_throwsIllegalArgumentException() {
        String userId = "       ";
        boolean maximizeIngredients = true;

        assertThrows(IllegalArgumentException.class, () -> recipeService.getRecipesUsingFridgeItems(userId, maximizeIngredients));

    }

    /***************************************************
     * recipeService.getAllFridgeItemsByUserId()       *
     **************************************************/
    @Test
    public void getAllFridgeItemsByUserId_withValidIdCacheMiss_returnsList() {
        // Set up test data
        String userId = "testUser";
        List<FridgeItemEntity> fridgeItemEntities = new ArrayList<>();

        fridgeItemEntities.add(new FridgeItemEntity(userId, "Sugar", LocalDateTime.now().toString(),
                LocalDateTime.now().minusDays(1).toString(), 3.0, "cup", mockNeat.urls().valStr()));

        fridgeItemEntities.add(new FridgeItemEntity(userId, "Honey", LocalDateTime.now().toString(),
                LocalDateTime.now().minusDays(1).toString(), 2.0, "ounces", mockNeat.urls().valStr()));

        // Mock the fridge item repository
        when(fridgeItemRepository.findAllByUserId(userId)).thenReturn(fridgeItemEntities);
        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());
        when(inMemoryCache.getFridgeItemsForUser(anyString())).thenReturn(Optional.empty());
        // Call the getAllFridgeItemsByUserId method
        List<FridgeItem> fridgeItems = recipeService.getAllFridgeItemsByUserId(userId);

        // Assert the results
        assertEquals(2, fridgeItems.size());
        assertEquals("Sugar", fridgeItems.get(0).getName());
        assertEquals("Honey", fridgeItems.get(1).getName());
        assertEquals(3, fridgeItems.get(0).getQuantity());
        assertEquals(2, fridgeItems.get(1).getQuantity());
        assertEquals("cup", fridgeItems.get(0).getUnitMeasurement());
        assertEquals("ounces", fridgeItems.get(1).getUnitMeasurement());
        assertEquals(userId, fridgeItems.get(0).getUserId());
        assertEquals(userId, fridgeItems.get(1).getUserId());
    }
    @Test
    public void getAllFridgeItemsByUserId_withValidIdCacheHit_returnsList() throws JsonProcessingException {
        // Set up test data
        String userId = "testUser";
        List<FridgeItemEntity> fridgeItemEntities = new ArrayList<>();

        fridgeItemEntities.add(new FridgeItemEntity(userId, "Sugar", LocalDateTime.now().minusDays(1).toString(),
                LocalDateTime.now().toString(), 3.0, "cup", mockNeat.urls().valStr()));

        fridgeItemEntities.add(new FridgeItemEntity(userId, "Honey", LocalDateTime.now().minusDays(1).toString(),
                LocalDateTime.now().toString(), 2.0, "ounces", mockNeat.urls().valStr()));

        List<FridgeItem> cachedFridgeItems = Arrays.asList(
                new FridgeItem(
                        userId,
                        fridgeItemEntities.get(0).getName(),
                        fridgeItemEntities.get(0).getPurchaseDate(),
                        fridgeItemEntities.get(0).getLastUpdated(),
                        fridgeItemEntities.get(0).getQuantity(),
                        fridgeItemEntities.get(0).getUnitMeasurement(),
                        fridgeItemEntities.get(0).getImageFileName()
                ),
                new FridgeItem(
                        userId,
                        fridgeItemEntities.get(1).getName(),
                        fridgeItemEntities.get(1).getPurchaseDate(),
                        fridgeItemEntities.get(1).getLastUpdated(),
                        fridgeItemEntities.get(1).getQuantity(),
                        fridgeItemEntities.get(1).getUnitMeasurement(),
                        fridgeItemEntities.get(1).getImageFileName()
                )
        );

        // Mock the fridge item repository
        when(fridgeItemRepository.findAllByUserId(userId)).thenReturn(fridgeItemEntities);
        String jsonFridgeItems = objectMapper.writeValueAsString(fridgeItemEntities);
        when(cacheClient.getValue(anyString())).thenReturn(Optional.of(jsonFridgeItems));
        when(inMemoryCache.getFridgeItemsForUser(anyString())).thenReturn(Optional.of(cachedFridgeItems));
        // Call the getAllFridgeItemsByUserId method
        List<FridgeItem> fridgeItems = recipeService.getAllFridgeItemsByUserId(userId);

        // Assert the results
        assertEquals(2, fridgeItems.size());
        assertEquals("Sugar", fridgeItems.get(0).getName());
        assertEquals("Honey", fridgeItems.get(1).getName());
        assertEquals(3, fridgeItems.get(0).getQuantity());
        assertEquals(2, fridgeItems.get(1).getQuantity());
        assertEquals("cup", fridgeItems.get(0).getUnitMeasurement());
        assertEquals("ounces", fridgeItems.get(1).getUnitMeasurement());
        assertEquals(userId, fridgeItems.get(0).getUserId());
        assertEquals(userId, fridgeItems.get(1).getUserId());
    }

    @Test
    public void getAllFridgeItems_withNullId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeService.getAllFridgeItemsByUserId(null),
                "Expected IllegalArgumentException for null user id");
    }

    @Test
    public void getAllFridgeItems_withEmptyId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeService.getAllFridgeItemsByUserId(""),
                "Expected IllegalArgumentException for empty user id");
    }

    /***************************************************
     * recipeService.addFridgeItem()                   *
     **************************************************/
    @Test
    public void addFridgeItem_validFridgeItem_addsItem() throws FridgeItemAlreadyExistsException {
        FridgeItem fridgeItem = new FridgeItem("user1", "milk", "Tuesday", "Yesterday", 2.0, "gal", mockNeat.urls().valStr());
        when(fridgeItemRepository.existsById(any(FridgeItemKey.class))).thenReturn(false);
        FridgeItem result = recipeService.addFridgeItem(fridgeItem);
        verify(fridgeItemRepository).save(any(FridgeItemEntity.class));
        assertEquals(fridgeItem.getUserId(), result.getUserId());
        assertEquals(fridgeItem.getName(), result.getName());
        assertEquals(fridgeItem.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(fridgeItem.getLastUpdated(), result.getLastUpdated());
        assertEquals(fridgeItem.getQuantity(), result.getQuantity());
        assertEquals(fridgeItem.getUnitMeasurement(), result.getUnitMeasurement());
    }

    @Test
    public void addFridgeItem_fridgeItemAlreadyExists_throwsFridgeItemAlreadyExistsException() {
        FridgeItem fridgeItem = new FridgeItem("user1", "milk", "Tuesday", "Yesterday", 2.0, "gal", mockNeat.urls().valStr());
        when(fridgeItemRepository.existsById(any(FridgeItemKey.class))).thenReturn(true);
        assertThrows(FridgeItemAlreadyExistsException.class, () -> recipeService.addFridgeItem(fridgeItem));
    }

    @Test
    public void addFridgeItem_nullFridgeItem_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> recipeService.addFridgeItem(null),
                "Fridge item cannot be null");
    }

    @Test
    public void addFridgeItem_nullUserId_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem(null, "milk", "Tuesday", "Yesterday", 2.0, "gal", mockNeat.urls().valStr());
        assertThrows(IllegalArgumentException.class, () -> recipeService.addFridgeItem(fridgeItem),
                "User ID cannot be null or empty");
    }

    @Test
    public void addFridgeItem_blankUserId_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem("", "milk", "Tuesday", "Yesterday", 2.0, "gal", mockNeat.urls().valStr());
        assertThrows(IllegalArgumentException.class, () -> recipeService.addFridgeItem(fridgeItem),
                "User ID cannot be null or empty");
    }

    @Test
    public void addFridgeItem_nullItemName_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem("user1", null, "Tuesday", "Yesterday", 2.0, "gal", mockNeat.urls().valStr());
        assertThrows(IllegalArgumentException.class, () -> recipeService.addFridgeItem(fridgeItem),
                "Ingredient cannot be null");
    }

    @Test
    public void addFridgeItem_zeroQuantity_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem("user1", "milk", "Tuesday", "Yesterday", 0.0, "gal", mockNeat.urls().valStr());
        assertThrows(IllegalArgumentException.class, () -> recipeService.addFridgeItem(fridgeItem),
                "Quantity must be greater than zero");
    }

    @Test
    public void addFridgeItem_negativeQuantity_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem("user1", "milk", "Tuesday", "Yesterday", -2.0, "gal", mockNeat.urls().valStr());
        assertThrows(IllegalArgumentException.class, () -> recipeService.addFridgeItem(fridgeItem),
                "Quantity must be greater than zero");
    }

    /***************************************************
     * recipeService.updateFridgeItem()                *
     **************************************************/
    @Test
    public void updateFridgeItem_itemExists_updatesFridgeItem() throws FridgeItemNotFoundException {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        when(fridgeItemRepository.existsById(any(FridgeItemKey.class))).thenReturn(true);

        FridgeItem updatedItem = recipeService.updateFridgeItem(fridgeItem);

        assertEquals(fridgeItem.getUserId(), updatedItem.getUserId());
        assertEquals(fridgeItem.getName(), updatedItem.getName());
        assertEquals(fridgeItem.getPurchaseDate(), updatedItem.getPurchaseDate());
        assertEquals(fridgeItem.getQuantity(), updatedItem.getQuantity());
        assertEquals(fridgeItem.getUnitMeasurement(), updatedItem.getUnitMeasurement());
        assertNotNull(fridgeItem.getLastUpdated());
    }
    @Test
    public void updateFridgeItem_itemDoesNotExist_throwsFridgeItemNotFoundException() {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        when(fridgeItemRepository.existsById(any(FridgeItemKey.class))).thenReturn(false);

        assertThrows(FridgeItemNotFoundException.class, () -> recipeService.updateFridgeItem(fridgeItem));
    }
    @Test
    public void updateFridgeItem_nullItem_throwsIllegalArgumentException() {

        assertThrows(IllegalArgumentException.class, () -> recipeService.updateFridgeItem(null));
    }
    @Test
    public void updateFridgeItem_nullUserId_throwsIllegalArgumentException() {
        String userId = null;
        String name = mockNeat.names().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        assertThrows(IllegalArgumentException.class, () -> recipeService.updateFridgeItem(fridgeItem));
    }
    @Test
    public void updateFridgeItem_blankUserId_throwsIllegalArgumentException() {
        String userId = "   ";
        String name = mockNeat.names().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        assertThrows(IllegalArgumentException.class, () -> recipeService.updateFridgeItem(fridgeItem));
    }
    @Test
    public void updateFridgeItem_nullItemName_throwsIllegalArgumentException() {
        String userId = mockNeat.users().valStr();
        String name = null;
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        assertThrows(IllegalArgumentException.class, () -> recipeService.updateFridgeItem(fridgeItem));
    }
    @Test
    public void updateFridgeItem_blankItemName_throwsIllegalArgumentException() {
        String userId = mockNeat.users().valStr();
        String name = "  ";
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = mockNeat.doubles().range(0.01, 10.0).val();
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        assertThrows(IllegalArgumentException.class, () -> recipeService.updateFridgeItem(fridgeItem));
    }
    @Test
    public void updateFridgeItem_invalidQuantity_throwsIllegalArgumentException() {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String purchaseDate = mockNeat.localDates().valStr();
        Double quantity = -1.0;
        String unitMeasurement = mockNeat.strings().valStr();

        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(userId);
        fridgeItem.setName(name);
        fridgeItem.setPurchaseDate(purchaseDate);
        fridgeItem.setQuantity(quantity);
        fridgeItem.setUnitMeasurement(unitMeasurement);

        assertThrows(IllegalArgumentException.class, () -> recipeService.updateFridgeItem(fridgeItem));
    }

    /***************************************************
     * recipeService.removeFridgeItem()                *
     **************************************************/
    @Test
    public void removeFridgeItem_validFridgeItem_removesFridgeItem() throws FridgeItemNotFoundException {
        // GIVEN a valid FridgeItem
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId("testUserId");
        fridgeItem.setName("testName");

        when(fridgeItemRepository.findById(any(FridgeItemKey.class))).thenReturn(Optional.of(new FridgeItemEntity()));
        // WHEN removeFridgeItem is called
        recipeService.removeFridgeItem(fridgeItem);


        // THEN the FridgeItem is removed
        verify(fridgeItemRepository, times(1)).delete(any(FridgeItemEntity.class));
    }

    @Test
    public void removeFridgeItem_invalidFridgeItem_throwsIllegalArgumentException() {
        // GIVEN an invalid FridgeItem
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId("testUserId");
        fridgeItem.setName("testName");

        when(fridgeItemRepository.findById(any(FridgeItemKey.class))).thenReturn(Optional.empty());
        // WHEN removeFridgeItem is called
        // THEN an FridgeItemNotFoundException is thrown
        assertThrows(FridgeItemNotFoundException.class, () -> recipeService.removeFridgeItem(fridgeItem));
    }

    @Test
    public void removeFridgeItem_nullFridgeItem_throwsIllegalArgumentException() {
        // GIVEN a null FridgeItem
        FridgeItem fridgeItem = null;

        // WHEN removeFridgeItem is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.removeFridgeItem(fridgeItem));
    }

    @Test
    public void removeFridgeItem_nullUserId_throwsIllegalArgumentException() {
        // GIVEN a FridgeItem with a null userId
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(null);
        fridgeItem.setName("testName");

        // WHEN removeFridgeItem is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.removeFridgeItem(fridgeItem));
    }
    @Test
    public void removeFridgeItem_nullItemName_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(mockNeat.users().valStr());
        fridgeItem.setName(null);

        assertThrows(IllegalArgumentException.class, () -> recipeService.removeFridgeItem(fridgeItem));
    }
    @Test
    public void removeFridgeItem_blankItemName_throwsIllegalArgumentException() {
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(mockNeat.users().valStr());
        fridgeItem.setName("");

        assertThrows(IllegalArgumentException.class, () -> recipeService.removeFridgeItem(fridgeItem));

    }

    /***************************************************
     * recipeService.getSavedRecipesByUserId()         *
     **************************************************/
    @Test
    public void getSavedRecipesByUserId_cacheMiss_returnsSavedRecipes() throws JsonProcessingException {
        String userId = mockNeat.users().valStr();
        List<SavedRecipeEntity> expectedRecipes = TestUtil.createMockListOfSavedRecipeEntity(userId);
        String expectedRecipesJson = objectMapper.writeValueAsString(expectedRecipes);
        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());
        when(inMemoryCache.getSavedRecipesForUser(anyString())).thenReturn(Optional.empty());

        List<SavedRecipe> actualRecipes = recipeService.getSavedRecipesByUserId(userId);

        for (int i = 0; i < actualRecipes.size(); i++) {
            SavedRecipeEntity expectedRecipe = expectedRecipes.get(i);
            SavedRecipe actualRecipe = actualRecipes.get(i);
            assertEquals(expectedRecipe.getUserId(), actualRecipe.getUserId());
            assertEquals(expectedRecipe.getRecipeId(), actualRecipe.getRecipeId());
            assertEquals(expectedRecipe.getName(), actualRecipe.getName());
            assertEquals(expectedRecipe.getReadyInMinutes(), actualRecipe.getReadyInMinutes());
            assertEquals(expectedRecipe.getImageUrl(), actualRecipe.getImageUrl());
            assertEquals(expectedRecipe.getImageSourceUrl(), actualRecipe.getImageSourceUrl());
            assertEquals(expectedRecipe.getSummary(), actualRecipe.getSummary());

            for (int j = 0; j < actualRecipe.getIngredients().size(); j++) {
                IngredientEntity expectedRecipeIngredient = expectedRecipe.getIngredients().get(j);
                Ingredient actualIngredient = actualRecipe.getIngredients().get(j);
                assertEquals(expectedRecipeIngredient.getId(), actualIngredient.getId());
                assertEquals(expectedRecipeIngredient.getName(), actualIngredient.getName());
                assertEquals(expectedRecipeIngredient.getQuantity(), actualIngredient.getQuantity());
                assertEquals(expectedRecipeIngredient.getUnitMeasurement(), actualIngredient.getUnitMeasurement());
                assertEquals(expectedRecipeIngredient.getImageFileName(), actualIngredient.getImageFileName());
            }
            for (int j = 0; j < actualRecipe.getInstructions().size(); j++) {
                InstructionEntity expectedRecipeInstruction = expectedRecipe.getInstructions().get(j);
                Instruction actualRecipeInstruction = actualRecipe.getInstructions().get(j);
                assertEquals(expectedRecipeInstruction.getNumber(), actualRecipeInstruction.getNumber());
                assertEquals(expectedRecipeInstruction.getStep(), actualRecipeInstruction.getStep());
            }
        }
    }
    @Test
    public void getSavedRecipesByUserId_cacheHit_returnsSavedRecipes() throws JsonProcessingException {
        String userId = mockNeat.users().valStr();
        List<SavedRecipe> expectedRecipes = TestUtil.createMockListOfSavedRecipe(userId);
        String expectedRecipesJson = objectMapper.writeValueAsString(expectedRecipes);
        when(cacheClient.getValue(anyString())).thenReturn(Optional.of(expectedRecipesJson));
        when(inMemoryCache.getSavedRecipesForUser(anyString())).thenReturn(Optional.of(expectedRecipes));

        List<SavedRecipe> actualRecipes = recipeService.getSavedRecipesByUserId(userId);

        for (int i = 0; i < actualRecipes.size(); i++) {
            SavedRecipe expectedRecipe = expectedRecipes.get(i);
            SavedRecipe actualRecipe = actualRecipes.get(i);
            assertEquals(expectedRecipe.getUserId(), actualRecipe.getUserId());
            assertEquals(expectedRecipe.getRecipeId(), actualRecipe.getRecipeId());
            assertEquals(expectedRecipe.getName(), actualRecipe.getName());
            assertEquals(expectedRecipe.getReadyInMinutes(), actualRecipe.getReadyInMinutes());
            assertEquals(expectedRecipe.getImageUrl(), actualRecipe.getImageUrl());
            assertEquals(expectedRecipe.getImageSourceUrl(), actualRecipe.getImageSourceUrl());
            assertEquals(expectedRecipe.getSummary(), actualRecipe.getSummary());

            for (int j = 0; j < actualRecipe.getIngredients().size(); j++) {
                Ingredient expectedRecipeIngredient = expectedRecipe.getIngredients().get(j);
                Ingredient actualIngredient = actualRecipe.getIngredients().get(j);
                assertEquals(expectedRecipeIngredient.getId(), actualIngredient.getId());
                assertEquals(expectedRecipeIngredient.getName(), actualIngredient.getName());
                assertEquals(expectedRecipeIngredient.getQuantity(), actualIngredient.getQuantity());
                assertEquals(expectedRecipeIngredient.getUnitMeasurement(), actualIngredient.getUnitMeasurement());
                assertEquals(expectedRecipeIngredient.getImageFileName(), actualIngredient.getImageFileName());
            }
            for (int j = 0; j < actualRecipe.getInstructions().size(); j++) {
                Instruction expectedRecipeInstruction = expectedRecipe.getInstructions().get(j);
                Instruction actualRecipeInstruction = actualRecipe.getInstructions().get(j);
                assertEquals(expectedRecipeInstruction.getNumber(), actualRecipeInstruction.getNumber());
                assertEquals(expectedRecipeInstruction.getStep(), actualRecipeInstruction.getStep());
            }
        }
    }

    @Test
    public void getSavedRecipesByUserId_blankUserId_throwsIllegalArgumentException() {
        String userId = " ";

        assertThrows(IllegalArgumentException.class, () ->  recipeService.getSavedRecipesByUserId(userId));
    }
    @Test
    public void getSavedRecipesByUserId_nullUserId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> recipeService.getSavedRecipesByUserId(null));
    }

    /************************************************************
     * recipeService.getSavedRecipesForUserByRecipeId()         *
     ***********************************************************/

    @Test
    public void getSavedRecipeForUserByRecipeId_savedRecipeExists_returnRecipe() throws SavedRecipeNotFoundException {
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        SavedRecipeKey savedRecipeKey = new SavedRecipeKey(userId, recipeId);
        SavedRecipeEntity expectedRecipe = TestUtil.createMockSavedRecipeEntity(userId);

        when(savedRecipeRepository.findById(eq(savedRecipeKey))).thenReturn(Optional.of(expectedRecipe));

        SavedRecipe actualRecipe = recipeService.getSavedRecipeForUserByRecipeId(userId, recipeId);


        assertEquals(expectedRecipe.getUserId(), actualRecipe.getUserId());
        assertEquals(expectedRecipe.getRecipeId(), actualRecipe.getRecipeId());
        assertEquals(expectedRecipe.getName(), actualRecipe.getName());
        assertEquals(expectedRecipe.getReadyInMinutes(), actualRecipe.getReadyInMinutes());
        assertEquals(expectedRecipe.getImageUrl(), actualRecipe.getImageUrl());
        assertEquals(expectedRecipe.getImageSourceUrl(), actualRecipe.getImageSourceUrl());
        assertEquals(expectedRecipe.getSummary(), actualRecipe.getSummary());

        for (int j = 0; j < actualRecipe.getIngredients().size(); j++) {
            IngredientEntity expectedRecipeIngredient = expectedRecipe.getIngredients().get(j);
            Ingredient actualIngredient = actualRecipe.getIngredients().get(j);
            assertEquals(expectedRecipeIngredient.getId(), actualIngredient.getId());
            assertEquals(expectedRecipeIngredient.getName(), actualIngredient.getName());
            assertEquals(expectedRecipeIngredient.getQuantity(), actualIngredient.getQuantity());
            assertEquals(expectedRecipeIngredient.getUnitMeasurement(), actualIngredient.getUnitMeasurement());
            assertEquals(expectedRecipeIngredient.getImageFileName(), actualIngredient.getImageFileName());
        }
        for (int j = 0; j < actualRecipe.getInstructions().size(); j++) {
            InstructionEntity expectedRecipeInstruction = expectedRecipe.getInstructions().get(j);
            Instruction actualRecipeInstruction = actualRecipe.getInstructions().get(j);
            assertEquals(expectedRecipeInstruction.getNumber(), actualRecipeInstruction.getNumber());
            assertEquals(expectedRecipeInstruction.getStep(), actualRecipeInstruction.getStep());
        }
    }

    @Test
    public void getSavedRecipeForUserByRecipeId_savedRecipeDoesNotExist_throwsSavedRecipeNotFoundException() {
        String userId = mockNeat.users().valStr();
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        SavedRecipeKey savedRecipeKey = new SavedRecipeKey(userId, recipeId);
        SavedRecipeEntity expectedRecipe = TestUtil.createMockSavedRecipeEntity(userId);

        when(savedRecipeRepository.findById(eq(savedRecipeKey))).thenReturn(Optional.empty());

        assertThrows(SavedRecipeNotFoundException.class, () ->  recipeService.getSavedRecipeForUserByRecipeId(userId, recipeId));
    }

    @Test
    public void getSavedRecipeForUserByRecipeId_blankUserId_throwsIllegalArgumentException() {
        String userId = " ";
        Integer recipeId = mockNeat.ints().lowerBound(1).val();

        assertThrows(IllegalArgumentException.class, () ->  recipeService.getSavedRecipeForUserByRecipeId(userId, recipeId));
    }
    @Test
    public void getSavedRecipeForUserByRecipeId_nullUserId_throwsIllegalArgumentException() {
        Integer recipeId = mockNeat.ints().lowerBound(1).val();
        assertThrows(IllegalArgumentException.class, () -> recipeService.getSavedRecipeForUserByRecipeId(null, recipeId));
    }
    @Test
    public void getSavedRecipeForUserByRecipeId_nullRecipeId_throwsIllegalArgumentException() {
        String userId = mockNeat.users().valStr();
        assertThrows(IllegalArgumentException.class, () -> recipeService.getSavedRecipeForUserByRecipeId(userId, null));
    }

    /***************************************************
     * recipeService.removeSavedRecipe()               *
     **************************************************/
    @Test
    public void removeSavedRecipe_savedRecipeExists_removesSavedRecipe() throws SavedRecipeNotFoundException {
        // GIVEN a valid SavedRecipe
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId("testUserId");
        savedRecipe.setRecipeId(1232);

        when(savedRecipeRepository.findById(any(SavedRecipeKey.class))).thenReturn(Optional.of(new SavedRecipeEntity()));

        // WHEN removeSavedRecipe is called
        recipeService.removeSavedRecipe(savedRecipe);
        savedRecipeRepository.delete(any(SavedRecipeEntity.class));

        //THEN the SavedRecipe is removed
        verify(savedRecipeRepository, times(1)).delete(any(SavedRecipeEntity.class));
    }
    @Test
    public void removeSavedRecipe_savedRecipeDoesNotExist_throwsSavedRecipeNotFoundException() {
        // GIVEN an invalid SavedRecipe
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId("testUserId");
        savedRecipe.setRecipeId(1232);

        when(savedRecipeRepository.findById(any(SavedRecipeKey.class))).thenReturn(Optional.empty());
        // WHEN removeSavedRecipe is called
        // THEN an SavedRecipeNotFoundException is thrown
        assertThrows(SavedRecipeNotFoundException.class, () -> recipeService.removeSavedRecipe(savedRecipe));
    }
    @Test
    public void removeSavedRecipe_nullSavedRecipe_throwsIllegalArgumentException() {
        // GIVEN a null SavedRecipe
        SavedRecipe savedRecipe = null;

        // WHEN removeSavedRecipe is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.removeSavedRecipe(savedRecipe));
    }
    @Test
    public void removeSavedRecipe_nullUserId_throwsIllegalArgumentException() {
        // GIVEN a SavedRecipe with a null userId
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId(null);
        savedRecipe.setRecipeId(1232);

        // WHEN removeSavedRecipe is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.removeSavedRecipe(savedRecipe));
    }
    @Test
    public void removeSavedRecipe_validUserIdNullRecipeId_throwsIllegalArgumentException() {
        // GIVEN a SavedRecipe with a null userId and recipeId
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId(mockNeat.users().valStr());
        savedRecipe.setRecipeId(null);

        // WHEN removeSavedRecipe is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.removeSavedRecipe(savedRecipe));
    }


    /***************************************************
     * recipeService.addSavedRecipe()               *
     **************************************************/

    @Test
    public void addSavedRecipe_withUserIdAndRecipeId_returnsSavedRecipe() throws SavedRecipeAlreadyExistsException {
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient(1, "Egg", 2.0, "pcs", "egg.jpg"),
                new Ingredient(2, "Flour", 200.0, "g", "flour.jpg")
        );
        List<Instruction> instructions = Arrays.asList(
                new Instruction(1, "Crack the eggs."),
                new Instruction(2, "Mix the flour.")
        );
        SavedRecipe savedRecipe = new SavedRecipe("user1", 101, "Recipe 1", ingredients,
                30, instructions, "recipe1.jpg", "https://testSavedRecipes.com/recipe1",
                "A Saved recipe No. 1");

        SavedRecipeKey expectedKey = new SavedRecipeKey(savedRecipe.getUserId(), savedRecipe.getRecipeId());
        when(savedRecipeRepository.existsById(expectedKey)).thenReturn(false);

        SavedRecipeEntity savedRecipeEntity = ModelConverter.convertSavedRecipeToSavedRecipeEntity(savedRecipe);
        when(savedRecipeRepository.save(any())).thenReturn(savedRecipeEntity);

        SavedRecipe result = recipeService.addSavedRecipe(savedRecipe);
        SavedRecipeKey actualKey = new SavedRecipeKey(result.getUserId(), result.getRecipeId());


        // Result is not null
        assertNotNull(result);
        // Verify repository checked for existing recipe/duplicate
        verify(savedRecipeRepository, times(1)).existsById(any(SavedRecipeKey.class));
        // Verify repository saved Recipe to Database
        verify(savedRecipeRepository, times(1)).save(any(SavedRecipeEntity.class));
        // Verify expected keys equal actual keys
        assertEquals(expectedKey, actualKey);
    }

    @Test
    public void addSavedRecipe_recipeAlreadyExist_throwsSavedRecipeAlreadyExistsException() {
        // GIVEN : Saved Recipe
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient(1, "Egg", 2.0, "pcs", "egg.jpg"),
                new Ingredient(2, "Flour", 200.0, "g", "flour.jpg")
        );
        List<Instruction> instructions = Arrays.asList(
                new Instruction(1, "Crack the eggs."),
                new Instruction(2, "Mix the flour.")
        );
        SavedRecipe savedRecipe = new SavedRecipe("user1", 101, "Recipe 1", ingredients,
                30, instructions, "recipe1.jpg", "https://testSavedRecipes.com/recipe1",
                "A Saved recipe No. 1");


        // WHEN : recipe already exist
        when(savedRecipeRepository.existsById(any(SavedRecipeKey.class))).thenReturn(true);

        // THEN : SavedRecipeAlreadyExistsException is thrown
        assertThrows(SavedRecipeAlreadyExistsException.class, () -> recipeService.addSavedRecipe(savedRecipe),
                "Expected SavedRecipeAlreadyExistsException to be thrown when adding Recipe that already Exist");
    }

    @Test
    public void addSavedRecipe_nullSavedRecipe_throwsIllegalArgumentException() {
        // GIVEN a null SavedRecipe
        SavedRecipe savedRecipe = null;

        // WHEN removeSavedRecipe is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.addSavedRecipe(savedRecipe));
    }
    @Test
    public void addSavedRecipe_nullUserId_throwsIllegalArgumentException() {
        // GIVEN a SavedRecipe with a null userId
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId(null);
        savedRecipe.setRecipeId(1232);

        // WHEN removeSavedRecipe is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.addSavedRecipe(savedRecipe));
    }
    @Test
    public void addSavedRecipe_validUserIdNullRecipeId_throwsIllegalArgumentException() {
        // GIVEN a SavedRecipe with a null userId and recipeId
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId(mockNeat.users().valStr());
        savedRecipe.setRecipeId(null);

        // WHEN removeSavedRecipe is called
        // THEN an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> recipeService.addSavedRecipe(savedRecipe));
    }


}