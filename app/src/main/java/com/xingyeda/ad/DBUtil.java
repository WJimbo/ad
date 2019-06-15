package com.xingyeda.ad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xingyeda.ad.util.LoggerHelper;

import java.util.List;

import cn.ittiger.database.SQLiteDB;
import cn.ittiger.database.SQLiteDBConfig;
import cn.ittiger.database.SQLiteDBFactory;
import cn.ittiger.database.listener.IDBListener;

public class DBUtil {

    private static DBUtil instance;

    private Context c;

    private SQLiteDB db;

    private DBUtil(Context c) {
        this.c = c;
    }

    public static DBUtil getInstance(Context c) {
        if (instance == null)
            return new DBUtil(c);
        return instance;
    }


    public void initialization() {
        SQLiteDBConfig config = new SQLiteDBConfig(c);
        //设置数据库创建更新时的监听，有提供空实现：SimpleDBListener
        config.setDbListener(new IDBListener() {
            @Override
            public void onUpgradeHandler(SQLiteDatabase db, int oldVersion, int newVersion) {
            }

            @Override
            public void onDbCreateHandler(SQLiteDatabase db) {
                LoggerHelper.i("数据库创建成功.'");
            }
        });
        //创建db，在创建数据库的时候，不需要在onDbCreateHandler手动去创建相关的数据表，在对实体对象进行数据操作的时候，会自动判断表是否存在，不存在的话会自动创建，同时如果有新增的字段也会自动更新表结构
        db = SQLiteDBFactory.createSQLiteDB(config);
    }

    public void save(AdEntity entity) {
        db.save(entity);
    }

    public AdEntity get(String id) {
        return db.query(AdEntity.class, id);
    }

    public List<AdEntity> list() {
        return db.queryAll(AdEntity.class);
    }

    public void delete(String id) {
        db.delete("delete from xyd_ad_info where id = '" + id + "'");
    }

    public void drop() {
        db.deleteAll(AdEntity.class);
    }

    public void delete(AdEntity entity) {
        db.delete(entity);
    }
}
