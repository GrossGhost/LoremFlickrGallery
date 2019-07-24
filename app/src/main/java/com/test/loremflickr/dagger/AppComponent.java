package com.test.loremflickr.dagger;

import com.test.loremflickr.dagger.modules.ApiModule;
import com.test.loremflickr.ui.DetailsActivity;
import com.test.loremflickr.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {ApiModule.class})
public interface AppComponent {
    void inject(DetailsActivity detailsActivity);

    void inject(MainActivity mainMVPActivity);
}
