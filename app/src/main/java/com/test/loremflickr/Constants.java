package com.test.loremflickr;

import java.util.concurrent.TimeUnit;

public final class Constants {

    public static String BASE_URL = "https://loremflickr.com/";

    public interface Http {
        int TIME_OUT = 30;
        int TIME_OUT_CONNECT = TIME_OUT;
        int TIME_OUT_READ = TIME_OUT;
        int TIME_OUT_WRITE = TIME_OUT;
        TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    }
}
