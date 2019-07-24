package com.test.loremflickr.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.views.DetailsView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class DetailsPresenter extends MvpPresenter<DetailsView> {

    private ApiClient apiClient;
    private CompositeDisposable d = new CompositeDisposable();

    public DetailsPresenter(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void loadPhoto(String tag, int lock) {
        d.add(apiClient.getPhoto(false, tag, lock)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> getViewState().showImageInfo(image),
                        throwable -> {
                            getViewState().showErrorDialog();
                            throwable.printStackTrace();
                        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         d.dispose();
    }
}

