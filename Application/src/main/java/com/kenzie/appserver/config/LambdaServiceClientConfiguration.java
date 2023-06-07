package com.kenzie.appserver.config;

import com.kenzie.capstone.service.client.ProfileServiceClient;
import com.kenzie.capstone.service.client.RecipeApiProxyClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LambdaServiceClientConfiguration {

    @Bean
    public RecipeApiProxyClient recipeApiProxyClient() {
        return new RecipeApiProxyClient();
    }
    @Bean
    public ProfileServiceClient profileServiceClient() {
        return new ProfileServiceClient();
    }
}
