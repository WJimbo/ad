package presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xingyeda.ad.service.DataManager;

import com.xingyeda.ad.vo.AdListResponseData;
import com.xingyeda.ad.vo.MsgInfo;
import com.xingyeda.ad.vo.VersionInfo;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import view.IAdView;
import view.IView;

/**
 * Created by tingyanwu on 2017/10/5.
 */

public class AdPresenter implements IPresenter {


    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private IAdView mAdView;


    public AdPresenter(Context mContext) {
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
        mAdView = (IAdView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {
    }


    public void register(String url) {

        mCompositeSubscription.add(manager.register(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("driverPresenter", e.toString());
                        mAdView.onError("保存失败!");
                    }

                    @Override
                    public void onNext(ResponseBody result) {
                        mAdView.onSuccessRegister(result);

                    }
                })
        );
    }


    public void getAnnouncement(String url) {
        mCompositeSubscription.add(manager.getAnnouncement(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MsgInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MsgInfo result) {
                        mAdView.onSuccessAnnouncement(result);
                    }
                })
        );
    }


    public void getVersion(String url) {
        mCompositeSubscription.add(manager.getVersion(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VersionInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mAdView.onError("版本更新请求失败!");
                    }

                    @Override
                    public void onNext(VersionInfo result) {
                        mAdView.onSuccessVersion(result);
                    }
                })
        );

    }
}
