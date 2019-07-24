package com.test.loremflickr;

import java.util.concurrent.TimeUnit;

public final class Constants {

    public static final String BASE_URL = "https://loremflickr.com/";
    public static final int PER_PAGE = 30;
    public static final int shortAnimationDuration = 1000;

    public interface Image{
        int THUMB_SIZE = 300;
        int BIG_RESOLUTION_SIZE = 720;
    }
    public interface Http {
        int TIME_OUT = 30;
        int TIME_OUT_CONNECT = TIME_OUT;
        int TIME_OUT_READ = TIME_OUT;
        int TIME_OUT_WRITE = TIME_OUT;
        TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    }
}
