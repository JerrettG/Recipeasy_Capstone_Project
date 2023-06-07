import BaseClass from "../util/baseClass";
import axios from "axios";

const RECIPE_SERVICE_BASE_PATH = "/api/v1/recipeService"
const REQUIRED_QUERY_PARAMS = "instructionsRequired=true&fillIngredients=true&addRecipeInformation=true&";
const GET_RECIPES_ENDPOINT = "/recipes";
const GET_INGREDIENTS_ENDPOINT = "/ingredients"
const GET_RECIPE_BY_ID_ENDPOINT = (id) => `/recipes/${id}`;
const GET_RECIPES_USING_FRIDGE_ITEMS_ENDPOINT = (userId) => `/recipes/fridgeItems/${userId}`;
const GET_CUSTOMER_FRIDGE_ENDPOINT = (userId) => `/fridgeItems/${userId}`;
const ADD_ITEM_TO_CUSTOMER_FRIDGE_ENDPOINT = "/fridgeItems";
const UPDATE_FRIDGE_ITEM_ENDPOINT = "/fridgeItems";
const REMOVE_ITEM_FROM_CUSTOMER_FRIDGE_ENDPOINT = "/fridgeItems";
const GET_SAVED_RECIPES_ENDPOINT = (userId) => `/savedRecipes/${userId}`;
const GET_SAVED_RECIPE_FOR_USER_BY_RECIPE_ID_ENDPOINT = (userId, recipeId) => `/savedRecipes/${userId}/${recipeId}`;
const ADD_SAVED_RECIPE_ENDPOINT = "/savedRecipes";
const REMOVE_SAVED_RECIPE_ENDPOINT = "/savedRecipes";

export default class RecipeServiceClient extends BaseClass {

    constructor(props = {}) {
        super();
        const methodsToBind = []
        this.bindClassMethods(methodsToBind);
        this.props = props;

        this.clientLoaded(axios);
    }

    /**
     * Run any functions that are supposed to be called once the client has loaded successfully.
     */
    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty("onReady")){
            this.props.onReady();
        }
    }

    async getRecipes(queryParameters = new Map(), errorCallback){
        let url = `${RECIPE_SERVICE_BASE_PATH}${GET_RECIPES_ENDPOINT}?${REQUIRED_QUERY_PARAMS}`;

        for (let [key, value] of queryParameters) {
            url += `${key}=${value}&`
        }
        try {
            const response = await this.client.get(encodeURI(url));
            return response.data;
        } catch (error){
            this.handleError("getRecipes", error, errorCallback);
        }
    }
    async getRecipeById(id, errorCallback) {
        try {
            const response = await this.client.get(`${RECIPE_SERVICE_BASE_PATH}${GET_RECIPE_BY_ID_ENDPOINT(id)}`);
            return response.data;
        } catch (error) {
            this.handleError("getRecipeById", error, errorCallback);
        }
    }

    async getIngredients(query, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${GET_INGREDIENTS_ENDPOINT}?query=${query}`;
        try {
            const response = await this.client.get(encodeURI(url));
            return response.data;
        } catch (error) {
            this.handleError("getIngredients", error, errorCallback);
        }
    }

    /**
     Retrieves all fridge items associated with the given userId.
     @param {string} userId - The ID of the user whose fridge items to retrieve.
     @param {function} errorCallback - A callback function to execute if the request fails.
     @return {List<FridgeItem>} A list of FridgeItem objects if the request is successful.
     @throws {Error} If the recipe service client encounters an error.
     */
    async getAllFridgeItemsByUserId(userId, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${GET_CUSTOMER_FRIDGE_ENDPOINT(userId)}`;
        try {
            const response = await this.client.get(encodeURI(url));
            return response.data;
        } catch (error) {
            this.handleError("getAllFridgeItems", error, errorCallback);
        }
    }

    async getRecipesUsingFridgeItems(userId, maximizeIngredients = true, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${GET_RECIPES_USING_FRIDGE_ITEMS_ENDPOINT(userId)}?maximizeIngredients=${maximizeIngredients}`;

        try {
            const response = await this.client.get(encodeURI(url));
            return response.data;
        } catch (error) {
            this.handleError("getRecipesUsingFridgeItems", error, errorCallback);
        }
    }

    async addFridgeItem(userId, name, purchaseDate, quantity, unitMeasurement, imageFileName, errorCallback){
        let url = `${RECIPE_SERVICE_BASE_PATH}${ADD_ITEM_TO_CUSTOMER_FRIDGE_ENDPOINT}`;
        try {
            const response = await this.client.post(url, {
                userId: userId,
                name: name,
                purchaseDate: purchaseDate,
                quantity: quantity,
                unitMeasurement: unitMeasurement,
                imageFileName: imageFileName,
            });
            return response.data;
        } catch (error) {
            this.handleError("addFridgeItem", error, errorCallback);
        }
    }
    async updateFridgeItem(userId, name, purchaseDate, quantity, unitMeasurement, imageFileName, errorCallback){
        let url = `${RECIPE_SERVICE_BASE_PATH}${UPDATE_FRIDGE_ITEM_ENDPOINT}`;
        try {
            const response = await axios.put(url, {
                userId: userId,
                name: name,
                purchaseDate: purchaseDate,
                quantity: quantity,
                unitMeasurement: unitMeasurement,
                imageFileName: imageFileName,
            });
            return response.data;
        } catch (error) {
            this.handleError("updateFridgeItem", error, errorCallback);
        }
    }

    async removeFridgeItem(userId, name, errorCallback){
        let url = `${RECIPE_SERVICE_BASE_PATH}${REMOVE_ITEM_FROM_CUSTOMER_FRIDGE_ENDPOINT}`;
        try {
            const response = await this.client.delete(url, {
                //axios requires use of "data" specifier to include body on delete request
                // source: https://stackoverflow.com/questions/51069552/axios-delete-request-with-request-body-and-headers#:~:text=So%20after%20a%20number%20of%20tries%2C%20I%20found%20it%20working.
                data: {
                    userId: userId,
                    name: name
                }
            });
            return response.data;
        } catch (error) {
            this.handleError("removeFridgeItem", error, errorCallback);
        }
    }
    async getAllSavedRecipesByUserId(userId, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${GET_SAVED_RECIPES_ENDPOINT(userId)}`;
        try {
            const response = await this.client.get(encodeURI(url));
            return response.data;
        } catch (error) {
            this.handleError("getSavedRecipesByUserId", error, errorCallback);
        }
    }

    async getSavedRecipeByRecipeId(userId, recipeId, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${GET_SAVED_RECIPE_FOR_USER_BY_RECIPE_ID_ENDPOINT(userId, recipeId)}`;
        try {
            const response = await this.client.get(encodeURI(url));
            return response.data;
        } catch (error) {
            console.log("getSavedRecipeByRecipeId", error, errorCallback);
        }
    }

    async addSavedRecipe(userId, recipeId, name, ingredients, readyInMinutes, instructions, imageUrl, imageSourceUrl, summary, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${ADD_SAVED_RECIPE_ENDPOINT}`;
        try {
            const response = await this.client.post(encodeURI(url), {
                userId: userId,
                recipeId: recipeId,
                name: name,
                ingredients: ingredients,
                readyInMinutes: readyInMinutes,
                instructions: instructions,
                imageUrl: imageUrl,
                imageSourceUrl: imageSourceUrl,
                summary: summary,
            });
            return response.data;
        } catch (error) {
            this.handleError("addSavedRecipe", error, errorCallback);
        }
    }
    async removeSavedRecipe(userId, recipeId, errorCallback) {
        let url = `${RECIPE_SERVICE_BASE_PATH}${REMOVE_SAVED_RECIPE_ENDPOINT}`;
        try {
            const response = await this.client.delete(encodeURI(url), {
                //axios requires use of "data" specifier to include body on delete request
                // source: https://stackoverflow.com/questions/51069552/axios-delete-request-with-request-body-and-headers#:~:text=So%20after%20a%20number%20of%20tries%2C%20I%20found%20it%20working.
                data: {
                    userId: userId,
                    recipeId: recipeId
                }
            });
            return response.data;
        } catch (error) {
            this.handleError("removeSavedRecipe", error, errorCallback);
        }
    }

    /**
     * Helper method to log the error and run any error functions.
     * @param error The error received from the server.
     * @param errorCallback (Optional) A function to execute if the call fails.
     */
    handleError(method, error, errorCallback) {
        console.error(method + " failed - " + error);
        if (error.response.data.message !== undefined) {
            console.error(error.response.data.message);
        }
        if (errorCallback) {
            errorCallback(method + " failed - " + error);
        }
    }

}
