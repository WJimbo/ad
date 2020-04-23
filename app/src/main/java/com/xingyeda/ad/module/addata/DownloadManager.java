package com.xingyeda.ad.module.addata;

import android.content.Context;
import android.os.Environment;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.mazouri.tools.Tools;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadManager {
    public static class DownloadItem{
        public String url;
        public File savePath;
        public int fileType;//0图片  1音频  2视频
        public File getTempDownloadPath(){
            return new File(savePath.getParent(), "tmp_" + savePath.getName());
        }
        public float videoRotateAngle = 0;
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof DownloadItem)){
                return false;
            }
            if (url.equals(((DownloadItem) obj).url)
                    && savePath.getPath().equals(((DownloadItem) obj).savePath.getPath())
                    && videoRotateAngle == ((DownloadItem) obj).videoRotateAngle
                    && fileType == ((DownloadItem) obj).fileType) {
                return true;
            }
            return super.equals(obj);
        }
    }

    private static DownloadManager instance = new DownloadManager();
    private List<DownloadItem> downloadingList = new ArrayList<>();
    private Context context;
    private DownloadManager(){

    }

    public static DownloadManager getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    private static String DOWNLOAD_ROOT_PATH = null;
    public static synchronized String getDownloadRootPath(Context context) {
        if(DOWNLOAD_ROOT_PATH == null){
            String rootPath;
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
                rootPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
            } else {// 如果SD卡不存在，就保存到本应用的目录下
                rootPath = context.getFilesDir().getAbsolutePath();
            }
            DOWNLOAD_ROOT_PATH = rootPath + File.separator + "XYD_AD" + File.separator + "download";
            File rootFilePath = new File(DOWNLOAD_ROOT_PATH);
            if (!rootFilePath.exists()) {
                rootFilePath.mkdirs();
            }
        }
        return DOWNLOAD_ROOT_PATH;
    }



    private boolean isFileDownloading(DownloadItem downloadItem){
        return downloadingList.contains(downloadItem);
    }

    public void startDownLoadWithList(List<DownloadItem> list){
        List<DownloadItem> needDownloadItemList = new ArrayList<>();
        for(DownloadItem downloadItem : list){
            if (!isFileDownloading(downloadItem)) {
                downloadingList.add(downloadItem);
                needDownloadItemList.add(downloadItem);
                LogDebugUtil.appendLog("加入到视频下载队列：" + downloadItem.getTempDownloadPath());
            }else{
                LogDebugUtil.appendLog("已在下载队列中：" + downloadItem.getTempDownloadPath());
            }
        }
        if(needDownloadItemList.size() > 0){
            final FileDownloadListener queueTarget =new FileDownloadListener() {
                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    MyLog.d("开始下载文件:" + task.getUrl());
                    LogDebugUtil.appendLog("开始下载文件:" + task.getUrl());
                }

                @Override
                protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    MyLog.d( "资源地址链接成功:" + task.getUrl());
                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                }

                @Override
                protected void blockComplete(BaseDownloadTask task) {
                }

                @Override
                protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                }

                //下载成功
                @Override
                protected void completed(BaseDownloadTask task) {
                    final DownloadItem downloadItem = (DownloadItem)task.getTag();
                    MyLog.d("下载完成:" + task.getUrl());
                    LogDebugUtil.appendLog("下载完成:" + task.getUrl());
                    if(2 == downloadItem.fileType && downloadItem.videoRotateAngle > 0){
                        RotateVideoAsyncTask rotateVideoAsyncTask = new RotateVideoAsyncTask(downloadItem.getTempDownloadPath().getPath(),downloadItem.savePath.getPath(),downloadItem.videoRotateAngle);
                        rotateVideoAsyncTask.setCallback(new RotateVideoAsyncTask.Callback() {
                            @Override
                            public void rotateVideoFinish(boolean success) {
                                LogDebugUtil.appendLog("视频旋转完成：" + downloadItem.savePath.getName());
                                downloadingList.remove(downloadItem);
                                ToolUtils.file().deleteFile(downloadItem.getTempDownloadPath());
                            }
                        });
                        rotateVideoAsyncTask.execute();
                    }else if(0 == downloadItem.fileType){
                        CompressImageAsyncTask compressImageAsyncTask = new CompressImageAsyncTask(context,downloadItem.getTempDownloadPath().getPath(),downloadItem.savePath.getPath());
                        compressImageAsyncTask.setCallback(new CompressImageAsyncTask.Callback() {
                            @Override
                            public void compressImageFinish(boolean success) {
                                LogDebugUtil.appendLog("图片压缩完成：" + downloadItem.savePath.getName());
                                downloadingList.remove(downloadItem);
                                ToolUtils.file().deleteFile(downloadItem.getTempDownloadPath());
                            }
                        });
                        compressImageAsyncTask.execute();
                    }else{
                        Tools.file().rename(downloadItem.getTempDownloadPath(),downloadItem.savePath.getName());
                        downloadingList.remove(downloadItem);
                    }

                }

                @Override
                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {
                    final DownloadItem downloadItem = (DownloadItem)task.getTag();
                    MyLog.d("下载出错:" + task.getUrl() +"\n" + e.getMessage());
                    downloadingList.remove(downloadItem);
                    LogDebugUtil.appendLog("下载出错:" + task.getUrl() +"\n" + e.getMessage());
                }

                @Override
                protected void warn(BaseDownloadTask task) {
//                        continueDownLoad(task);
                }
            };
            for(DownloadItem downloadItem : needDownloadItemList){
                FileDownloader.getImpl().create(downloadItem.url)
                        .setPath(downloadItem.getTempDownloadPath().getPath())
                        .setCallbackProgressTimes(0) // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
                        //.setForceReDownload(true)
                        .setTag(downloadItem)
                        .setAutoRetryTimes(2)
                        .setListener(queueTarget)
                        .asInQueueTask()
                        .enqueue();
            }
            FileDownloader.getImpl().start(queueTarget, true);
        }
    }


}
