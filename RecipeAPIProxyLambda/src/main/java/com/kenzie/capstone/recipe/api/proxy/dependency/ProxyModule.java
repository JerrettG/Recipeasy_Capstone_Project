package com.kenzie.capstone.recipe.api.proxy.dependency;

import com.kenzie.capstone.recipe.api.proxy.RecipeApiProxy;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;
@Module
public class ProxyModule {

    @Singleton
    @Provides
    @Inject
    public RecipeApiProxy provideRecipeApiProxy() {
        return new RecipeApiProxy();
    }
}
