package com.test.loremflickr.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.test.loremflickr.App;
import com.test.loremflickr.R;
import com.test.loremflickr.api.ApiClient;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_THUMB = "extra.thumb";
    private static final String EXTRA_TAG = "extra.tag";
    private static final String EXTRA_LOCK = "extra.lock";

    @BindView(R.id.image_view_details)
    ImageView photoView;

    @Inject
    ApiClient apiClient;

    private String thumb;
    private String tag;
    private int lock;
    private AlertDialog alertDialog;

    public static Intent newInstance(Context context, String thumb, String tag, int lock){
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(EXTRA_THUMB, thumb);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_LOCK, lock);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

        thumb = getIntent().getStringExtra(EXTRA_THUMB);
        tag = getIntent().getStringExtra(EXTRA_TAG);
        lock = getIntent().getIntExtra(EXTRA_LOCK, 0);

        setupView();
        loadPhoto();
    }

    private void loadPhoto() {
        apiClient.getPhoto(false, tag, lock)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> Glide.with(this).asBitmap().load(image.getImage())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        photoView.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                }),
                        throwable -> {
                            showErrorDialog();
                            throwable.printStackTrace();
                        });
    }


    private void setupView() {
        Glide.with(this).load(thumb).into(photoView);
    }

    private void showErrorDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.ooops)
                    .setMessage(R.string.something_went_wrong)
                    .setPositiveButton(R.string.try_again, (dialog, which) -> loadPhoto())
                    .create();
        }
        if (!alertDialog.isShowing())
            alertDialog.show();
    }
}
