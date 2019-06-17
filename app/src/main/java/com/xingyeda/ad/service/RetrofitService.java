package com.xingyeda.ad.service;


import com.xingyeda.ad.vo.MsgInfo;
import com.xingyeda.ad.vo.VersionInfo;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by tingyanwu on 2017/10/5.
 */


public interface RetrofitService {

    @GET
    Observable<ResponseBody> register(@Url String url);

    @GET
    Observable<MsgInfo> getAnnouncement(@Url String url);

    @GET
    Observable<VersionInfo> getVersion(@Url String url);
}



