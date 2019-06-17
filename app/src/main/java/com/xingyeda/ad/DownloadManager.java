package com.xingyeda.ad;

import android.content.Context;

import com.altang.app.common.utils.ToolUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.ad.util.MyLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadManager {
    public static class DownloadItem{
        public String url;
        public File savePath;
        public File getTempSavePath(){
            return new File(savePath.getPath() + ".tmp.mp4");
        }
        public boolean rotateVideo = false;

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof DownloadItem)){
                return false;
            }
            if (url.equals(((DownloadItem) obj).url)
                    && savePath.getPath().equals(((DownloadItem) obj).savePath.getPath())
                    && rotateVideo == ((DownloadItem) obj).rotateVideo) {
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

    private boolean isFileDownloading(DownloadItem downloadItem){
        return downloadingList.contains(downloadItem);
    }
    public void downloadWithUrl(DownloadItem downloadItem){
        if (!isFileDownloading(downloadItem)) {
            downloadingList.add(downloadItem);
            startDownload(downloadItem);
        }
    }

    private void startDownload(final DownloadItem downloadItem){
        FileDownloader.getImpl().create(downloadItem.url)
                .setPath(downloadItem.getTempSavePath().getPath())
                //.setForceReDownload(true)
                .setAutoRetryTimes(5)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        MyLog.d("开始下载文件:" + task.getUrl());
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
                        MyLog.d("下载完成:" + task.getUrl());

                        if(downloadItem.rotateVideo){
                            RotateVideoAsyncTask rotateVideoAsyncTask = new RotateVideoAsyncTask(downloadItem.getTempSavePath().getPath(),downloadItem.savePath.getPath());
                            rotateVideoAsyncTask.setCallback(new RotateVideoAsyncTask.Callback() {
                                @Override
                                public void rotateVideoFinish(boolean success) {
                                    downloadingList.remove(downloadItem);
                                    ToolUtils.file().deleteFile(downloadItem.getTempSavePath());
                                }
                            });
                            rotateVideoAsyncTask.execute();
                        }else{
                            ToolUtils.file().moveFile(downloadItem.getTempSavePath(),downloadItem.savePath);
                            ToolUtils.file().deleteFile(downloadItem.getTempSavePath());
                            downloadingList.remove(downloadItem);
                        }

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        MyLog.d("下载出错:" + task.getUrl() +"\n" + e.getMessage());
                        downloadingList.remove(downloadItem);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
//                        continueDownLoad(task);
                    }
                }).start();
    }
    private void continueDownLoad(BaseDownloadTask task) {
        while (task.getSmallFileSoFarBytes() != task.getSmallFileTotalBytes()) {
            int percent = (int) ((double) task.getSmallFileSoFarBytes() / (double) task.getSmallFileTotalBytes() * 100);
        }
    }
}
