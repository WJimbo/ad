package presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yunchuan.tingyanwu.ad.service.DataManager;
import com.yunchuan.tingyanwu.ad.util.FileCallBack;
import com.yunchuan.tingyanwu.ad.util.FileSubscriber;
import com.yunchuan.tingyanwu.ad.vo.UploadResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import view.IDownloadView;
import view.IView;

/**
 * Created by tingyanwu on 2017/10/5.
 */
//用于升级下载
public class DownloadPresenter implements IPresenter {


    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private IDownloadView mDownloadView;
    private String mParameter;
    private ResponseBody mResponseBody;


    public DownloadPresenter(Context mContext) {

        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        manager = new DataManager(mContext);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        if (mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(IView view) {
        mDownloadView = (IDownloadView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {
    }



    //下载文件到本地 给升级使用
    public void download2(String url, String dir, String name) {
        final String filename = name;
        Log.e("download", "load: " + dir.toString());
        FileCallBack<ResponseBody> callBack = new FileCallBack<ResponseBody>(dir, name) {

            @Override
            public void onSuccess(final ResponseBody responseBody) {
                mDownloadView.onDownload(filename);
            }

            @Override
            public void progress(long progress, long total) {
                Log.d("porgresss", progress + "_" + total);
                mDownloadView.onUpdate(progress, total);
            }

            @Override
            public void onStart() {
                mDownloadView.onStart();
            }

            @Override
            public void onCompleted() {
                mDownloadView.onComplete();
            }

            @Override
            public void onError(Throwable e) {

                e.printStackTrace();
            }
        };
        this.download2(url, callBack);
    }




    //下载视频到 给视频广告使用
    public void downloadVideo(String url, String dir, String name) {
        final String filename = name;
        final String downloadUrl=url;
        Log.e("download", "load: " + dir.toString());
        FileCallBack<ResponseBody> callBack = new FileCallBack<ResponseBody>(dir, name) {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                mDownloadView.onDownloadVideo(downloadUrl,filename);
            }

            @Override
            public void progress(long progress, long total) {
                Log.d("porgresss", progress + "_" + total);
                mDownloadView.onUpdate(progress, total);
            }

            @Override
            public void onStart() {
                mDownloadView.onStart();
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mDownloadView.onVideoError(downloadUrl);
                Log.d("download video",downloadUrl);
            }
        };
        this.download2(url, callBack);
    }








    public void download2(String url, final FileCallBack<ResponseBody> callBack) {
        manager.download(url)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .doOnNext(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody body) {
                        callBack.saveFile(body);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new FileSubscriber<ResponseBody>(callBack));
    }


    public void upload(RequestBody rid, RequestBody flag, RequestBody name, RequestBody photo) {
        manager.upload(rid, flag, name, photo)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new Observer<UploadResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("upload", e.toString());

                    }

                    @Override
                    public void onNext(UploadResult result) {
                        mDownloadView.onUpload(result);

                    }

                });
     }


}
