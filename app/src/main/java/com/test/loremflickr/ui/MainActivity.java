package com.test.loremflickr.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.SharedElementCallback;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.test.loremflickr.App;
import com.test.loremflickr.Constants;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.R;
import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.utils.EndlessRecyclerViewScrollListener;
import com.test.loremflickr.utils.GridAutofitLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final String EXTRA_IMAGE_LIST = "extra.image.list";
    private static final String EXTRA_LAST_CLICK_POSITION = "extra.last.position";
    private static final String EXTRA_CURRENT_PAGE = "extra.curr.page";

    @BindView(R.id.recycler_main)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    @Inject
    ApiClient apiClient;

    private ImageAdapter adapter;
    private int currentPage = 0;
    private int lastClickedPosition;
    private String tag = "sea";
    private EndlessRecyclerViewScrollListener scrollListener;
    private AlertDialog alertDialog;
    private Disposable d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

        initViews();
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(EXTRA_LAST_CLICK_POSITION);
            currentPage = savedInstanceState.getInt(EXTRA_CURRENT_PAGE);
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
        } else
            getPhotos();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_IMAGE_LIST, adapter.getItems());
        outState.putInt(EXTRA_LAST_CLICK_POSITION, lastClickedPosition);
        outState.putInt(EXTRA_CURRENT_PAGE, currentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        adapter = new ImageAdapter(this::onImageClick);

        recyclerView.setAdapter(adapter);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(this,
                (int) getResources().getDimension(R.dimen.item_image_main_width));
        recyclerView.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("TAG_LOAD", "loading");
                getPhotos();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                d.dispose();
                tag = query;
                adapter.clear();
                currentPage = 0;
                getPhotos();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void onImageClick(LoremFlickrImage image, int position, View view) {
        lastClickedPosition = position;

        Intent intent = DetailsActivity.newInstance(this, image.getImage(), tag, image.getLock());

        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(MainActivity.this, view, getResources().getString(R.string.image_transition));

        startActivity(intent, options.toBundle());
    }

    private void getPhotos() {
        int from = currentPage * Constants.PER_PAGE;
        int to = from + Constants.PER_PAGE;
        List<Integer> locks = getLocksList(from, to);

        d = Observable.fromIterable(locks)
                .flatMap(i -> apiClient.getPhoto(true, tag, i))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> scrollListener.setLoading(true))
                .doOnComplete(() -> {
                    currentPage += 1;
                    scrollListener.onScrolled(recyclerView, recyclerView.getScrollX(), recyclerView.getScrollY());
                })
                .doFinally(() -> scrollListener.setLoading(false))
                .subscribe(image -> adapter.addItem(image),
                        throwable -> {
                            showErrorDialog();
                            throwable.printStackTrace();
                        });

        disposables.add(d);
    }

    private void showErrorDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.ooops)
                    .setMessage(R.string.something_went_wrong)
                    .setPositiveButton(R.string.try_again, (dialog, which) -> getPhotos())
                    .create();
        }
        if (!alertDialog.isShowing())
            alertDialog.show();
    }

    private List<Integer> getLocksList(int from, int to) {
        List<Integer> locks = new ArrayList<>(30);
        for (int i = from; i < to; i++) {
            locks.add(i);
        }

        return locks;
    }
}
