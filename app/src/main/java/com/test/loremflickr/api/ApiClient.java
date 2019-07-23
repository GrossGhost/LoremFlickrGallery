package com.test.loremflickr.api;

import com.google.gson.Gson;
import com.test.loremflickr.Constants;
import com.test.loremflickr.model.LoremFlickrImage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private Api api;

    public ApiClient(Gson gson) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())
                .connectTimeout(Constants.Http.TIME_OUT_CONNECT, Constants.Http.TIME_UNIT)
                .readTimeout(Constants.Http.TIME_OUT_READ, Constants.Http.TIME_UNIT)
                .writeTimeout(Constants.Http.TIME_OUT_WRITE, Constants.Http.TIME_UNIT);


        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                .build();

        api = retrofit.create(Api.class);

    }

    public Observable<LoremFlickrImage> getPhoto(boolean isThumbnail, String tag, int lock){
        int size = isThumbnail ? Constants.Image.THUMB_SIZE : Constants.Image.BIG_RESOLUTION_SIZE;

        return api.getImage(size, size, tag, lock)
                .doOnNext(image -> image.setLock(lock));

    }

    public Observable<List<LoremFlickrImage>> getPhotos(int page, String tag){
        int from = page * Constants.PER_PAGE;
        int to = from + Constants.PER_PAGE;

        List<Integer> locks = getLocksList(from, to);

        return Observable.fromIterable(locks)
                .flatMap(i -> getPhoto(true, tag, i))
                .toList()
                .toObservable();



    }

    private List<Integer> getLocksList(int from, int to) {
        List<Integer> locks = new ArrayList<>(30);
        for (int i = from; i < to; i++)
            locks.add(i);

        return locks;
    }
}
