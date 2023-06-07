package com.kenzie.appserver.service;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.google.common.reflect.TypeToken;
import com.kenzie.appserver.caching.CacheClient;
import com.kenzie.appserver.caching.InMemoryCacheStore;
import com.kenzie.appserver.exceptions.FridgeItemNotFoundException;
import com.kenzie.appserver.exceptions.FridgeItemAlreadyExistsException;
import com.kenzie.appserver.exceptions.SavedRecipeAlreadyExistsException;
import com.kenzie.appserver.exceptions.SavedRecipeNotFoundException;
import com.kenzie.appserver.repositories.FridgeItemRepository;
import com.kenzie.appserver.repositories.SavedRecipeRepository;
import com.kenzie.appserver.repositories.model.FridgeItemEntity;
import com.kenzie.appserver.repositories.model.FridgeItemKey;
import com.kenzie.appserver.repositories.model.SavedRecipeEntity;
import com.kenzie.appserver.repositories.model.SavedRecipeKey;
import com.kenzie.appserver.service.model.FridgeItem;
import com.kenzie.appserver.service.model.Ingredient;
import com.kenzie.appserver.service.model.Recipe;
import com.kenzie.appserver.service.model.SavedRecipe;
import com.kenzie.appserver.task.GetProfile;
import com.kenzie.appserver.util.ModelConverter;
import com.kenzie.capstone.service.client.ProfileServiceClient;
import com.kenzie.capstone.service.client.RecipeApiProxyClient;
import com.kenzie.capstone.service.model.profile.service.Profile;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyIngredientResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyRecipeResponse;
import com.kenzie.capstone.service.model.recipe.proxy.SortingOption;
import com.kenzie.capstone.service.model.recipe.proxy.ValidRecipeQueryParam;
import io.swagger.models.Model;
import net.bytebuddy.jar.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kenzie.appserver.util.ModelConverter.convertFridgeItemEntityToFridgeItem;
import static com.kenzie.appserver.util.ModelConverter.convertSavedRecipeEntityToSavedRecipe;

@Service
public class RecipeService {
    private final RecipeApiProxyClient recipeApiProxyClient;
    private final FridgeItemRepository fridgeItemRepository;
    private final SavedRecipeRepository savedRecipeRepository;
    private final ProfileServiceClient profileServiceClient;
    private final CacheClient cacheClient;
    private final InMemoryCacheStore inMemoryCache;

    private final static String ALL_FRIDGE_ITEMS_FOR_USER_CACHE_KEY = "AllFridgeItemsForUser::%s";
    private final static int ALL_FRIDGE_ITEMS_FOR_USER_CACHE_TTL = 60 * 60;
    private final static String ALL_SAVED_RECIPES_FOR_USER_CACHE_KEY = "AllSavedRecipesForUser::%s";
    private final static int ALL_SAVED_RECIPES_FOR_USER_CACHE_TTL = 60 * 60;
    @Autowired
    public RecipeService(
            RecipeApiProxyClient recipeApiProxyClient,
            FridgeItemRepository fridgeItemRepository,
            SavedRecipeRepository savedRecipeRepository,
            ProfileServiceClient profileServiceClient,
            CacheClient cacheClient,
            InMemoryCacheStore inMemoryCacheStore) {
            this.recipeApiProxyClient = recipeApiProxyClient;
            this.fridgeItemRepository = fridgeItemRepository;
            this.savedRecipeRepository = savedRecipeRepository;
            this.profileServiceClient = profileServiceClient;
            this.cacheClient = cacheClient;
            this.inMemoryCache = inMemoryCacheStore;
        }

    /**
     * Makes a request to retrieve recipes to the third-party API using the RecipeApiProxy via the RecipeApiProxyClient
     * @param queryParams The query parameters that were sent as part of the request to this API
     * @return A list of Recipes returned from the proxy client
     */

    public List<Recipe> getRecipes(Map<String, String> queryParams) {
        // Call the "getRecipesByComplexSearch" method of a recipeApiProxyClient to get a List of ProxyRecipeResponse objects based on the provided queryParams
        List<ProxyRecipeResponse> proxyRecipeResponses = recipeApiProxyClient.getRecipesByComplexSearch(queryParams);

        // Use a stream to transform the list of proxy recipe responses to a list of Recipe objects
        List<Recipe> recipes = Optional.ofNullable(proxyRecipeResponses).orElse(Collections.emptyList()).stream()
                // Map each proxy recipe response to a Recipe object using the "convertProxyRecipeToRecipe" method
                .map(ModelConverter::convertProxyRecipeToRecipe)
                // Collect the mapped Recipe objects into a List
                .collect(Collectors.toList());

        return recipes;
    }

    /**
     * Retrieves a recipe by its id from the Recipe API
     * @param id The id of the recipe you want to retrieve
     * @return The retrieved recipe.
     */
    public Recipe getRecipeById(String id) {
        ProxyRecipeResponse proxyRecipeResponse = recipeApiProxyClient.getRecipeById(id);

        return ModelConverter.convertProxyRecipeToRecipe(proxyRecipeResponse);
    }

    public List<Ingredient> getIngredients(Map<String, String> queryParams) {
        List<ProxyIngredientResponse> proxyIngredientResponses = recipeApiProxyClient.getIngredients(queryParams);

        return Optional.ofNullable(proxyIngredientResponses).orElse(Collections.emptyList()).stream()
                .map(ModelConverter::convertProxyIngredientToIngredient)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves recipes that utilize the ingredients in the fridge of the customer with specified userId
     * @param userId userId of the customer whose fridge ingredients will be used
     * @param maximizeIngredients flag to maximize the use of customer ingredients if true or minimize missing ingredients if false
     * @return List of recipes retrieved from the query
     */
    public List<Recipe> getRecipesUsingFridgeItems(String userId, boolean maximizeIngredients) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        // submit task to retrieve profile from profile service asynchronously
        Future<Profile> profileFuture = executorService.submit(new GetProfile(userId, profileServiceClient));
        executorService.shutdown();

        List<FridgeItem> fridgeItems = getAllFridgeItemsByUserId(userId);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidRecipeQueryParam.ADD_RECIPE_INFO.getValidQueryParamKey(), "true");
        queryParams.put(ValidRecipeQueryParam.INSTRUCTIONS_REQUIRED.getValidQueryParamKey(), "true");
        queryParams.put(ValidRecipeQueryParam.FILL_INGREDIENTS.getValidQueryParamKey(), "true");
        queryParams.put(ValidRecipeQueryParam.INCLUDE_INGREDIENTS.getValidQueryParamKey(), ModelConverter.convertFridgeItemsToCsvString(fridgeItems));
        queryParams.put(ValidRecipeQueryParam.NUMBER.getValidQueryParamKey(), "10");
        queryParams.put(
                ValidRecipeQueryParam.SORT.getValidQueryParamKey(),
                maximizeIngredients ? SortingOption.MAX_USED_INGREDIENTS.getSortingOption() : SortingOption.MIN_MISSING_INGREDIENTS.getSortingOption()
        );

        // retrieve the profile from the future and add their dietary restrictions (if any) to the query params
        Profile profile = profileFuture.get();

        if (profile != null && profile.getDiets() != null && profile.getDiets().size() > 0) {
            queryParams.put(ValidRecipeQueryParam.DIET.getValidQueryParamKey(), ModelConverter.convertDietsToCsvString(profile.getDiets()));
        }
        System.out.println(queryParams);
        List<ProxyRecipeResponse> proxyRecipeResponses = recipeApiProxyClient.getRecipesByComplexSearch(queryParams);

        return proxyRecipeResponses.stream()
                .map(ModelConverter::convertProxyRecipeToRecipe)
                .collect(Collectors.toList());
    }

    /**
     Retrieves a list of fridge items for the specified user ID.
     @param userId the ID of the user whose fridge is to be retrieved
     @return the customer fridge object for the specified user ID, or null if no object exists
     */
    public List<FridgeItem> getAllFridgeItemsByUserId(String userId) {
        // Check if userId is null or blank
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Cannot retrieve fridge items for null or empty user id");
        }
        // Create a cache key to retrieve all fridge items for the given userId
        String key = String.format(ALL_FRIDGE_ITEMS_FOR_USER_CACHE_KEY, userId);
        // Check if there is a cached value for the key
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        Optional<String> allFridgeItemsJson = cacheClient.getValue(key);
//        // If a cached value is present, deserialize and return the list of fridge items
//        if (allFridgeItemsJson.isPresent()) {
//           return ModelConverter.fromJson(allFridgeItemsJson.get(), new TypeToken<ArrayList<FridgeItem>>(){});
//
//        }

        Optional<List<FridgeItem>> cachedFridgeItems = inMemoryCache.getFridgeItemsForUser(key);

        if (cachedFridgeItems.isPresent()) {
            return cachedFridgeItems.get();
        }

        // Find the customer fridge entity for the given user ID using the repository.
        List<FridgeItemEntity> fridgeItemEntities = fridgeItemRepository.findAllByUserId(userId);

        // Convert the list of fridge item entities to a list of fridge items using the converter.
        List<FridgeItem> fridgeItems = Optional.ofNullable(fridgeItemEntities)
                .orElse(Collections.emptyList())
                .stream()
                .map(ModelConverter::convertFridgeItemEntityToFridgeItem)
                .collect(Collectors.toList());
        // Store the list in the cache as json string
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        cacheClient.setValue(key, ALL_FRIDGE_ITEMS_FOR_USER_CACHE_TTL, ModelConverter.toJson(fridgeItems));
        inMemoryCache.setFridgeItemsForUser(key, fridgeItems);
        // return list of fridgeItems
        return fridgeItems;
    }

    /**
     * Adds a fridge item to the user's fridge
     * @param fridgeItem the fridge item to be added
     * @return the added fridge item with a generated ID
     */
    public FridgeItem addFridgeItem(FridgeItem fridgeItem) throws FridgeItemAlreadyExistsException {
        // Validate the fridge item
        if (fridgeItem == null) {
            throw new IllegalArgumentException("Cannot add fridge item with null item.");
        } else if (fridgeItem.getUserId() == null || fridgeItem.getUserId().isBlank()) {
            throw new IllegalArgumentException("Cannot add fridge item with invalid user id.");
        } else if (fridgeItem.getName() == null || fridgeItem.getName().isBlank()) {
            throw new IllegalArgumentException("Cannot add fridge item with invalid item name.");
        } else if (fridgeItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cannot add fridge item with quantity less than or equal to 0");
        }
        if (fridgeItemRepository.existsById(new FridgeItemKey(fridgeItem.getUserId(), fridgeItem.getName())))
            throw new FridgeItemAlreadyExistsException(String.format("Cannot create fridge item with userId: %s and name: %s", fridgeItem.getUserId(), fridgeItem.getName()));

        // Setting the Last Updated field
        fridgeItem.setLastUpdated(LocalDateTime.now().toString());

        // Convert the FridgeItem object to a FridgeItemEntity object
        FridgeItemEntity fridgeItemEntity = ModelConverter.itemToEntity(fridgeItem);

        // Save the FridgeItemEntity object to the database
        fridgeItemRepository.save(fridgeItemEntity);
        // Invalidate the cache for the given key
        String cacheKey = String.format(ALL_FRIDGE_ITEMS_FOR_USER_CACHE_KEY, fridgeItem.getUserId());
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        cacheClient.invalidate(cacheKey);
        inMemoryCache.invalidateFridgeItemsForUser(cacheKey);

        // Convert the FridgeItemEntity object back to a FridgeItem object
        return convertFridgeItemEntityToFridgeItem(fridgeItemEntity);
    }
    public FridgeItem updateFridgeItem(FridgeItem fridgeItem) throws FridgeItemNotFoundException {
        if (fridgeItem == null)
            throw new IllegalArgumentException("Cannot update null fridge item");
        if (fridgeItem.getUserId() == null || fridgeItem.getUserId().isBlank())
            throw new IllegalArgumentException("Cannot update fridge item with invalid userId");
        if (fridgeItem.getName() == null || fridgeItem.getName().isBlank())
            throw new IllegalArgumentException("Cannot update fridge item with invalid item name");
        else if (fridgeItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cannot update fridge item with quantity less than or equal to 0");
        }
        if (!fridgeItemRepository.existsById(new FridgeItemKey(fridgeItem.getUserId(), fridgeItem.getName()))) {
            throw new FridgeItemNotFoundException(
                    String.format("Cannot update fridge item with userId: %s and name: %s. Item not found",
                            fridgeItem.getUserId(),
                            fridgeItem.getName())
            );
        }
        fridgeItem.setLastUpdated(LocalDateTime.now().toString());
        fridgeItemRepository.save(ModelConverter.itemToEntity(fridgeItem));

        String cacheKey = String.format(ALL_FRIDGE_ITEMS_FOR_USER_CACHE_KEY, fridgeItem.getUserId());
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        cacheClient.invalidate(cacheKey);
        inMemoryCache.invalidateFridgeItemsForUser(cacheKey);

        return fridgeItem;
    }

    public FridgeItem removeFridgeItem(FridgeItem fridgeItem) throws FridgeItemNotFoundException {
        if (fridgeItem == null)
            throw new IllegalArgumentException("Cannot remove null fridge item");
        if (fridgeItem.getUserId() == null || fridgeItem.getUserId().isBlank())
            throw new IllegalArgumentException("Cannot remove fridge item with null or empty user id");
        if (fridgeItem.getName() == null || fridgeItem.getName().isBlank())
            throw new IllegalArgumentException("Cannot remove fridge item with invalid item name");

        // Find the fridge item entity for the given fridge item ID using the repository.
        Optional<FridgeItemEntity> fridgeItemEntity = fridgeItemRepository.findById(new FridgeItemKey(fridgeItem.getUserId(), fridgeItem.getName()));

        // If the fridge item entity is not found, throw a FridgeItemNotFoundException.
        if (fridgeItemEntity.isEmpty())
            throw new FridgeItemNotFoundException(fridgeItem.getUserId());

        // Delete the fridge item entity using the repository.
        fridgeItemRepository.delete(fridgeItemEntity.get());

        // Invalidate the cache for the given key
        String cacheKey = String.format(ALL_FRIDGE_ITEMS_FOR_USER_CACHE_KEY, fridgeItem.getUserId());
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        cacheClient.invalidate(cacheKey);
        inMemoryCache.invalidateFridgeItemsForUser(cacheKey);
        return convertFridgeItemEntityToFridgeItem(fridgeItemEntity.get());
    }

    /**
     * Retrieves a list of saved recipes for the specified user ID.
     *@param userId the ID of the user whose saved recipes are to be retrieved
     * @return A list of saved recipes for the specified user ID, or an empty list if no saved recipes exist
     */
    public List<SavedRecipe> getSavedRecipesByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Cannot retrieve saved recipes for null or empty user id");
        }
        String cacheKey = String.format(ALL_SAVED_RECIPES_FOR_USER_CACHE_KEY, userId);

        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        Optional<String> cachedSavedRecipesAsJson = cacheClient.getValue(cacheKey);
//        if (cachedSavedRecipesAsJson.isPresent()) {
//            return ModelConverter.fromJson(cachedSavedRecipesAsJson.get(), new TypeToken<List<SavedRecipe>>(){});
//        }
        Optional<List<SavedRecipe>> cachedSavedRecipes = inMemoryCache.getSavedRecipesForUser(cacheKey);
        if (cachedSavedRecipes.isPresent())
            return cachedSavedRecipes.get();

        // Find the saved recipe entities for the given user ID using the repository.
        List<SavedRecipeEntity> recipeEntities = savedRecipeRepository.findAllByUserId(userId);

        // Convert the list of saved recipe entities to a list of recipe objects using the converter.
        List<SavedRecipe> savedRecipes = Optional.ofNullable(recipeEntities)
                .orElse(Collections.emptyList())
                .stream()
                .map(ModelConverter::convertSavedRecipeEntityToSavedRecipe)
                .collect(Collectors.toList());

        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
//        String savedRecipesAsJson =  ModelConverter.toJson(savedRecipes);
//        cacheClient.setValue(cacheKey, ALL_SAVED_RECIPES_FOR_USER_CACHE_TTL, savedRecipesAsJson);
        inMemoryCache.setSavedRecipesForUser(cacheKey, savedRecipes);

        return savedRecipes;
    }

    public SavedRecipe getSavedRecipeForUserByRecipeId(String userId, Integer recipeId) throws SavedRecipeNotFoundException {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("Cannot retrieve saved recipe when userId is invalid.");
        if (recipeId == null)
            throw new IllegalArgumentException("Cannot retrieve saved recipe with invalid recipeId");
        SavedRecipeKey savedRecipeKey = new SavedRecipeKey(userId, recipeId);
        Optional<SavedRecipeEntity> savedRecipeEntity = savedRecipeRepository.findById(savedRecipeKey);

        return ModelConverter.convertSavedRecipeEntityToSavedRecipe(savedRecipeEntity.orElseThrow(SavedRecipeNotFoundException::new));
    }

    /**
     * Adds a new SavedRecipe to the user's saved recipes list.
     * @param savedRecipe SavedRecipe object to be added to the user's saved recipes
     * @return SavedRecipe object that was added to the user's saved recipes
     * @throws SavedRecipeAlreadyExistsException if the SavedRecipe already exists
     */
    public SavedRecipe addSavedRecipe(SavedRecipe savedRecipe) throws SavedRecipeAlreadyExistsException {
        if (savedRecipe == null)
            throw new IllegalArgumentException("Cannot add saved recipe when saved recipe is null.");
        if (savedRecipe.getUserId() == null || savedRecipe.getUserId().isBlank())
            throw new IllegalArgumentException("Cannot add saved recipe with invalid userId.");
        if (savedRecipe.getRecipeId() == null)
            throw new IllegalArgumentException("Cannot add saved recipe with null recipeId.");

        // Create SaveRecipeKey object to query SavedRecipeRepository
        SavedRecipeKey key = new SavedRecipeKey(savedRecipe.getUserId(), savedRecipe.getRecipeId());
        // If recipe already exist, throw SavedRecipeAlreadyExistsException
        if (savedRecipeRepository.existsById(key)) {
            throw new SavedRecipeAlreadyExistsException("Unable to Add Saved Recipe.  Saved Recipe: " + key +
                    "already exist");
        }
        // Delete list of recipes for user that is stored in the Cache
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
        String cacheKey = String.format(ALL_SAVED_RECIPES_FOR_USER_CACHE_KEY, savedRecipe.getUserId());
//        cacheClient.invalidate(cacheKey);
        inMemoryCache.invalidateSavedRecipesForUser(cacheKey);

        // Convert SavedRecipe to SavedRecipeEntity object (SavedRecipeRepository requires entity object)
        SavedRecipeEntity savedRecipeEntity = ModelConverter.convertSavedRecipeToSavedRecipeEntity(savedRecipe);

        // Save SaveRecipeEntity to the repository
        SavedRecipeEntity returnedEntity = savedRecipeRepository.save(savedRecipeEntity);

        // Convert SavedRecipeEntity back to a SavedRecipe object and return
        return ModelConverter.convertSavedRecipeEntityToSavedRecipe(returnedEntity);
    }

    public SavedRecipe removeSavedRecipe(SavedRecipe savedRecipe) throws SavedRecipeNotFoundException {
        if (savedRecipe == null)
            throw new IllegalArgumentException("Cannot remove null saved recipe");
        if (savedRecipe.getUserId() == null || savedRecipe.getUserId().isBlank())
            throw new IllegalArgumentException("Cannot remove saved recipe with null or empty user id");
        if (savedRecipe.getRecipeId() == null)
            throw new IllegalArgumentException("Cannot remove recipe with null recipe id.");
        SavedRecipeKey savedRecipeKey = new SavedRecipeKey(savedRecipe.getUserId(), savedRecipe.getRecipeId());
        Optional<SavedRecipeEntity> existingRecipe = savedRecipeRepository.findById(savedRecipeKey);
        if (existingRecipe.isEmpty()){
            throw new SavedRecipeNotFoundException(String.format("Cannot find recipe with userId: %s and recipeId: %s", savedRecipeKey.getUserId(), savedRecipe.getRecipeId()));
        }
        savedRecipeRepository.deleteById(savedRecipeKey);
        // TODO: Uncomment the below lines when we can figure out a way to get the JEDIS_URL in the Application module
        String cacheKey = String.format(ALL_SAVED_RECIPES_FOR_USER_CACHE_KEY, savedRecipe.getUserId());
//        cacheClient.invalidate(cacheKey);
        inMemoryCache.invalidateSavedRecipesForUser(cacheKey);
        return ModelConverter.convertSavedRecipeEntityToSavedRecipe(existingRecipe.get());
    }

}
