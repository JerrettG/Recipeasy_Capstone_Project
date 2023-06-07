package com.kenzie.capstone.service.model.recipe.proxy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProxyRecipeResponseDeserializer extends StdDeserializer<ProxyRecipeResponse> {

    public ProxyRecipeResponseDeserializer() {
        super(ProxyRecipeResponse.class);
    }

    @Override
    public ProxyRecipeResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode recipeNode = jp.getCodec().readTree(jp);
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ProxyRecipeResponse proxyRecipeResponse = new ProxyRecipeResponse();

        proxyRecipeResponse.setId(recipeNode.get("id").asInt());
        proxyRecipeResponse.setName(recipeNode.get("title").asText());
        proxyRecipeResponse.setImageUrl(recipeNode.get("image").asText());
        proxyRecipeResponse.setImageSourceUrl(recipeNode.get("sourceUrl").asText());
        proxyRecipeResponse.setReadyInMinutes(recipeNode.get("readyInMinutes").asInt());
        proxyRecipeResponse.setSummary(recipeNode.get("summary").asText());
        JsonNode analyzedInstructions = recipeNode.get("analyzedInstructions");
        List<ProxyInstructionResponse> instructions  = analyzedInstructions.size() > 0 ? objectMapper.convertValue(
                analyzedInstructions.get(0).get("steps"),
                new TypeReference<List<ProxyInstructionResponse>>() {
                })
                : new ArrayList<>();
        proxyRecipeResponse.setInstructions(instructions);
        proxyRecipeResponse.setIngredientList(
                objectMapper.convertValue(recipeNode.get("extendedIngredients"), new TypeReference<List<ProxyIngredientResponse>>() {
                })
        );

        return proxyRecipeResponse;
    }
}
