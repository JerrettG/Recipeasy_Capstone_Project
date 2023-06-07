package com.kenzie.capstone.service.model.recipe.proxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ValidIngredientQueryParam {

    QUERY("query", Collections.emptyList()),
    ADD_CHILDREN("addChildren", getBooleansAsList()),
    META_INFORMATION("metaInformation", getBooleansAsList());


    private final Map.Entry<String, List<String>> validQueryParam;
    ValidIngredientQueryParam(String key, List<String> value) {
        this.validQueryParam = Map.entry(key, value);
    }

    /**
     *  Creates and returns an unmodifiable map of all query parameters that are considered
     *  valid by the external API being used
     * @return An unmodifiable map with key, valid query parameter key, and value, a List of all valid values for specified key
     */
    public static Map<String, List<String>> getAllValidQueryParams() {
        return Arrays.stream(ValidIngredientQueryParam.values())
                .collect(Collectors
                        .toUnmodifiableMap(
                                ValidIngredientQueryParam::getValidQueryParamKey,
                                ValidIngredientQueryParam::getValidQueryParamValue
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
