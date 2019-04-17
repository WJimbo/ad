package presenter;

import android.content.Intent;

import view.IView;

public interface IPresenter {
    void onCreate();

    void onStart();//暂时没用到

    void onStop();

    void pause();//暂时没用到

    void attachView(IView view);

    void attachIncomingIntent(Intent intent);//暂时没用到
}
