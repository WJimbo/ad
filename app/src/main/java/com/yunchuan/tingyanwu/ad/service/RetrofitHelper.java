package com.yunchuan.tingyanwu.ad.service;

import android.content.Context;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.yunchuan.tingyanwu.ad.util.CrashApplication;
import com.yunchuan.tingyanwu.ad.util.PersistentCookieStore;
import com.yunchuan.tingyanwu.ad.util.ProgressInterceptor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private Context mCntext;


    GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitHelper instance = null;
    private Retrofit mRetrofit = null;
    public static RetrofitHelper getInstance(Context context){
        if (instance == null){
            instance = new RetrofitHelper(context);
        }
        return instance;
    }



    private RetrofitHelper(Context mContext){
        mCntext = mContext;
        init();
    }

    public static  void updateToken(){

        RetrofitHelper.instance.resetApp();

    };

    private void init() {
        resetApp();
    }

    private void resetApp() {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException, java.io.IOException {

                String credentials = "admin" + ":" + "123456";
                final String basic =
                        "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


                Request request = chain.request().newBuilder()
//                        .addHeader("token", Constants.token)
//                .addHeader("Authorization", basic).addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(request);
            }
        });
        builder.addInterceptor(new ProgressInterceptor());

        builder.cookieJar(new CookiesManager(mCntext));

        OkHttpClient client = builder.connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(CrashApplication.www+"")
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
    public RetrofitService getService(){
        return mRetrofit.create(RetrofitService.class);
    }
}






 class CookiesManager implements CookieJar {

    private Context context;
     private  PersistentCookieStore cookieStore=null ;
    public CookiesManager(Context context) {
        this.context=context;


        cookieStore = new PersistentCookieStore(context);

    }



    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}

