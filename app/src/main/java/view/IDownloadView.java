package view;

import com.yunchuan.tingyanwu.ad.vo.UploadResult;

/**
 * Created by tingyanwu on 2017/10/5.
 */

public interface IDownloadView extends IView {
    void onUpload(UploadResult result);
    void onDownload(String filename);
    void onDownloadVideo(String url,String filename);
    void onStart();
    void onComplete();
    void onUpdate(long progress,long total);
    void onError(String result);
    void onVideoError(String downloadUrl);
}