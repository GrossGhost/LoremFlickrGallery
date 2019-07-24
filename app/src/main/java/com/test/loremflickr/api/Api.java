package com.test.loremflickr.api;

import com.test.loremflickr.model.LoremFlickrImage;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @GET("/json/{width}/{height}/{tag}")
    Observable<LoremFlickrImage> getImage(
            @Path("width") int width, @Path("height") int height,
            @Path("tag") String tag, @Query("lock") int lock);
}
