package com.xingyeda.ad;
import android.os.AsyncTask;
import com.lansosdk.videoeditor.VideoEditor;
import com.mazouri.tools.Tools;

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
        dstVideo = videoEditor.executeSetVideoMetaAngle(srcPath, 270);
        if (dstVideo == null) {
            //旋转视频
            dstVideo = videoEditor.executeVideoRotate90Clockwise(srcPath);
        }
        if (dstVideo != null) {
            Tools.file().deleteFile(dstPath);
            Tools.file().moveFile(dstVideo, dstPath);
        } else {
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
