package view;

import com.xingyeda.ad.vo.MsgInfo;
import com.xingyeda.ad.vo.VersionInfo;

import okhttp3.ResponseBody;

/**
 * Created by tingyanwu on 2017/10/5.
 */

public interface IAdView extends IView {
    void onError(String result);
    void onSuccessRegister(ResponseBody result);
    void onSuccessAnnouncement(MsgInfo result);
    void onSuccessVersion(VersionInfo result);
}