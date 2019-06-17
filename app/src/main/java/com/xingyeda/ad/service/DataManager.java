package com.xingyeda.ad.service;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.xingyeda.ad.BaseApplication;
import com.xingyeda.ad.vo.MsgInfo;
import com.xingyeda.ad.vo.VersionInfo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class DataManager {

    public static final String TAG = "DataManager";

    private RetrofitService mRetrofitService;

    public DataManager(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BaseApplication.www + "")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        this.mRetrofitService = mRetrofit.create(RetrofitService.class);
    }


    public Observable<ResponseBody> register(String url) {
        Log.e("data manager register", url);
        return mRetrofitService.register(url);
    }


    public Observable<MsgInfo> getAnnouncement(String url) {
        return mRetrofitService.getAnnouncement(url);
    }


    public Observable<VersionInfo> getVersion(String url) {
        return mRetrofitService.getVersion(url);
    }

}