package com.kenzie.capstone.service.dependency;

import com.kenzie.capstone.service.ProfileService;

import com.kenzie.capstone.service.dao.CachingProfileDao;
import com.kenzie.capstone.service.dao.NonCachingProfileDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Module(
    includes = DaoModule.class
)
public class ServiceModule {

    @Singleton
    @Provides
    @Inject
    public ProfileService provideProfileService(@Named("CachingProfileDao")CachingProfileDao cachingProfileDao) {
        return new ProfileService(cachingProfileDao);
    }
}

