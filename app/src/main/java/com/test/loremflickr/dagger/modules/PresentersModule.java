package com.test.loremflickr.dagger.modules;

import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.presenters.DetailsPresenter;
import com.test.loremflickr.presenters.MainPresenter;


import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    MainPresenter providesMainPresenter(ApiClient apiClient){
        return new MainPresenter(apiClient);
    }

    @Provides
    DetailsPresenter providesDetailsPresenter(ApiClient apiClient){
        return new DetailsPresenter(apiClient);
    }
}
