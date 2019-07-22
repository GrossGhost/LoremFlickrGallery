package com.test.loremflickr.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.test.loremflickr.App;
import com.test.loremflickr.Constants;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.R;
import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.utils.GridAutofitLayoutManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_LIST = "extra.image.list";
    private static final String EXTRA_LAST_CLICK_POSITION = "extra.last.position";

    @BindView(R.id.recycler_main)
    RecyclerView recyclerView;

    @Inject
    ApiClient apiClient;

    private ImageAdapter adapter;
    private int page = 0;
    private int lastClickedPosition;
    private String query = "sea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

        initViews();
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(EXTRA_LAST_CLICK_POSITION);
            adapter.setItems(savedInstanceState.getParcelableArrayList(EXTRA_IMAGE_LIST));
            recyclerView.scrollToPosition(position);

            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (sharedElements.isEmpty()) {
                        View view = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(position);
                        if (view != null) {
                            sharedElements.put(names.get(0), view);
                        }
                    }
                }
            });
        }
        else
            getPhoto(page);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_IMAGE_LIST, adapter.getItems());
        outState.putInt(EXTRA_LAST_CLICK_POSITION, lastClickedPosition);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        adapter = new ImageAdapter(this::onImageClick);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridAutofitLayoutManager(this,
                (int) getResources().getDimension(R.dimen.item_image_main_width)));
    }

    private void onImageClick(LoremFlickrImage image, int position, View view) {
        lastClickedPosition = position;

        Intent intent = DetailsActivity.newInstance(this, image.getImage(), query, image.getLock());

        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(MainActivity.this, view, getResources().getString(R.string.image_transition));

        startActivity(intent, options.toBundle());
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
                    }, Throwable::printStackTrace);
        }
    }
}
