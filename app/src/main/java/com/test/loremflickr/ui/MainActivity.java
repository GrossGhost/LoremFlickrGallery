package com.test.loremflickr.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.SharedElementCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.test.loremflickr.App;
import com.test.loremflickr.Constants;
import com.test.loremflickr.R;
import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.presenters.MainPresenter;
import com.test.loremflickr.utils.EndlessRecyclerViewScrollListener;
import com.test.loremflickr.utils.GridAutofitLayoutManager;
import com.test.loremflickr.views.MainView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    private static final String EXTRA_LAST_CLICK_POSITION = "extra.last.position";

    @BindView(R.id.recycler_main)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.progress_main)
    ProgressBar progressBar;

    @Inject
    ApiClient apiClient;

    @Inject
    @InjectPresenter
    MainPresenter presenter;

    @ProvidePresenter
    MainPresenter provideMainPresenter() {
        return presenter;
    }

    private ImageAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private AlertDialog alertDialog;
    private int lastClickedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getInstance().getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initViews();
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(EXTRA_LAST_CLICK_POSITION);
            recyclerView.scrollToPosition(position);

            adapter.setItems(presenter.getCurrentImages());
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
            presenter.getPhotos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_LAST_CLICK_POSITION, lastClickedPosition);
        super.onSaveInstanceState(outState);
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
                presenter.getPhotos();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.setCurrentTag(query);
                presenter.getPhotos();
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

        Intent intent = DetailsActivity.newInstance(MainActivity.this, image.getImage(),
                presenter.getCurrentTag(), image.getLock(), image.getOwner());

        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(MainActivity.this, view, getResources().getString(R.string.image_transition));

        startActivity(intent, options.toBundle());
    }

    @Override
    public void onLoadingStart() {
        scrollListener.setLoading(true);
        crossfadeAnimation(progressBar, true);
    }

    @Override
    public void onLoadingEnd() {
        scrollListener.setLoading(false);
        crossfadeAnimation(progressBar, false);
    }

    private void crossfadeAnimation(View v, boolean appearing) {
        if (appearing) {
            v.setAlpha(0f);
            v.setVisibility(View.VISIBLE);
            v.animate()
                    .alpha(1f)
                    .setDuration(Constants.shortAnimationDuration)
                    .setListener(null);
        } else {
            v.animate()
                    .alpha(0f)
                    .setDuration(Constants.shortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            v.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void showErrorDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.ooops)
                    .setMessage(R.string.something_went_wrong)
                    .setPositiveButton(R.string.try_again, (dialog, which) -> presenter.getPhotos())
                    .create();
        }
        if (!alertDialog.isShowing())
            alertDialog.show();
    }

    @Override
    public void showImages(List<LoremFlickrImage> images) {
        adapter.addItems(images);
    }

    @Override
    public void clearAdapter() {
        adapter.clear();
    }
}
