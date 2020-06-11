package com.xingyeda.ad.module.ad.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.mazouri.tools.Tools;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.zz9158.app.common.utils.PhotoBitmapUtils;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;
import java.io.FileInputStream;

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
    private float imageRotateAngle = 0;
    private Context mContext;
    public CompressImageAsyncTask(Context context,String srcPath, String dstPath,float imageRotateAngle) {
        mContext = context;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.imageRotateAngle = imageRotateAngle;
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
            boolean rotated = false;
            if(imageRotateAngle != 0){
                FileInputStream fis = null;
                try{
                    fis = new FileInputStream(zipFile.getPath());
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fis.getFD());
                    Bitmap rotateBitmap = ToolUtils.bitmap().rotateBitmap(bitmap,(int)imageRotateAngle,true);
                    if(rotateBitmap != null){
                        ToolUtils.bitmap().saveBitmap(rotateBitmap,zipFile);
                        rotated = true;
                        LogDebugUtil.appendLog("旋转图片成功");
                        if(!rotateBitmap.isRecycled()){
                            rotateBitmap.recycle();
                        }
                    }else{
                        throw new Exception("图片旋转错误，rotateBitmap is null");
                    }
                }catch (Exception ex){
                    LogDebugUtil.appendLog("旋转图片出错:" + ex.getMessage());
                }finally {
                    ToolUtils.close().closeIO(fis);
                }
            }



            if(srcFile == zipFile && !rotated){//无需压缩 直接改文件名字
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
