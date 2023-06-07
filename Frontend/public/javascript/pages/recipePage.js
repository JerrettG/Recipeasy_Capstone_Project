import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import RecipeServiceClient from "../api/recipeServiceClient";


export default class RecipePage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods([
            'renderRecipe',
            'toggleSavedRecipeStatus',
        ], this);
        this.dataStore = new DataStore();
        this.recipeServiceClient = new RecipeServiceClient();
    }

    async mount() {
        this.showLoading(true);
        const queryParams = new URLSearchParams(window.location.search);

        const id = queryParams.get("id");

        let recipe = await this.recipeServiceClient.getRecipeById(id);

        if (isAuthenticated) {
            let isSavedRecipe = await this.recipeServiceClient.getSavedRecipeByRecipeId(userId, recipe.id, this.errorHandler);
            if (isSavedRecipe) {
                this.dataStore.set("isSavedRecipe", isSavedRecipe);
            }
        }

        if (recipe) {
            this.dataStore.set("recipe", recipe);
        } else {
            this.showMessage("Error getting recipe.");
        }
        await this.renderRecipe();
    }

    async toggleSavedRecipeStatus(event) {
        if (isAuthenticated) {
            let saveRecipeCheckbox = event.target;
            const recipe = this.dataStore.get("recipe");
            if (saveRecipeCheckbox.checked) {
                await this.recipeServiceClient.addSavedRecipe(
                    userId,
                    recipe.id,
                    recipe.name,
                    recipe.ingredients,
                    recipe.readyInMinutes,
                    recipe.instructions,
                    recipe.imageUrl,
                    recipe.imageSourceUrl,
                    recipe.summary,
                    this.errorHandler);
            } else {
                await this.recipeServiceClient.removeSavedRecipe(userId, recipe.id, this.errorHandler);
            }
        } else {
            window.location.href = "/login";
        }
    }



    async renderRecipe() {
        const recipe = this.dataStore.get("recipe");
        const recipeContainer = document.querySelector(".recipe-container");

        if (recipe) {
            let ingredients = '';
            for (let ingredient of recipe.ingredients) {
                ingredients +=
                    `
                    <li class="ingredients-list-item">
                        <img src="https://spoonacular.com/cdn/ingredients_100x100/${ingredient.imageFileName}" alt="${ingredient.imageFileName}">
                        <span>${ingredient.quantity} ${ingredient.unitMeasurement} ${this.capitalizeFirstLetter(ingredient.name)}</span>
                    </li>
                    `;
            }
            let instructions = '';
            for (let instruction of recipe.instructions) {
                instructions +=
                    `
                    <li>
                        <span>${instruction.number}. ${instruction.step}</span>
                    </li>
                    `;
            }
            let isSavedRecipe = this.dataStore.get("isSavedRecipe");
            let loginMessage =
                `
                <div class="saveRecipe-loginMessage">
                    <span>Please login to save recipes to your profile</span>
                    <div class="loginMessage-point"></div>  
                </div>
                `;
            recipeContainer.innerHTML =
                `
                <div class="recipe">
                    <div class="recipe-imageName-container">
                        <div class="recipe-image">
                            <img src="${recipe.imageUrl}" alt="${recipe.name}">
                        </div>
                        <div class="recipe-name">
                            <h2>${recipe.name}</h2>
                        </div>
                        <div class="recipe-saveRecipe">
                            <label for="saveRecipe-checkbox" class="saveRecipe-label">
                                <input ${isSavedRecipe ? "checked" : ""} type="checkbox" class="saveRecipe-checkbox" id="saveRecipe-checkbox" ${!isAuthenticated ? "disabled" : ""}>
                                <i class="fa-regular fa-bookmark fa-2xl saveRecipe-icon"></i>
                            </label>
                            ${!isAuthenticated ? loginMessage : ""}
                        </div>
                    </div>
                    <div class="recipe-summary">
                        <h3>Description</h3>
                        <span>${recipe.summary}</span>
                    </div>
                    <div class="recipe-ingredients">
                        <h3>Ingredients</h3>
                        <ul class="ingredients-list">${ingredients}</ul>
                    </div>
                    <div class="recipe-instructions">
                        <h3>Instructions</h3>
                        <ul class="instructions-list">${instructions}</ul>
                    </div>
                </div>
                `;
            document.querySelector("#saveRecipe-checkbox").addEventListener("change", this.toggleSavedRecipeStatus);
            this.showLoading(false);
        } else {
            recipeContainer.innerHTML = "<h1>Sorry there was an issue loading your recipe</h1>";
        }
    }


}


const main = async () => {
    const recipePage = new RecipePage();
    await recipePage.mount();
};

window.addEventListener('DOMContentLoaded', main);