import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import ProfileServiceClient from "../api/profileServiceClient";
import RecipeServiceClient from "../api/recipeServiceClient";


export default class ProfilePage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods([
            'addFridgeItem', 'displayAddFridgeItem',
            'renderFridgeItems', 'renderIngredientSearchResults',
            'renderProfile', 'renderRecommendations',
            'renderSavedRecipes', 'removeFridgeItem', 'updateFridgeItem','updateProfile',
        ], this);
        this.profileServiceClient = new ProfileServiceClient();
        this.recipeServiceClient = new RecipeServiceClient();
        this.dataStore = new DataStore();
    }

    async mount() {
        document.querySelector(".addFridgeItem-button").addEventListener("click", this.displayAddFridgeItem);
        document.querySelector("#editProfile-button")
            .addEventListener("click", this.toggleEdit);
        document.querySelector("#cancelEditProfile-button")
            .addEventListener("click", this.toggleEdit);
        document.querySelector("#editProfile-form")
            .addEventListener("submit", this.updateProfile);
        // this.dataStore.addChangeListener();
        this.renderProfile();
        this.renderRecommendations();
        this.renderSavedRecipes();
        this.renderFridgeItems();
    }

    async renderRecommendations() {
        let skeletonLoaders = []
        for (let i = 0; i < 10; i++) {
            skeletonLoaders.push(
                `
                <div class="thumbnail thumbnail-userRecommendation">
                    <div class="userRecommendations-image skeleton"></div>
                    <div class="userRecommendations-recipeName skeleton-text-box">
                        <div class="skeleton-text skeleton"></div>
                        <div class="skeleton-text skeleton" style="width: 75%"></div>
                    </div>
                </div>
                `
            )
        }
        skeletonLoaders = skeletonLoaders.join("");
        let maxIngredientsSlider = document.querySelector("#recommendations-maxIngredients .userRecommendations-slider");
        maxIngredientsSlider.innerHTML = skeletonLoaders;
        let minMissingSlider = document.querySelector("#recommendations-minMissing .userRecommendations-slider");
        minMissingSlider.innerHTML = skeletonLoaders;
        let maximizeIngredients = this.recipeServiceClient.getRecipesUsingFridgeItems(userId, true, this.errorHandler);
        let minimizeMissing = this.recipeServiceClient.getRecipesUsingFridgeItems(userId, false, this.errorHandler);

        maximizeIngredients
            .then(recipes => {
                document.querySelector("#recommendations-maxIngredients .userRecommendations-slider").innerHTML = recipes.reduce((html, recipe) => {
                    return html +=
                        `
                    <a href="/recipe/${recipe.name}?id=${recipe.id}">
                        <div class="thumbnail thumbnail-userRecommendation">
                            <img src="${recipe.imageUrl}" alt="${recipe.name}" width="100" height="100">
                            <div class="thumbnail-text-box">
                                <span class="thumbnail-text">${recipe.name}</span>
                            </div>
                        </div>
                    </a>
                    `
                }, "");
            })
            .catch(err => console.log(err));
        minimizeMissing
            .then(recipes => {
                document.querySelector("#recommendations-minMissing .userRecommendations-slider").innerHTML = recipes.reduce((html, recipe) => {
                    return html +=
                        `
                    <a href="/recipe/${recipe.name}?id=${recipe.id}">
                        <div class="thumbnail thumbnail-userRecommendation">
                            <img src="${recipe.imageUrl}" alt="${recipe.name}" width="100" height="100">
                            <div class="thumbnail-text-box">
                                <span class="thumbnail-text">${recipe.name}</span>
                            </div>
                        </div>
                    </a>
                    `
                }, "");
            })
            .catch(err => console.log(err));
    }

    async renderFridgeItems() {
        let skeletonLoaders = [];
        for (let i = 0; i < 12; i++) {
            skeletonLoaders.push(`
                <li class="fridgeItem-li">
                    <div class="thumbnail ">
                        <div class="userRecommendations-image skeleton"></div>
                        <div class="thumbnail-text-box skeleton-text-box text-box-ingredient">
                            <div class="skeleton-text skeleton" style="width: 75%"></div>
                            <div class="skeleton-text skeleton"></div>
                            <div class="skeleton-text skeleton"></div>
                        </div>
                    </div>
                </li>`);
        }
        let fridgeItemsList = document.querySelector(".fridgeItems-ul");
        fridgeItemsList.innerHTML = skeletonLoaders.join("");

        let fridgeItems = await this.recipeServiceClient.getAllFridgeItemsByUserId(userId, this.errorHandler);

        if (fridgeItems && fridgeItems.length > 0) {
            let html = "";

            for (let fridgeItem of fridgeItems) {
                let options = ["g", "oz", "lb", "tsp", "tbsp", "cup", "gallon", "each", "ct", "box"];
                let optionsHtml = options.reduce((html, option) => {
                    if (option === fridgeItem.unitMeasurement) {
                        return html += `<option value="${option}" selected>${option}</option>`
                    } else {
                        return html += `<option value="${option}">${option}</option>`
                    }
                });
                html +=
                    `
                    <li class="fridgeItem-li">
                        <form data-imageFileName="${fridgeItem.imageFileName}" data-itemName="${fridgeItem.name}" id="${fridgeItem.name}-update-form" class="updateFridgeItem-form">
                            <div class="editButtons">
                                <button type="button" class="edit-button">Edit</button>
                                <button data-itemName="${fridgeItem.name}" type="button" class="delete-button">Remove</button>
                                <button type="submit" form="${fridgeItem.name}-update-form" class="save_cancel-buttons">Save</button>
                                <button type="button" class="save_cancel-buttons cancel-button">Cancel</button>
                               
                                <!--                <button type="button" id="deleteProfile-button">Delete</button>-->
                            </div>
                            <fieldset disabled>
                                <div class="thumbnail">
                                    <img src="https://spoonacular.com/cdn/ingredients_100x100/${fridgeItem.imageFileName}" alt="vanilla-wafers.jpg" width="100" height="100">
                                    <div class="thumbnail-text-box text-box-ingredient">
                                        <span class="thumbnail-text">
                                            <input class="fridgeItem-input" type="number" min="0.01" step="0.01" value="${fridgeItem.quantity}" name="quantity"> 
                                            <select class="fridgeItem-input" name="unitMeasurement">
                                                ${optionsHtml}
                                            </select>
                                        </span>
                                        <span class="thumbnail-text thumbnail-text-ingredient">${this.capitalizeFirstLetter(fridgeItem.name)}</span>
                                        <span class="thumbnail-text" style="display: flex">Purchased: <input class="fridgeItem-input" type="date" value="${fridgeItem.purchaseDate}" name="purchaseDate"/></span>
                                    </div>
                                </div>
                            </fieldset>
                        </form>
                    </li>
                    `;
            }
            fridgeItemsList.innerHTML = html;
            document.querySelectorAll(".fridgeItem-li .edit-button, .fridgeItem-li .cancel-button")
                .forEach(editBtn => editBtn.addEventListener("click", this.toggleEdit));
            document.querySelectorAll(".updateFridgeItem-form")
                .forEach(saveBtn => saveBtn.addEventListener("submit", this.updateFridgeItem));
            document.querySelectorAll(".delete-button")
                .forEach(deleteBtn => deleteBtn.addEventListener("click", this.removeFridgeItem));
        } else {
            fridgeItemsList.innerHTML = `<h2>You haven't added any fridge items yet.</h2>`;
        }
    }

    async renderIngredientSearchResults(event) {
        let submitButton = document.getElementById("submit");
        submitButton.disabled = true;
        let searchResults = document.querySelector(".searchResults-ingredients");
        searchResults.innerHTML = "<i class=\"fas fa-sync fa-spin\"></i>";
        const query = event.target.value.toLowerCase();
        let ingredients = await this.recipeServiceClient.getIngredients(query, this.errorHandler);
        // let ingredients = null;
        if (ingredients) {
            let html = "";
            for (let ingredient of ingredients) {
                html +=
                    `
                    <div class="searchResult">
                        <label>
                        <input
                            required
                            data-name="${ingredient.name}"
                            data-imageFileName="${ingredient.imageFileName}"
                            type="radio"
                            class="searchResult-ingredient-radio"
                            name="ingredient">
                        ${ingredient.name}
                        </label>
                    </div>
                    `;
            }
            searchResults.style.justifyContent = "flex-start";
            searchResults.innerHTML = html;
            submitButton.disabled = false;
        } else {
            searchResults.style.justifyContent = "center";
            searchResults.innerHTML = `<h2>No search results</h2>`;
        }
    }

    async renderProfile() {
        let profile = await this.profileServiceClient.getProfileByUserId(userId);

        let profileFieldset = document.querySelector("#editProfile-form fieldset");
        let userDietsSet = new Set(profile.diets);
        let DIETARY_RESTRICTIONS = new Map(Object.entries({
            GLUTEN_FREE: "Gluten Free",
            KETOGENIC: "Ketogenic",
            VEGETARIAN: "Vegetarian",
            LACTO_VEGETARIAN: "Lacto-Vegetarian",
            OVO_VEGETARIAN: "Ovo-Vegetarian",
            VEGAN: "Vegan",
            PESCETARIAN: "Pescetarian",
            PALEO: "Paleo",
            PRIMAL: "Primal",
            LOW_FOD_MAP: "Low FODMAP",
            WHOLE30: "Whole30"
        }));
        let userDietaryRestrictions = "";
        DIETARY_RESTRICTIONS.forEach( (value, key) => {
           return userDietaryRestrictions +=
               `
                <label>
                    <input ${userDietsSet.has(key) ? "checked" : ""} class="filter dietaryRestrictionFilter" name="dietaryRestriction" value="${key}" type="checkbox"/>${value}                </label>
                `});
        profileFieldset.innerHTML =
            `
            <div class="profileInfo form-group">
                        <span>Username:</span>
                        <span class="form-group-input" id="profile-username">${profile.userId}</span>
                    </div>
                    <div class="profileInfo form-group">
                        <span>Email:</span>
                        <input name="email" value="${profile.email}" type="email" id="profile-email">
                    </div>
                    <div class="profileInfo form-group">
                        <span>Full Name:</span>
                        <input name="name" value="${profile.name}" type="text" id="profile-fullName">
                    </div>
                    <div class="profileInfo">
                        <span>Dietary Restrictions:</span>
                        <div class="dietaryRestricts">
                            ${userDietaryRestrictions}
                        </div>
                    </div>
            `;
    }

    async renderSavedRecipes() {
        let skeletonLoaders = []
        for (let i = 0; i < 12; i++) {
            skeletonLoaders.push(
                `
                <div class="thumbnail thumbnail-userRecommendation">
                    <div class="userRecommendations-image skeleton"></div>
                    <div class="userRecommendations-recipeName skeleton-text-box">
                        <div class="skeleton-text skeleton"></div>
                        <div class="skeleton-text skeleton" style="width: 75%"></div>
                    </div>
                </div>
                `
            );
        }
        skeletonLoaders = skeletonLoaders.join("");
        let savedRecipesContainer = document.querySelector(".savedRecipes-ul");
        savedRecipesContainer.innerHTML = skeletonLoaders;
        let recipes = await this.recipeServiceClient.getAllSavedRecipesByUserId(userId, this.errorHandler);

        if (recipes) {
            savedRecipesContainer.innerHTML = recipes.reduce((html, recipe) => {
                return html +=
                    `
                    <a href="/recipe/${recipe.name}?id=${recipe.recipeId}">
                        <div class="thumbnail thumbnail-userRecommendation">
                            <img src="${recipe.imageUrl}" alt="${recipe.name}" width="100" height="100">
                            <div class="thumbnail-text-box">
                                <span class="thumbnail-text">${recipe.name}</span>
                            </div>
                        </div>
                    </a>
                    `
            }, "");
        }
    }

    async addFridgeItem(event) {
        event.preventDefault();
        let addFridgeItemForm = document.getElementById("addFridgeItem-form");
        let formData = new FormData(addFridgeItemForm);
        // retrieve the selected ingredient that's radio is checked
        let selectedIngredient = addFridgeItemForm.querySelector("input[name=\"ingredient\"]:checked");

        let name = selectedIngredient.getAttribute("data-name")
        let imageFileName = selectedIngredient.getAttribute("data-imageFileName");
        let quantity = formData.get("quantity");
        quantity = Number.parseFloat(quantity.toString());
        let unitMeasurement = formData.get("unitMeasurement");
        let purchaseDate = formData.get("purchaseDate");

        let result = await this.recipeServiceClient.addFridgeItem(
            userId,
            name,
            purchaseDate,
            quantity,
            unitMeasurement,
            imageFileName,
            this.errorHandler
        );
        if (result) {
            this.renderFridgeItems();
            this.closePopup(event);
        } else {

        }
    }

    async updateFridgeItem(event) {
        event.preventDefault();
        let updateFridgeItemForm = event.target;
        let formData = new FormData(updateFridgeItemForm);
        let name = updateFridgeItemForm.getAttribute("data-itemName");
        let imageFileName = updateFridgeItemForm.getAttribute("data-imageFileName");
        let quantity = formData.get("quantity");
        let unitMeasurement = formData.get("unitMeasurement");
        let purchaseDate = formData.get("purchaseDate");
        let response = await this.recipeServiceClient.updateFridgeItem(userId, name, purchaseDate, quantity, unitMeasurement, imageFileName, this.errorHandler);
        if (response) {
            this.toggleEdit(event, true);
        } else {
            this.showMessage("Error updating item");
        }
    }

    async removeFridgeItem(event) {
        let deleteBtn = event.target;
        let name = deleteBtn.getAttribute("data-itemName");

        let response = await this.recipeServiceClient.removeFridgeItem(userId, name, this.errorHandler);
        if (response) {
            this.renderFridgeItems();
        } else {
            this.showMessage("Error removing item");
        }
    }

    async displayAddFridgeItem(event) {
        let popupsContainer = document.querySelector(".popups-container");
        popupsContainer.innerHTML =
            `
             <div class="addFridgeItem-container popup">
                <button type="button" class="close-popup-btn">x</button>
                <form class="addFridgeItem-form form" id="addFridgeItem-form">
                    <div class="form-group">
                        <p>Search Ingredients</p>
                        <input placeholder="Apple" name="name" id="searchBar-ingredients">
                        <div class="search-results searchResults-ingredients" style="justify-content: center">
                            <h2>No search results</h2>
                        </div>
                    </div>
                    <div class="form-group">
                        <p>Quantity</p>
                        <input required min="0.01" value="1.00" step="0.01" type="number" name="quantity">
                    </div>
                    <div class="form-group">
                        <p>Unit Measurement</p>
                        <select required name="unitMeasurement">
                            <option value="g">g</option>
                            <option value="oz">oz</option>
                            <option value="lb">lb</option>
                            <option value="tsp">tsp</option>
                            <option value="tbsp">tbsp</option>
                            <option value="cup">cup</option>
                            <option value="gallon">gallon</option>
                            <option value="each">each</option>
                            <option value="ct">ct</option>
                            <option value="box">box</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <p>Purchase Date</p>
                        <input required type="date" name="purchaseDate">
                    </div>
                    <button type="submit" id="submit" disabled>Submit</button>
                </form>
            </div>
            `;
        document.querySelector(".close-popup-btn").addEventListener("click", this.closePopup);
        document.querySelector(".addFridgeItem-form").addEventListener("submit", this.addFridgeItem);
        document.getElementById("searchBar-ingredients").addEventListener("change", this.renderIngredientSearchResults);
        this.showPopup(event);
    }


    async updateProfile(event) {
        event.preventDefault();
        let editProfileForm = event.target;
        let formData = new FormData(editProfileForm);

        let name = formData.get("name");
        name = name.replaceAll(/[`!?~@#()$^*\`\'\"_;<>\{\}\[\]\\\/]/gi, '');
        let email = formData.get("email");
        email = email.replaceAll(/[`!?~@#()$^*\`\'\"_;<>\{\}\[\]\\\/]/gi, '');
        let dietaryRestrictions = formData.getAll("dietaryRestriction");


        let response = this.profileServiceClient.updateProfile(userId, name, email, dietaryRestrictions, this.errorHandler);

        this.toggleEdit(event, true);

        if (response) {
        } else {
            this.showMessage("Error updating profile...");
        }
    }

    toggleEdit(event, update = false) {
        let target = event.target;
        let editButtons;
        let fieldset;
        if (target.tagName === "FORM") {
            editButtons = target.querySelector(".editButtons");
        } else {
            editButtons = target.closest(".editButtons");
        }

        fieldset = editButtons.nextElementSibling;

        if (fieldset.disabled) {
            fieldset.disabled = false;
            editButtons.querySelectorAll(".edit-button, .delete-button").forEach(btn => btn.style.display = "none");
            editButtons.querySelectorAll(".save_cancel-buttons").forEach(btn => btn.style.display = "inline");
        } else {
            fieldset.disabled = true;
            if (!update) {
                fieldset.closest("form").reset();
            }
            editButtons.querySelectorAll(".edit-button, .delete-button").forEach(btn => btn.style.display = "inline");
            editButtons.querySelectorAll(".save_cancel-buttons").forEach(btn => btn.style.display = "none");
        }
    }


}

const main = async () => {
    const profilePage = new ProfilePage();
    await profilePage.mount();
}
window.addEventListener('DOMContentLoaded', main);