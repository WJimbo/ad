package com.xingyeda.ad.util;

import android.content.Context;
import android.os.Environment;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.UIUtils;

import java.io.File;

import top.wuhaojie.installerlibrary.AutoInstaller;

public class ApkInstaller {
    /**
     * 通过autoinstaller进行安装
     * @param context
     * @param downloadUrl
     */
    public static void installApkUseAutoInstaller(final Context context, String downloadUrl){
        AutoInstaller autoInstaller = new AutoInstaller.Builder(context)
                .setMode(AutoInstaller.MODE.AUTO_ONLY)
                .setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
                    @Override
                    public void onStart() {
                        // 当后台安装线程开始时回调
                        ToastUtils.showToastLong(context.getApplicationContext(),"开始安装");
                    }

                    @Override
                    public void onComplete() {
                        // 当请求安装完成时回调
                        ToastUtils.showToastLong(context.getApplicationContext(),"安装完成");
                    }

                    @Override
                    public void onNeed2OpenService() {
                        // 当需要用户手动打开 `辅助功能服务` 时回调
                        // 可以在这里提示用户打开辅助功能
                        ToastUtils.showToastLong(context.getApplicationContext(),"请打开辅助功能服务");
                    }
                })
                .build();
        autoInstaller.installFromUrl(downloadUrl);
    }

    /*
        获取路径
        */
    private static String initApkDownloadPath(Context context) {
        String PATH_APK_DOWNLOAD;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_APK_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "XYD_AD" + File.separator + "apk";
        } else {
            PATH_APK_DOWNLOAD = context.getFilesDir().getAbsolutePath() + File.separator + "XYD_AD" + File.separator + "apk";
        }
        File file = new File(PATH_APK_DOWNLOAD);
        if (!file.exists()) {
            file.mkdirs();
        }
        return PATH_APK_DOWNLOAD;
    }

    public interface OnDownloadApkCallback{
        void downloadFinish(String apkPath);
    }

    /*
   从服务器获取APK
   */
    public static void downloadApkAndInstall(final String downloadURL, final Context context, final String fileName,OnDownloadApkCallback onDownloadApkCallback) {
        final String apkPath = initApkDownloadPath(context) + "/" + fileName;
        final File apkFile = new File(apkPath);
        if(apkFile != null && apkFile.exists()){
            apkFile.delete();
        }

        FileDownloader.getImpl().create(downloadURL)
                .setPath(apkPath)
                .setAutoRetryTimes(3)
                .setMinIntervalUpdateSpeed(5 * 1000)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        ToastUtils.showToast(context,"准备下载apk");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        ToastUtils.showToast(context,"apk下载地址连接成功");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int progress = (int) (((float) soFarBytes / totalBytes) * 100);
                        progress = (progress > 0) ? progress : 0;
                        ToastUtils.showToast(context,"下载中(" + progress + "%)");
                    }


                    @Override
                    protected void completed(BaseDownloadTask task) {
                        ToastUtils.showToast(context,"下载完成");
                        if(onDownloadApkCallback != null){
                            UIUtils.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    onDownloadApkCallback.downloadFinish(apkPath);
                                }
                            });

                        }

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        MyLog.e(downloadURL + "下载出错:" + e.getMessage());
                        try{
                            ToastUtils.showToast(context,"下载APK出错：" + e.getMessage());
                        }catch (Exception ex){

                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        MyLog.e(downloadURL + "   warn");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        MyLog.e(downloadURL + "   paused");
                    }
                }).start();
    }
}
