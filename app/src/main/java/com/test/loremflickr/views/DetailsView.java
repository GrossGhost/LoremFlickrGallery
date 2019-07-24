package com.test.loremflickr.views;

import com.arellomobile.mvp.MvpView;
import com.test.loremflickr.model.LoremFlickrImage;


public interface DetailsView extends MvpView {
    void showErrorDialog();
    void showImageInfo(LoremFlickrImage image);
}
