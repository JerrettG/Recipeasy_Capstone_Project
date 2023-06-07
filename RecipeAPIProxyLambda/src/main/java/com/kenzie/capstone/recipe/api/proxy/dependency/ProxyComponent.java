package com.kenzie.capstone.recipe.api.proxy.dependency;

import com.kenzie.capstone.recipe.api.proxy.RecipeApiProxy;

import dagger.Component;
import redis.clients.jedis.Jedis;

import javax.inject.Singleton;
/**
 * Declares the dependency roots that Dagger will provide.
 */
@Singleton
@Component(modules = {ProxyModule.class})
public interface ProxyComponent {
    RecipeApiProxy provideRecipeApiProxy();
}
