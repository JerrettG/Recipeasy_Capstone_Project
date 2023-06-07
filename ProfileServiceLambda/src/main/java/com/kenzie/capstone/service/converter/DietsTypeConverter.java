package com.kenzie.capstone.service.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;

import java.util.List;
import java.util.stream.Collectors;
public class DietsTypeConverter implements DynamoDBTypeConverter<List<String>, List<Diet>> {


    @Override
    public List<String> convert(List<Diet> diets) {
        return diets.stream()
                .map(Diet::getDiet)
                .collect(Collectors.toList());
    }

    @Override
    public List<Diet> unconvert(List<String> diets) {
        return diets.stream()
                .map(Diet::getDietFromString)
                .collect(Collectors.toList());
    }
}
