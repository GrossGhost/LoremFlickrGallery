package com.test.loremflickr.views;

import com.arellomobile.mvp.MvpView;
import com.test.loremflickr.model.LoremFlickrImage;

import java.util.List;

public interface MainView extends MvpView {
    void onLoadingStart();
    void onLoadingEnd();
    void showErrorDialog();
    void showImages(List<LoremFlickrImage> images);
    void clearAdapter();
}
