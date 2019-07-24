package com.test.loremflickr.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.test.loremflickr.App;
import com.test.loremflickr.R;
import com.test.loremflickr.api.ApiClient;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.presenters.DetailsPresenter;
import com.test.loremflickr.views.DetailsView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends MvpAppCompatActivity implements DetailsView {

    private static final String EXTRA_THUMB = "extra.thumb";
    private static final String EXTRA_TAG = "extra.tag";
    private static final String EXTRA_LOCK = "extra.lock";
    private static final String EXTRA_AUTHOR = "extra.author";

    @BindView(R.id.image_view_details)
    ImageView imageView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_author_details)
    TextView authorText;

    @Inject
    ApiClient apiClient;

    @InjectPresenter
    DetailsPresenter presenter;

    @ProvidePresenter
    DetailsPresenter provideDetailsPresenter() {
        App.getInstance().getAppComponent().inject(this);
        return new DetailsPresenter(apiClient);
    }

    private String thumb;
    private String tag;
    private String author;
    private String imageLink;
    private int lock;
    private AlertDialog alertDialog;

    public static Intent newInstance(Context context, String thumb, String tag, int lock, String author) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(EXTRA_THUMB, thumb);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_AUTHOR, author);
        intent.putExtra(EXTRA_LOCK, lock);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        thumb = getIntent().getStringExtra(EXTRA_THUMB);
        tag = getIntent().getStringExtra(EXTRA_TAG);
        author = getIntent().getStringExtra(EXTRA_AUTHOR);
        lock = getIntent().getIntExtra(EXTRA_LOCK, 0);

        setSupportActionBar(toolbar);
        setupInitInfo();
        presenter.loadPhoto(tag, lock);
    }

    private void setupInitInfo() {
        authorText.setText(getResources().getString(R.string.author, author));
        Glide.with(this).load(thumb).into(imageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void showErrorDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.ooops)
                    .setMessage(R.string.something_went_wrong)
                    .setPositiveButton(R.string.try_again, (dialog, which) ->
                            presenter.loadPhoto(tag, lock))
                    .create();
        }
        if (!alertDialog.isShowing())
            alertDialog.show();
    }

    @Override
    public void showImageInfo(LoremFlickrImage image) {
        imageLink = image.getImage();
        Glide.with(this).asBitmap().load(image.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @OnClick(R.id.text_link_details)
    void onLinkClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageLink)));
    }
}
