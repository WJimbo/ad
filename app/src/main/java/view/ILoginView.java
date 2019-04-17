package view;

import com.yunchuan.tingyanwu.ad.vo.LoginResult;

/**
 * Created by tingyanwu on 2017/10/5.
 */

public interface ILoginView extends IView {
    void onSuccess(LoginResult mLoginResult);
    void onError(String result);
}