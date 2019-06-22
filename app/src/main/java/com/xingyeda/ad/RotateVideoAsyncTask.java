package com.xingyeda.ad;
import android.os.AsyncTask;

import com.altang.app.common.utils.LoggerHelper;
import com.lansosdk.videoeditor.VideoEditor;
import com.mazouri.tools.Tools;
import com.xingyeda.ad.logdebug.LogDebugUtil;

import java.io.File;

public class RotateVideoAsyncTask extends AsyncTask<Object, Object, Boolean> {
    private VideoEditor videoEditor;
    public interface Callback{
        void rotateVideoFinish(boolean success);
    }
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    private String srcPath;
    private String dstPath;
    public RotateVideoAsyncTask(String srcPath,String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        videoEditor = new VideoEditor();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected synchronized Boolean doInBackground(Object... params) {
        //修改视频元数据
        String dstVideo;
        LogDebugUtil.appendLog("开始旋转视频");
        dstVideo = videoEditor.executeSetVideoMetaAngle(srcPath, 270);
        if (dstVideo == null) {
            //旋转视频
            LogDebugUtil.appendLog("尝试旋转视频文件成功");
            dstVideo = videoEditor.executeVideoRotate90Clockwise(srcPath);
        }else{
            LogDebugUtil.appendLog("旋转视频元数据成功");
        }
        if (dstVideo != null) {
            LogDebugUtil.appendLog("旋转视频成功");
            LoggerHelper.i("旋转视频地址:" + dstVideo);
            Tools.file().deleteFile(dstPath);
            Tools.file().deleteFile(srcPath);
            Tools.file().moveFile(dstVideo, srcPath);
            Tools.file().rename(srcPath,(new File(dstPath).getName()));
        } else {
            LogDebugUtil.appendLog("视频旋转失败");
        }

        if(callback != null){
            callback.rotateVideoFinish(Tools.file().isFileExists(dstVideo));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
