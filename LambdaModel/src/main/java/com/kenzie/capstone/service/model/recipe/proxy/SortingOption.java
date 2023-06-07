package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SortingOption {

    META_SCORE("meta-score"),
    POPULARITY("popularity"),
    HEALTHINESS("healthiness"),
    PRICE("price"),
    TIME("time"),
    RANDOM("random"),
    MAX_USED_INGREDIENTS("max-used-ingredients"),
    MIN_MISSING_INGREDIENTS("min-missing-ingredients"),
    ALCOHOL("alcohol"),
    CAFFEINE("caffeine"),
    COPPER("copper"),
    ENERGY("energy"),
    CALORIES("calories"),
    CALCIUM("calcium"),
    CARBOHYDRATES("carbohydrates"),
    CARBS("carbs"),
    CHOLINE("choline"),
    CHOLESTEROL("cholesterol"),
    TOTAL_FAT("total-fat"),
    FLUORIDE("fluoride"),
    TRANS_FAT("trans-fat"),
    SATURATED_FAT("saturated-fat"),
    MONO_UNSATURATED_FAT("mono-unsaturated-fat"),
    POLY_UNSATURATED_FAT("poly-unsaturated-fat"),
    FIBER("fiber"),
    FOLATE("folate"),
    FOLIC_ACID("folic-acid"),
    IODINE("iodine"),
    IRON("iron"),
    MAGNESIUM("magnesium"),
    MANGANESE("manganese"),
    VITAMIN_B3("vitamin-b3"),
    NIACIN("niacin"),
    VITAMIN_B5("vitamin-b5"),
    PANTOTHENIC_ACID("pantothenic-acid"),
    PHOSPHORUS("phosphorus"),
    POTASSIUM("potassium"),
    PROTEIN("protein"),
    VITAMIN_B2("vitamin-b2"),
    RIBOFLAVIN("riboflavin"),
    SELENIUM("selenium"),
    SODIUM("sodium"),
    VITAMIN_B1("vitamin-b1"),
    THIAMIN("thiamin"),
    VITAMIN_A("vitamin-a"),
    VITAMIN_B6("vitamin-b6"),
    VITAMIN_B12("vitamin-b12"),
    VITAMIN_C("vitamin-c"),
    VITAMIN_D("vitamin-d"),
    VITAMIN_E("vitamin-e"),
    VITAMIN_K("vitamin-k"),
    SUGAR("sugar"),
    ZINC("zinc");

    private final String sortingOption;

    SortingOption(String sortingOption) {
        this.sortingOption = sortingOption;
    }

    public String getSortingOption() {
        return sortingOption;
    }

    public static List<String> getAllSortingOptionsAsList() {
        return Arrays.stream(SortingOption.values()).map(SortingOption::getSortingOption)
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<String> getSortDirectionsAsList() {
        return Stream.of("asc", "desc").collect(Collectors.toUnmodifiableList());
    }
}
