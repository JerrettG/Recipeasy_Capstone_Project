package com.kenzie.capstone.service.dependency;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.kenzie.capstone.service.caching.CacheClient;
import com.kenzie.capstone.service.dao.CachingProfileDao;
import com.kenzie.capstone.service.dao.NonCachingProfileDao;
import com.kenzie.capstone.service.util.DynamoDbClientProvider;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides DynamoDBMapper instance to DAO classes.
 */
@Module
public class DaoModule {

    @Singleton
    @Provides
    @Named("DynamoDBMapper")
    public DynamoDBMapper provideDynamoDBMapper() {
        return new DynamoDBMapper(
                DynamoDbClientProvider.getDynamoDBClient(),
                DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES.config()
        );
    }

    @Singleton
    @Provides
    @Named("NonCachingProfileDao")
    @Inject
    public NonCachingProfileDao provideNonCachingProfileDao(@Named("DynamoDBMapper") DynamoDBMapper mapper) {
        return new NonCachingProfileDao(mapper);
    }

    @Singleton
    @Provides
    @Named("CachingProfileDao")
    @Inject
    public CachingProfileDao provideCachingProfileDao(@Named("CacheClient") CacheClient cacheClient,
                                                      @Named("NonCachingProfileDao")NonCachingProfileDao nonCachingProfileDao) {
        return new CachingProfileDao(cacheClient, nonCachingProfileDao);
    }

}
