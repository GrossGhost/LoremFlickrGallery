package com.test.loremflickr.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.test.loremflickr.Constants;
import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.views.MainView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private List<LoremFlickrImage> currentImages = new ArrayList<>();

    private ApiClient apiClient;
    private String currentTag = "sea";
    private int currentPage = 0;
    private CompositeDisposable d = new CompositeDisposable();

    public MainPresenter(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<LoremFlickrImage> getCurrentImages() {
        return currentImages;
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(String currentTag) {
        this.currentTag = currentTag;
        currentPage = 0;
        d.dispose();
        currentImages.clear();
        getViewState().clearAdapter();

        getPhotos();
    }

    public void getPhotos() {
        int from = currentPage * Constants.PER_PAGE;
        int to = from + Constants.PER_PAGE;
        List<Integer> locks = getLocksList(from, to);

        currentPage += 1;
        getViewState().onLoadingStart();
        d.add(Observable.fromIterable(locks)
                .flatMap(i -> apiClient.getPhoto(true, currentTag, i))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .toObservable()
                .subscribe(images -> {
                            currentImages.addAll(images);
                            getViewState().onLoadingEnd();
                            getViewState().showImages(images);
                        },
                        throwable -> {
                            getViewState().onLoadingEnd();
                            getViewState().showErrorDialog();
                            throwable.printStackTrace();
                        }));
    }

    private List<Integer> getLocksList(int from, int to) {
        List<Integer> locks = new ArrayList<>(30);
        for (int i = from; i < to; i++) {
            locks.add(i);
        }
        return locks;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        d.dispose();
    }
}

