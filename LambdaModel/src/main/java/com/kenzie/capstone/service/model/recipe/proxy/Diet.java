package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Diet {
    GLUTEN_FREE("Gluten Free"),
    KETOGENIC("Ketogenic"),
    VEGETARIAN("Vegetarian"),
    LACTO_VEGETARIAN("Lacto-Vegetarian"),
    OVO_VEGETARIAN("Ovo-Vegetarian"),
    VEGAN("Vegan"),
    PESECETARIAN("Pescetarian"),
    PALEO("Paleo"),
    PRIMAL("Primal"),
    LOW_FOD_MAP("Low FODMAP"),
    WHOLE30("Whole30");
    private final String diet;

    Diet(String diet) {
        this.diet = diet;
    }

    public String getDiet() {
        return diet;
    }
    public static List<String> getAllDietsAsList() {
        return Arrays.stream(Diet.values()).map(Diet::getDiet).collect(Collectors.toUnmodifiableList());
    }
    public static Diet getDietFromString(String diet) {
        return Arrays.stream(Diet.values())
                .filter(dietEnum -> dietEnum.getDiet().equals(diet))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
