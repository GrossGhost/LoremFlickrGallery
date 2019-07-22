package com.test.loremflickr.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.test.loremflickr.App;
import com.test.loremflickr.Constants;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.R;
import com.test.loremflickr.api.ApiClient;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_main)
    RecyclerView recyclerView;

    @Inject
    ApiClient apiClient;

    private ImageAdapter adapter;
    private int page = 0;
    private String query = "sea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

        initViews();
        getPhoto(page);
    }

    private void initViews() {
        adapter = new ImageAdapter(this::onImageClick);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void onImageClick(LoremFlickrImage image) {
    }

    private void getPhoto(int page) {
        int from = page * Constants.PER_PAGE;
        int to = from + Constants.PER_PAGE;

        for (int i = from; i < to; i++){
            int lock = i;
            apiClient.getPhoto(true, query, lock)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(image -> {
                        image.setLock(lock);
                        adapter.addItem(image);
                    });
        }
    }
}
