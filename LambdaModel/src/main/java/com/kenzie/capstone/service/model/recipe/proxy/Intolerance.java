package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Intolerance {

    DAIRY("Dairy"),
    EGG("Egg"),
    GLUTEN("Gluten"),
    GRAIN("Grain"),
    PEANUT("Peanut"),
    SEAFOOD("Seafood"),
    SESAME("Sesame"),
    SHELLFISH("Shellfish"),
    SOY("Soy"),
    SULFITE("Sulfite"),
    TREE_NUT("Tree Nut"),
    WHEAT("Wheat");

    private final String intolerance;

    Intolerance(String intolerance) {
        this.intolerance = intolerance;
    }

    public String getIntolerance() {
        return intolerance;
    }
    public static List<String> getAllIntolerancesAsList() {
        return Arrays.stream(Intolerance.values()).map(Intolerance::getIntolerance)
                .collect(Collectors.toUnmodifiableList());
    }
}
