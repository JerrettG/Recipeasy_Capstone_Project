import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import RecipeServiceClient from "../api/recipeServiceClient";


export default class SearchPage extends BaseClass {


    constructor() {
        super();
        this.bindClassMethods([
            'includeIngredient','getFiltersAsCsv', 'getRecipes',
            "renderIncludedIngredients", 'renderRecipes','removeIngredient',
            'showLoading','toggleFilterVisibility', 'toggleMealTypeSelection',], this);
        this.dataStore = new DataStore();
        this.recipeServiceClient = new RecipeServiceClient();
    }

    async mount() {

        document.querySelectorAll(".dropdown")
            .forEach(dropdownTitle => dropdownTitle.addEventListener("click", this.toggleFilterVisibility));
        document.querySelectorAll(".mealTypeFilter")
            .forEach(filter => filter.addEventListener("change", this.toggleMealTypeSelection));
        document.getElementById("includeIngredients-form").addEventListener("submit", this.includeIngredient);
        document.getElementById("searchBar").addEventListener("submit", this.getRecipes);
    }

    async renderRecipes() {
        this.showLoading(false);
        const recipes = this.dataStore.get("recipes");
        let searchResults = document.getElementById("searchResults");
        let resultHtml = '';
        if (recipes && recipes.length > 0) {
            for (let recipe of recipes) {
                resultHtml +=
                    `
                    <div class="result-container">
                        <img src="${recipe.imageUrl}" alt="${recipe.name}" class="result-recipeImage" width="90" height="90">
                        <div class="result-recipeInfo">
                            <h3 class="result-recipeInfo-recipeName"><a href="/recipe/${recipe.name}?id=${recipe.id}">${recipe.name}</a></h3>
                            <span class="result-recipeInfo-summary">${recipe.summary}</span>
                            <span class="result-recipeInfo-readyInMinutes">Ready in: ${recipe.readyInMinutes} mins</span>
                        </div>
                    </div>
                    `;
            }
            searchResults.innerHTML = resultHtml;
        } else {
            searchResults.innerHTML = '<h1>No results</h1>'
        }
    }

    async getRecipes(event) {
        event.preventDefault();
        this.showLoading(true);

        // collect included ingredients
        const includedIngredients = this.dataStore.get("includedIngredients");
        // collect all cuisine filters that are checked
        let cuisinesCsv = Array.from(document.querySelectorAll(".cuisineFilter")).reduce(this.getFiltersAsCsv, '');
        cuisinesCsv = cuisinesCsv.substring(0, cuisinesCsv.length-1); // remove trailing comma
        // collect all dietary restriction filters using the AND connection of "," separated values
        let dietRestrictionsCsv = Array.from(document.querySelectorAll(".dietaryRestrictionFilter")).reduce(this.getFiltersAsCsv, '');
        dietRestrictionsCsv = dietRestrictionsCsv.substring(0, dietRestrictionsCsv.length-1); //remove trailing comma
        // meal types can only have one selected so the rest need to be disabled
        const mealType = Array.from(document.querySelectorAll(".mealTypeFilter")).filter(filter => filter.checked);

        let searchBar = document.getElementById("searchBar");
        // retrieve the search term and add it to query
        let searchTerm = new FormData(searchBar).get("searchTerm");
        searchTerm = searchTerm.replaceAll(/[`!?~@#()$^*\`\'\"_;<>\{\}\[\]\\\/]/gi, '');
        let map = new Map();
        if (searchTerm) {
            map.set("query", searchTerm);
        }
        if (includedIngredients) {
            let includedIngredientsCsv = includedIngredients.reduce((csv, ingredient) => csv += `${ingredient},`, '');
            includedIngredientsCsv = includedIngredientsCsv.substring(0, includedIngredientsCsv.length-1); // remove trailing comma
            map.set("includeIngredients", includedIngredientsCsv);
            map.set("sort", "max-used-ingredients");
        }
        if (cuisinesCsv) {
            map.set("cuisine", cuisinesCsv);
        }
        if (dietRestrictionsCsv) {
            map.set("diet", dietRestrictionsCsv);
        }
        if (mealType.length === 1) {
            map.set("type", mealType[0].value);
        }

        const results = await this.recipeServiceClient.getRecipes(map, this.errorHandler);

        if (results) {
            this.dataStore.set("recipes", results);
            await this.renderRecipes();
        } else {
            this.showMessage("Error getting recipes");
        }
    }

    getFiltersAsCsv(csv, filter) {
        return csv += filter.checked ? `${filter.value},` : '';
    }

    async toggleMealTypeSelection(event) {
        let selected = event.target;

        const mealTypes = document.querySelectorAll(".mealTypeFilter");

        for (let mealType of mealTypes) {
            if (mealType !== selected) {
                mealType.disabled = selected.checked;
            }
        }
    }

    async toggleFilterVisibility(event) {
        let filterTitle = event.target;

        if (filterTitle.tagName !== "I") {
            filterTitle = filterTitle.querySelector(".fa-caret-down");
        }
        let dropdownFilters = filterTitle.closest(".filters").querySelector(".dropdown-filters");

        if (dropdownFilters.ariaExpanded === "false") {
            dropdownFilters.ariaExpanded = "true";
            filterTitle.classList.add("dropdown-caret-right");
            dropdownFilters.classList.add("dropdown-filters-visible");
        } else {
            dropdownFilters.ariaExpanded = "false";
            filterTitle.classList.remove("dropdown-caret-right");
            dropdownFilters.classList.remove("dropdown-filters-visible");
        }
    }

    async renderIncludedIngredients(event) {
        let includedIngredientsSection = document.querySelector(".includedIngredients");
        let includedIngredients = this.dataStore.get("includedIngredients");

        if (includedIngredients) {
            let html = '';

            for (let ingredient of includedIngredients) {
                html += `<span data-ingredient="${ingredient}"class="includedIngredient">${ingredient}<button>X</button></span>`;
            }
            includedIngredientsSection.innerHTML = html;
            document.querySelectorAll(".includedIngredient button")
                .forEach( ingredient => ingredient.addEventListener("click", this.removeIngredient));
        }
    }
    async includeIngredient(event) {
        event.preventDefault();
        let includeIngredientForm = event.target;
        let includeIngredientFormData = new FormData(includeIngredientForm);
        let ingredient = includeIngredientFormData.get("includeIngredient");
        ingredient = ingredient.replaceAll(/[`!?~@#()$^*\`\'\"_;<>\{\}\[\]\\\/]/gi, '');
        let includedIngredients = this.dataStore.get("includedIngredients");
        if (includedIngredients) {
            let isIngredientIncluded = includedIngredients.indexOf(ingredient) !== -1;
            if (!isIngredientIncluded) {
                includedIngredients.push(ingredient);
            }
        } else {
            includedIngredients = Array.of(ingredient);
            this.dataStore.set("includedIngredients", includedIngredients);
        }
        includeIngredientForm.reset();
        await this.renderIncludedIngredients();
    }

    async removeIngredient(event) {
        let ingredient = event.target.closest(".includedIngredient").getAttribute("data-ingredient");
        let includedIngredients = this.dataStore.get("includedIngredients");
        includedIngredients.splice(includedIngredients.indexOf(ingredient),1);
        await this.renderIncludedIngredients();
    }

}


const main = async () => {
    const recipesPage = new SearchPage();
    await recipesPage.mount();
}

window.addEventListener('DOMContentLoaded', main);
