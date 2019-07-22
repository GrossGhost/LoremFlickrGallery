package com.test.loremflickr.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LoremFlickrImage implements Parcelable {
    private String file;
    private String owner;
    private int lock;

    protected LoremFlickrImage(Parcel in) {
        file = in.readString();
        owner = in.readString();
        lock = in.readInt();
    }

    public String getImage() {
        return file;
    }

    public String getOwner() {
        return owner;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public static final Creator<LoremFlickrImage> CREATOR = new Creator<LoremFlickrImage>() {
        @Override
        public LoremFlickrImage createFromParcel(Parcel in) {
            return new LoremFlickrImage(in);
        }

        @Override
        public LoremFlickrImage[] newArray(int size) {
            return new LoremFlickrImage[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(file);
        parcel.writeString(owner);
        parcel.writeInt(lock);
    }
}
