package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Cuisine {

    AFRICAN("African"),
    AMERICAN("American"),
    BRITISH("British"),
    CAJUN("Cajun"),
    CARIBBEAN("Caribbean"),
    CHINESE("Chinese"),
    EASTERN("Eastern"),
    EUROPEAN("European"),
    FRENCH("French"),
    GERMAN("German"),
    GREEK("Greek"),
    INDIAN("Indian"),
    IRISH("Irish"),
    ITALIAN("Italian"),
    JAPANESE("Japanese"),
    JEWISH("Jewish"),
    KOREAN("Korean"),
    LATIN_AMERICAN("Latin American"),
    MEDITERRANEAN("Mediterranean"),
    MEXICAN("Mexican"),
    MIDDLE_EASTERN("Middle Eastern"),
    NORDIC("Nordic"),
    SOUTHERN("Southern"),
    THAI("Thai"),
    VIETNAMESE("Vietnamese");

    private final String cuisine;
    public static List<String> getAllCuisinesAsList() {
        return Arrays.stream(Cuisine.values())
                .map(Cuisine::getCuisine)
                .collect(Collectors.toUnmodifiableList());
    }

    Cuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCuisine() {
        return cuisine;
    }

}
