package com.test.loremflickr.ui;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity {
    protected final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
