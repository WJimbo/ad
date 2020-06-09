package com.xingyeda.ad.module.ad.data;

import android.content.Context;
import android.os.AsyncTask;

import com.mazouri.tools.Tools;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.zz9158.app.common.utils.PhotoBitmapUtils;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;

public class CompressImageAsyncTask extends AsyncTask<Object, Object,Boolean> {
    public interface Callback{
        void compressImageFinish(boolean success);
    }
    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    private String srcPath;
    private String dstPath;
    private Context mContext;
    public CompressImageAsyncTask(Context context,String srcPath, String dstPath) {
        mContext = context;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected synchronized Boolean doInBackground(Object... params) {
        //修改视频元数据
        File srcFile = new File(srcPath);
        LogDebugUtil.appendLog("开始压缩图片:" + srcPath);
        ToolUtils.file().deleteFile(dstPath);
        if(srcFile.exists()){
            File zipFile = PhotoBitmapUtils.compressorFile(mContext,srcFile,300 * 1024,600,800,80);
            if(srcFile == zipFile){//无需压缩 直接改文件名字
                LogDebugUtil.appendLog("图片无需压缩:" + srcPath);
                Tools.file().rename(srcPath,(new File(dstPath).getName()));
            }else{
                //图片压缩后将压缩图片替换原下载文件，然后改名最终目标文件名
                LogDebugUtil.appendLog("图片压缩完成:" + zipFile.getPath());
                Tools.file().deleteFile(dstPath);
                Tools.file().deleteFile(srcPath);
                Tools.file().moveFile(zipFile,srcFile);
                Tools.file().rename(srcPath,(new File(dstPath).getName()));
            }
        }else{
            LogDebugUtil.appendLog("图片图片源文件不存在:" + srcPath);
        }
        LogDebugUtil.appendLog("压缩处理结束图片:" + srcPath);
        if(callback != null){
            callback.compressImageFinish(Tools.file().isFileExists(dstPath));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
