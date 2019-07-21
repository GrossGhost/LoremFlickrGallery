package com.test.loremflickr;

import android.app.Application;

import com.test.loremflickr.dagger.AppComponent;
import com.test.loremflickr.dagger.DaggerAppComponent;
import com.test.loremflickr.dagger.modules.ApiModule;

public class App extends Application {

    private AppComponent appComponent;
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        appComponent = DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
