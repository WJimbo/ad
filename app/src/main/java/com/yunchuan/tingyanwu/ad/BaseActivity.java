package com.yunchuan.tingyanwu.ad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

import presenter.DownloadPresenter;

public class BaseActivity extends Activity {
    protected Context mContext;


    protected String mProvince;
    protected String mCity;
    protected String mDistrict;

    private ArrayList<String> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//区


    private DownloadPresenter mDownloadPresenter = null;


    public String getTag()
    {

        return this.getClass().getName();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;





    }





}
