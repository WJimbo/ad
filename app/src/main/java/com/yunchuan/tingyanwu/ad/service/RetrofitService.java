package com.yunchuan.tingyanwu.ad.service;


import com.yunchuan.tingyanwu.ad.vo.AdInfo;
import com.yunchuan.tingyanwu.ad.vo.LoginResult;
import com.yunchuan.tingyanwu.ad.vo.MsgInfo;
import com.yunchuan.tingyanwu.ad.vo.PostResult;
import com.yunchuan.tingyanwu.ad.vo.UploadResult;
import com.yunchuan.tingyanwu.ad.vo.VersionInfo;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by tingyanwu on 2017/10/5.
 */


public interface RetrofitService {


    @GET("login")
    Observable<LoginResult> login(@Query("mobile") String mobile,
                                      @Query("password") String password);





    @FormUrlEncoded
    @POST("register")
    Observable<PostResult> register(@FieldMap Map<String,String> map );



    @GET
    Observable<ResponseBody> download(@Url String fileUrl);


    @GET
    Observable<ResponseBody> download2(@Url String url);
    @Streaming



    @Multipart
    @POST("upload")
    Observable<UploadResult> upload(@Part("rid") RequestBody rid, @Part("flag") RequestBody flag, @Part("name") RequestBody name,
                                    @Part("file") RequestBody photo);



    @FormUrlEncoded
    @POST("member/post")
    Observable<PostResult> postMember(@FieldMap Map<String,String> map);




//    @GET("GetAdversitingByMac/R")
//    Observable<AdInfo> getAdInfo(@Query("mac") String mac);

    @GET
    Observable<AdInfo> getAdInfo(@Url String url);


    @GET
    Observable<ResponseBody> register(@Url String url);

    @GET
    Observable<MsgInfo> getAnnouncement(@Url String url);

    @GET
    Observable<VersionInfo> getVersion(@Url String url);
}



