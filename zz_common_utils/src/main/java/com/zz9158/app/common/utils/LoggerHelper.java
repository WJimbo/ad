package com.zz9158.app.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.zz9158.app.common.BuildConfig;

/**
 * Created by winnie on 2017/12/21.
 */

public class LoggerHelper{
    public static void init(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("XYDLOG")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * General log function that accepts all configurations as parameter
     */
    public static void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        Logger.log(priority, tag, message, throwable);
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        Logger.d(message, args);
    }

    public static void d(@Nullable Object object) {
        Logger.d(object);
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        Logger.e(null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        Logger.e(throwable, message, args);
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        Logger.i(message, args);
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        Logger.v(message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        Logger.w(message, args);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        Logger.wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        Logger.json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        Logger.xml(xml);
    }
}
