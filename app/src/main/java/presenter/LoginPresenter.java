package presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yunchuan.tingyanwu.ad.service.DataManager;
import com.yunchuan.tingyanwu.ad.vo.LoginResult;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import view.ILoginView;
import view.IView;

/**
 * Created by tingyanwu on 2017/10/5.
 */

public class LoginPresenter implements IPresenter {


    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private ILoginView mLoginView;
    private LoginResult mLoginResult;

    public LoginPresenter(Context mContext) {

        Log.d("loginpresent",mContext.toString());
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
        mLoginView = (ILoginView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {
    }

    public void login(String mobile, String password) {


        mCompositeSubscription.add(manager.login(mobile, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResult>() {
                    @Override
                    public void onCompleted() {
                        if (mLoginResult != null) {
                            mLoginView.onSuccess(mLoginResult);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("retrieve", e.toString());
                        mLoginView.onError("登录失败!");
                    }

                    @Override
                    public void onNext(LoginResult loginResult) {
                        mLoginResult = loginResult;
                    }
                })
        );
    }
}
