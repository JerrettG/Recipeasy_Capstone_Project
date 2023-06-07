package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MealType {

    MAIN_COURSE("main course"),
    SIDE_DISH("side dish"),
    DESSERT("dessert"),
    APPETIZER("appetizer"),
    SALAD("salad"),
    BREAD("bread"),
    BREAKFAST("breakfast"),
    SOUP("soup"),
    BEVERAGE("beverage"),
    SAUCE("sauce"),
    MARINADE("marinade"),
    FINGER_FOOD("fingerfood"),
    SNACK("snack"),
    DRINK("drink");

    public final String type;

    MealType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static List<String> getAllMealTypesAsList() {
        return Arrays.stream(MealType.values())
                .map(MealType::getType)
                .collect(Collectors.toUnmodifiableList());
    }
}
