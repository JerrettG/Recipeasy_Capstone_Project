package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ValidRecipeQueryParam {
    // Empty list means query parameter supports any value
    QUERY("query", Collections.emptyList()),
    CUISINE("cuisine", Cuisine.getAllCuisinesAsList()),
    MEAL_TYPE("type", MealType.getAllMealTypesAsList()),
    EXCLUDE_CUISINE("excludeCuisine", Cuisine.getAllCuisinesAsList()),
    DIET("diet", Diet.getAllDietsAsList()),
    INTOLERANCES("intolerances", Intolerance.getAllIntolerancesAsList()),
    EQUIPMENT("equipment", Collections.emptyList()),
    INCLUDE_INGREDIENTS("includeIngredients", Collections.emptyList()),
    EXCLUDE_INGREDIENTS("excludeIngredients", Collections.emptyList()),
    INSTRUCTIONS_REQUIRED("instructionsRequired", getBooleansAsList()),
    FILL_INGREDIENTS("fillIngredients", getBooleansAsList()),
    ADD_RECIPE_INFO("addRecipeInformation", getBooleansAsList()),
    AUTHOR("author", Collections.emptyList()),
    TAGS("tags", Collections.emptyList()),
    MAX_READY_TIME("maxReadyTime", Collections.emptyList()),
    IGNORE_PANTRY("ignorePantry", Collections.emptyList()),
    SORT("sort", SortingOption.getAllSortingOptionsAsList()),
    SORT_DIRECTION("sortDirection", SortingOption.getSortDirectionsAsList()),
    MIN_CARBS("minCarbs", Collections.emptyList()),
    MAX_CARBS("maxCarbs", Collections.emptyList()),
    MIN_PROTEIN("minProtein", Collections.emptyList()),
    MAX_PROTEIN("maxProtein", Collections.emptyList()),
    MIN_CALORIES("minCalories", Collections.emptyList()),
    MAX_CALORIES("maxCalories", Collections.emptyList()),
    MIN_FAT("minFat", Collections.emptyList()),
    MAX_FAT("maxFat", Collections.emptyList()),
    MIN_ALCOHOL("minAlcohol", Collections.emptyList()),
    MAX_ALCOHOL("maxAlcohol", Collections.emptyList()),
    MIN_CAFFEINE("minCaffeine", Collections.emptyList()),
    MAX_CAFFEINE("maxCaffeine", Collections.emptyList()),
    MIN_COPPER("minCopper", Collections.emptyList()),
    MAX_COPPER("maxCopper", Collections.emptyList()),
    MIN_CALCIUM("minCalcium", Collections.emptyList()),
    MAX_CALCIUM("maxCalcium", Collections.emptyList()),
    MIN_CHOLINE("minCholine", Collections.emptyList()),
    MAX_CHOLINE("maxCholine", Collections.emptyList()),
    MIN_CHOLESTEROL("minCholesterol", Collections.emptyList()),
    MAX_CHOLESTEROL("maxCholesterol", Collections.emptyList()),
    MIN_FLUORIDE("minFluoride", Collections.emptyList()),
    MAX_FLUORIDE("maxFluoride", Collections.emptyList()),
    MIN_SATURATED_FAT("minSaturatedFat", Collections.emptyList()),
    MAX_SATURATED_FAT("maxSaturatedFat", Collections.emptyList()),
    MIN_VITAMIN_A("minVitaminA", Collections.emptyList()),
    MAX_VITAMIN_A("maxVitaminA", Collections.emptyList()),
    MIN_VITAMIN_C("minVitaminC", Collections.emptyList()),
    MAX_VITAMIN_C("maxVitaminC", Collections.emptyList()),
    MIN_VITAMIN_D("minVitaminD", Collections.emptyList()),
    MAX_VITAMIN_D("maxVitaminD", Collections.emptyList()),
    MIN_VITAMIN_E("minVitaminE", Collections.emptyList()),
    MAX_VITAMIN_E("maxVitaminE", Collections.emptyList()),
    MIN_VITAMIN_K("minVitaminK", Collections.emptyList()),
    MAX_VITAMIN_K("maxVitaminK", Collections.emptyList()),
    MIN_VITAMIN_B1("minVitaminB1", Collections.emptyList()),
    MAX_VITAMIN_B1("maxVitaminB1", Collections.emptyList()),
    MIN_VITAMIN_B2("minVitaminB2", Collections.emptyList()),
    MAX_VITAMIN_B2("maxVitaminB2", Collections.emptyList()),
    MIN_VITAMIN_B5("minVitaminB5", Collections.emptyList()),
    MAX_VITAMIN_B5("maxVitaminB5", Collections.emptyList()),
    MIN_VITAMIN_B3("minVitaminB3", Collections.emptyList()),
    MAX_VITAMIN_B3("maxVitaminB3", Collections.emptyList()),
    MIN_VITAMIN_B6("minVitaminB6", Collections.emptyList()),
    MAX_VITAMIN_B6("maxVitaminB6", Collections.emptyList()),
    MIN_VITAMIN_B12("minVitaminB12", Collections.emptyList()),
    MAX_VITAMIN_B12("maxVitaminB12", Collections.emptyList()),
    MIN_FIBER("minFiber", Collections.emptyList()),
    MAX_FIBER("maxFiber", Collections.emptyList()),
    MIN_FOLATE("minFolate", Collections.emptyList()),
    MAX_FOLATE("maxFolate", Collections.emptyList()),
    MIN_FOLIC_ACID("minFolicAcid", Collections.emptyList()),
    MAX_FOLIC_ACID("maxFolicAcid", Collections.emptyList()),
    MIN_IODINE("minIodine", Collections.emptyList()),
    MAX_IODINE("maxIodine", Collections.emptyList()),
    MIN_IRON("minIron", Collections.emptyList()),
    MAX_IRON("maxIron", Collections.emptyList()),
    MIN_MAGNESIUM("minMagnesium", Collections.emptyList()),
    MAX_MAGNESIUM("maxMagnesium", Collections.emptyList()),
    MIN_MANGANESE("minManganese", Collections.emptyList()),
    MAX_MANGANESE("maxManganese", Collections.emptyList()),
    MIN_PHOSPHORUS("minPhosphorus", Collections.emptyList()),
    MAX_PHOSPHORUS("maxPhosphorus", Collections.emptyList()),
    MIN_POTASSIUM("minPotassium", Collections.emptyList()),
    MAX_POTASSIUM("maxPotassium", Collections.emptyList()),
    MIN_SELENIUM("minSelenium", Collections.emptyList()),
    MAX_SELENIUM("maxSelenium", Collections.emptyList()),
    MIN_SODIUM("minSodium", Collections.emptyList()),
    MAX_SODIUM("maxSodium", Collections.emptyList()),
    MIN_SUGAR("minSugar", Collections.emptyList()),
    MAX_SUGAR("maxSugar", Collections.emptyList()),
    MIN_ZINC("minZinc", Collections.emptyList()),
    MAX_ZINC("maxZinc", Collections.emptyList()),
    OFFSET("offset", Collections.emptyList()),
    NUMBER("number", Collections.emptyList()),
    LIMIT_LICENSE("limitLicense", getBooleansAsList());




    private final Map.Entry<String, List<String>> validQueryParam;
    ValidRecipeQueryParam(String key, List<String> value) {
        this.validQueryParam = Map.entry(key, value);
    }

    /**
     *  Creates and returns an unmodifiable map of all query parameters that are considered
     *  valid by the external API being used
     * @return An unmodifiable map with key, valid query parameter key, and value, a List of all valid values for specified key
     */
    public static Map<String, List<String>> getAllValidQueryParams() {
        return Arrays.stream(ValidRecipeQueryParam.values())
                .collect(Collectors
                        .toUnmodifiableMap(
                                ValidRecipeQueryParam::getValidQueryParamKey,
                                ValidRecipeQueryParam::getValidQueryParamValue
                        )
                );
    }
    public String getValidQueryParamKey() {
        return validQueryParam.getKey();
    }
    public List<String> getValidQueryParamValue() {
        return validQueryParam.getValue();
    }

    private static List<String> getBooleansAsList() {
        return Stream.of("true", "false").collect(Collectors.toUnmodifiableList());
    }
}
