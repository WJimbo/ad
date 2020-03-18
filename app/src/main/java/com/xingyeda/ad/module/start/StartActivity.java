package com.xingyeda.lowermachine.business.modules.start;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.altang.baidutts.BaiduTTSHelper;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.xingyeda.lowermachine.AppInitWithConfigFile;
import com.xingyeda.lowermachine.BuildConfig;
import com.xingyeda.lowermachine.MainApplication;
import com.xingyeda.lowermachine.R;
import com.xingyeda.lowermachine.base.BaseActivity;
import com.xingyeda.lowermachine.business.modules.init.InitActivity;
import com.xingyeda.lowermachine.common.utils.DeviceUtil;
import com.xingyeda.lowermachine.common.utils.MediaTipUtil;
import com.xingyeda.lowermachine.common.utils.MyLog;
import com.xingyeda.lowermachine.serial.PhysicalKeyboardMonitorService;
import com.zz9158.app.common.utils.DialogHelper;
import com.zz9158.app.common.utils.FileHelper;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.utils.UIUtils;
import com.zz9158.app.common.utils.wifi.WifiHelper;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author tangyongx
 * @date 11/10/18
 */

public class StartActivity extends BaseActivity {
    public static boolean isStarted = false;
    private static String KEY_DOWNLOADED_SOZIP;
    @BindView(R.id.infoTextView)
    TextView infoTextView;
    @BindView(R.id.versionInfoTextView)
    TextView versionInfoTextView;
    private int tryUpdateTime = 0;//下载失败重连次数

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isStarted){
            MyLog.i("StartActivity被重复启动:" + this.toString());
            finish();
            return;
        }
        isStarted = true;
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    MyLog.i("StartActivity 启动失败 isTaskRoot:" + this.toString());
                    DeviceUtil.reboot(this);
                    finish();
                    return;
                }
            }
        }
        MyLog.i("StartActivity正常启动:" + this.toString());
        if(savedInstanceState != null){
            MyLog.i("StartActivity正常启动: savedInstanceState :" + savedInstanceState.toString());
        }
//        //这是为了应用程序安装完后直接打开，按home键退出后，再次打开程序出现的BUG
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            //结束你的activity
//            return;
//        }
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        requestOverlayPermission();
        if (AppInitWithConfigFile.isIsInited()) {
            if (!WifiHelper.isWifiConnect(this)) {
                if (!ToolUtils.string().isEmpty(AppInitWithConfigFile.getWifiName())) {
                    WifiHelper.openAndConfigWifi(this, true, AppInitWithConfigFile.getWifiName(), AppInitWithConfigFile.getWifiPassword(), AppInitWithConfigFile.getWifiType(), new WifiHelper.OpenAndConfigWifiCallback() {
                        @Override
                        public void operationResult(boolean success) {

                        }
                    });
                }
            }

        }
        MyLog.i("StartActivity正常启动:---》1");
        int waitTime = 15 * 1000;
        if (BuildConfig.DEBUG) {
            waitTime = 3 * 1000;
        }

        MediaTipUtil.playCardTip();
        KEY_DOWNLOADED_SOZIP = "KEY_DOWNLAOD_SOZIP_VERSION" + BuildConfig.BUILD_SO_VERSION;
        countDownTimer = new CountDownTimer(waitTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (infoTextView != null) {
                    infoTextView.setText("正在启动中(" + millisUntilFinished / 1000 + "秒)...");
                }
            }

            @Override
            public void onFinish() {
                MyLog.i("StartActivity正常启动:---》onFinish");
                BaiduTTSHelper.getInstance().init(getApplicationContext());
                if (!BuildConfig.DEBUG) {
                    if (checkNeedDownloadSoZip()) {
                        //			LoggerHelper.i(getApplicationContext().getApplicationInfo().nativeLibraryDir);
                        //
                        //			LoggerHelper.i("lib a.so exsit:" + ToolUtils.file().isFileExists("/data/data/" + this.getPackageName() + "/lib" + "/a.so"));
                        //
                        //			LoggerHelper.i("lib libBugly.so exsit:" + ToolUtils.file().isFileExists("/data/data/" + this.getPackageName() + "/lib" + "/libBugly.so"));
                        downloadSOZip();
                    } else {
                        if (MainApplication.ISADMODEL) {
                            gotoInitActivity(2000, false);
                        } else {
                            gotoInitActivity(2000, true);
                        }

                    }
                } else {
                    gotoInitActivity(2000, false);
                }
            }
        };
        countDownTimer.start();
        versionInfoTextView.setText("版本号:" + ToolUtils.appTool().getVersionNameFromPackage(this) + "_" + ToolUtils.appTool().getAppVersionCode(this) + "\n编译时间:" + BuildConfig.BUILD_DATE);
    }

    private static final int REQUEST_OVERLAY = 4444;

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {

            }
        }

    }

    private boolean checkNeedDownloadSoZip() {
        if (PhysicalKeyboardMonitorService.supportKeyboard() && BuildConfig.BUILD_SO_VERSION <= 3) {
            return false;
        }
        if (MainApplication.ISADMODEL) {
            return false;
        }
        return !ToolUtils.sp().getShareBoolean(KEY_DOWNLOADED_SOZIP) || !isAllSoExsist();
    }

    private boolean isAllSoExsist() {
        String[] soList = {
                "libagora-crypto.so",
                "libagora-rtc-sdk-jni.so",
                "libarcsoft_face.so",
                "libarcsoft_face_engine.so",
                "libbase.so",
                "libcheckcpu.so",
                "libjcore126.so",
                "libmpbase.so",
                "librkctrl.so",
                "libserialport.so",
                "libvvsip-v7a.so",
                "libvvsip-v7a-neon.so",
                "libalivcffmpeg.so"
        };
        for (String soFileName : soList) {
            if (!ToolUtils.file().isFileExists("system/lib/" + soFileName)) {
                return false;
            }
        }
        return true;
    }

    private void gotoInitActivity(int delayMillis, boolean needCheckSOs) {
        infoTextView.setText("初始化完成,即将进入应用");
        MyLog.i("StartActivity正常启动:---》gotoInitActivity  1 ");
        if (isAllSoExsist() || !needCheckSOs) {
            UIUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {

                    if (BaiduTTSHelper.getInstance().isInit()) {
                        BaiduTTSHelper.getInstance().speak("初始化完成,即将进入应用");
                    } else {
                        MediaTipUtil.playButtonTip(3);
                    }
                    MyLog.i("StartActivity正常启动:---》gotoInitActivity 2");
                    Intent intent = new Intent(StartActivity.this, InitActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, delayMillis);
        } else {
            BaiduTTSHelper.getInstance().speak("程序所需要到运行库不全，部分功能将无法使用");
            DialogHelper.showTwoButtonDialog(this, "警告", "程序所需要到运行库不全，部分功能将无法使用", "安装运行库", "继续使用", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadSOZip();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(StartActivity.this, InitActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void downloadSOZip() {
        String zipFileName = "jniLib_v" + BuildConfig.BUILD_SO_VERSION + ".zip";
        final String downloadURL = "http://service.xyd999.com:8080/xydServer/downloadFile/" + zipFileName;

        infoTextView.setText("准备下载资源包");
        BaiduTTSHelper.getInstance().speak("准备下载资源包");
        final String zipPath = FileHelper.getExternalStorageRootPath() + File.separator + zipFileName;
//        ToolUtils.file().deleteFile(zipPath);
        FileDownloader.getImpl().create(downloadURL)
                .setPath(zipPath)
                .setForceReDownload(false)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        infoTextView.setText("下载连接中");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        infoTextView.setText("资源下载中");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int progress = (int) (((float) soFarBytes / totalBytes) * 100);
                        progress = (progress > 0) ? progress : 0;
                        infoTextView.setText("正在下载中(" + progress + "%)");
                    }


                    @Override
                    protected void completed(BaseDownloadTask task) {
                        BaiduTTSHelper.getInstance().speak("下载完成，开始解压");
                        infoTextView.setText("下载完成,开始解压");
                        MyLog.i("下载完成,开始解压");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final File zipFile = new File(zipPath);
                                File destDir = new File(FileHelper.getExternalStorageRootPath() + File.separator + "jniLibs");
                                ToolUtils.file().deleteDir(destDir);
                                destDir.mkdirs();
                                if (unZipFile(zipFile, destDir)) {
                                    MyLog.i("SOZIP解压成功 准备拷贝资源文件");
                                    BaiduTTSHelper.getInstance().speak("解压成功 准备拷贝资源文件");
                                    UIUtils.runOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            infoTextView.setText("解压成功,开始拷贝资源文件");
                                        }
                                    });
                                    moveSoToSystemLib(destDir);
                                    ToolUtils.sp().saveShareBoolean(KEY_DOWNLOADED_SOZIP, true);

                                    UIUtils.runOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            infoTextView.setText("拷贝资源文件完成,即将重启程序");
                                        }
                                    });
                                    UIUtils.runOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DeviceUtil.reboot(StartActivity.this);
                                        }
                                    }, 1000);
                                } else {
                                    UIUtils.runOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            gotoInitActivity(1000, true);
                                        }
                                    });
                                }


                            }
                        }).start();
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        MyLog.e(downloadURL + "第" + tryUpdateTime + "次尝试SOZIP 下载出错:" + e.getMessage());
                        if (tryUpdateTime < 6) {
                            tryUpdateTime++;
                            infoTextView.setText("网络异常 5秒后尝试重新下载（第" + tryUpdateTime + "/6次尝试）:");
                            UIUtils.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadSOZip();
                                }
                            }, 5000);
                        } else {
                            BaiduTTSHelper.getInstance().speak("下载出错");
                            infoTextView.setText("下载出错:" + e.getMessage());
                            gotoInitActivity(2000, true);
                            ToastUtils.showToast(StartActivity.this, "SOZIP 下载出错：" + e.getMessage());
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


    private boolean unZipFile(File zipFile, File destDir) {
        try {
            ZipUtil.unpack(zipFile, destDir);
            return true;
        } catch (Exception ex) {
            MyLog.e("SOZIP 解压出错:" + ex.getMessage());
            ToastUtils.showToast(StartActivity.this, "SOZIP 解压出错：" + ex.getMessage());
        }
        return false;
    }

    private void moveSoToSystemLib(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                moveSoToSystemLib(subFile);
            }
        } else {
            String fileName = file.getName();
            if (fileName.endsWith(".so")) {
                //Q588机器不能覆盖这个so 不然系统会挂掉
                if ("libbase.so".equals(fileName) && ToolUtils.file().isFileExists("system/lib/" + fileName) && (PhysicalKeyboardMonitorService.supportKeyboard() || MainApplication.ISADMODEL)) {
                    return;
                }
                boolean deleteResult = DeviceUtil.deleteFileInSystem("system/lib/" + fileName);
                boolean moveResult = DeviceUtil.moveFileToSystem(file.getAbsolutePath(), "system/lib");
                MyLog.i(fileName + "删除:" + deleteResult + "   拷贝到system/lib目录：" + moveResult);
                if (moveResult == false) {
                    ToastUtils.showToast(this, "移动" + fileName + "到system/lib目录失败");
                }
            }
        }
    }


}
