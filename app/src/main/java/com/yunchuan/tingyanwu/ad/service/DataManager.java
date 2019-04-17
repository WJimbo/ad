package com.yunchuan.tingyanwu.ad.service;

import android.content.Context;
import android.util.Log;

import com.yunchuan.tingyanwu.ad.vo.AdInfo;
import com.yunchuan.tingyanwu.ad.vo.LoginResult;
import com.yunchuan.tingyanwu.ad.vo.MsgInfo;
import com.yunchuan.tingyanwu.ad.vo.PostResult;
import com.yunchuan.tingyanwu.ad.vo.UploadResult;
import com.yunchuan.tingyanwu.ad.vo.VersionInfo;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

public class DataManager {
    private RetrofitService mRetrofitService;

    public DataManager(Context context) {
        this.mRetrofitService = RetrofitHelper.getInstance(context).getService();
    }












    public Observable<LoginResult> login(String mobile, String password) {
        return mRetrofitService.login(mobile, password);
    }



    public Observable<PostResult> register(Map<String, String> map) {
        return mRetrofitService.register(map);
    }




    public Observable<ResponseBody> download(String url) {
        return mRetrofitService.download(url);
    }


    public Observable<UploadResult> upload(RequestBody rid, RequestBody flag, RequestBody name, RequestBody photo) {
        return mRetrofitService.upload(rid, flag, name, photo);
    }


    public Observable<PostResult> postMember(Map<String, String> map) {
        Log.e("dataManager","memberPost");
        return mRetrofitService.postMember(map);
    }


    public Observable<AdInfo>  getAdInfo(String mac) {
        Log.e("data manager adinfo",mac);

        return mRetrofitService.getAdInfo(mac);
    }


    public Observable<ResponseBody> register(String url) {
        Log.e("data manager register",url);
        return mRetrofitService.register(url);
    }



    public Observable<MsgInfo> getAnnouncement(String url) {
        return mRetrofitService.getAnnouncement(url);
    }


    public Observable<VersionInfo>  getVersion(String url) {

        return mRetrofitService.getVersion(url);


    }
}