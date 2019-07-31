package com.test.loremflickr.dagger.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.loremflickr.api.ApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApiModule {
    @Singleton
    @Provides
    ApiClient providesApi(Gson gson) {
        return new ApiClient(gson);
    }

    @Singleton
    @Provides
    Gson providesGson() {
        return new GsonBuilder().create();
    }

}
