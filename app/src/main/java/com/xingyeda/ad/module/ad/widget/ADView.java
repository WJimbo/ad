package com.xingyeda.ad.module.ad.widget;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.UrlSource;
import com.xingyeda.ad.R;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.module.ad.data.AdItem;
import com.xingyeda.ad.module.ad.data.DownloadManager;
import com.xingyeda.ad.util.GlideUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.ToastUtils;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.widget.CustomView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class ADView extends CustomView {

    public interface IADViewCallBack{

    }
    public interface IADDataSourceListener{
        AdItem getNextAD(AdItem finishPlayItem);
    }


    private boolean videoMute = false;//视频静音
    private boolean autoFadeInWhenNoAD = false;//没有广告的时候 将控件Alpha设置低一些
    //旋转角度
    private float rotation = 0;

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.videoViewRootLayout)
    FrameLayout videoViewRootLayout;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.iv_Defualt)
    ImageView ivDefualt;
    @BindView(R.id.tv_CountSecond)
    TextView tvCountSecond;
    //定义一个播放器对象
//    private AliPlayer mAliyunVodPlayer;
    private AliListPlayer aliListPlayer;
    private Disposable disposable;

    private IADDataSourceListener dataSourceListener;

    private WeakReference<TextView> mWeakTvCountSecond; //显示倒计时的文字  用弱引用 防止内存泄漏

    public void setVideoMute(boolean videoMute) {
        this.videoMute = videoMute;
    }

    public void setAutoFadeInWhenNoAD(boolean autoFadeInWhenNoAD) {
        this.autoFadeInWhenNoAD = autoFadeInWhenNoAD;
    }

    public void setDataSourceListener(IADDataSourceListener dataSourceListener) {
        this.dataSourceListener = dataSourceListener;
    }

    public ADView(@NonNull Context context) {
        super(context);
    }

    public ADView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ADView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.widget_adview;
    }

    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this, rootView);
        mWeakTvCountSecond = new WeakReference<>(tvCountSecond);
        tvCountSecond.setRotation(rotation);
        surfaceView.setZOrderMediaOverlay(true);
        SurfaceHolder holder = surfaceView.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(aliListPlayer != null){
                    aliListPlayer.setDisplay(surfaceHolder);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                       int height) {
                if(aliListPlayer != null){
                    aliListPlayer.redraw();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(aliListPlayer != null){
                    aliListPlayer.setDisplay(null);
                }
            }
        });

        currentADEndTimeMillis = System.currentTimeMillis() + 1 * 1500;
        startCountDownDisposable();
    }

    private void createAliPlayer(){
        try {
            aliListPlayer = AliPlayerFactory.createAliListPlayer(getContext().getApplicationContext());
            aliListPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
            aliListPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
                @Override
                public void onError(ErrorInfo errorInfo) {
//                    currentADEndTimeMillis = System.currentTimeMillis() + 3 * 1000;
//                    ToastUtils.showToastLong(getContext().getApplicationContext(),"播放视频错误:" + errorInfo.getMsg());
                    LoggerHelper.i("播放视频错误:" + errorInfo.getMsg());
//                    playNextAd("errorInfo");
                }
            });
            aliListPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
                @Override
                public void onStateChanged(int i) {
                    if(i == IPlayer.started){
                        //延时100毫秒  去除切换时的残留影像
                        Observable.timer(100,TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        imageView.setVisibility(View.INVISIBLE);
                                        imageView.setImageDrawable(null);
                                    }
                                }).subscribe();

                    }
                }
            });
//            mAliyunVodPlayer = AliPlayerFactory.createAliPlayer(getContext().getApplicationContext());
//
//            mAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
//            mAliyunVodPlayer.enableHardwareDecoder(false);
            aliListPlayer.setDisplay(surfaceView.getHolder());
        }catch (Exception ex){
            MyLog.i("ADView---> Exception:" + ex.getMessage());
        }catch (Error error){
            MyLog.i("ADView---> Error:" + error.getMessage());
        }
    }

    private void cancelCountDownDisposable(){
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }
    private void startCountDownDisposable(){
        cancelCountDownDisposable();
        StringBuilder stringBuilder = new StringBuilder();
        String undefineTime = "--";
        disposable = Flowable.intervalRange(0, Long.MAX_VALUE, 0, 500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    //用弱引用 先判空 避免崩溃
                    if (mWeakTvCountSecond.get() == null) {
                        cancelCountDownDisposable();
                        return;
                    }
                    long lastSecond = ((currentADEndTimeMillis - System.currentTimeMillis()) + 500) / 1000;
                    if(lastSecond >= 0 && lastSecond <= 1000){
                        stringBuilder.setLength(0);
                        stringBuilder.append(lastSecond);
                        mWeakTvCountSecond.get().setText(stringBuilder);
                    }else{
                        mWeakTvCountSecond.get().setText(undefineTime);
                    }

                    if(System.currentTimeMillis() >= currentADEndTimeMillis){
                        playNextAd("CountDown");
                    }
                })
                .doOnComplete(() -> {
                    startCountDownDisposable();
                })
                .subscribe();
    }

    private void stopAliPlayer() {
        if(aliListPlayer != null && isVideoPlaying){
            aliListPlayer.setOnCompletionListener(null);
//            aliListPlayer.reset();

            aliListPlayer.stop();
//            mAliyunVodPlayer.release();
//            mAliyunVodPlayer = null;
            isVideoPlaying = false;
        }
    }

    private boolean isVideoPlaying = false;

    private void playLocalVideo(File file) {
//        Observable<AliPlayer> observable = Observable.create(new ObservableOnSubscribe<AliPlayer>() {
//            @Override
//            public void subscribe(ObservableEmitter<AliPlayer> emitter) throws Exception {
//
//            }
//        });
        String path = "file://" + file.getPath();
        stopAliPlayer();
        if(aliListPlayer == null){
            createAliPlayer();
        }
        if(aliListPlayer != null){
            aliListPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
                @Override
                public void onCompletion() {
//                    playNextAd("onCompletion");
                    currentADEndTimeMillis = System.currentTimeMillis();
                }
            });

            aliListPlayer.setLoop(false);
            aliListPlayer.setAutoPlay(true);

            aliListPlayer.setMute(videoMute);
//            if(!videoURLAddList.contains(file.getName())){
//                aliListPlayer.addUrl(path,file.getName());
//                videoURLAddList.add(file.getName());
//            }
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(path);
            aliListPlayer.setDataSource(urlSource);
//            LoggerHelper.i("setDataSource");
            aliListPlayer.prepare();
            aliListPlayer.start();
            surfaceView.setVisibility(View.VISIBLE);
            isVideoPlaying = true;
        }
    }


    private long currentADEndTimeMillis = 0;
    private AdItem currentADItem = null;
    private long lastTryToPlayTime = 0;
    private void playNextAd(String info) {
        try {
            //距离上次播放切换处理不足500ms  忽略本次切换操作
            if(System.currentTimeMillis() - lastTryToPlayTime < 500){
                MyLog.i("距离上次播放切换处理不足500ms  忽略本次切换操作");
                return;
            }
//            //当前广告播放结束时间比当前时间晚  则认为广告在有效期  无需切换
//            if(currentADEndTimeMillis >= System.currentTimeMillis()){
//                MyLog.i("PlayNextAD--->Start当前广告播放结束时间比当前时间晚  则认为广告在有效期  无需切换");
//                return;
//            }
//            currentADEndTimeMillis = System.currentTimeMillis() + 3 * 1000;
            //避免视频广告播放后 导致后续广告过来两处理回调异常  一个是定时器发出的 一个是播放结束发出的
            long delayTime = 0;
            AdItem adItem = null;
            if(isPause){
                adItem = null;
            }else{
                if(dataSourceListener != null){
                    adItem = dataSourceListener.getNextAD(currentADItem);
                }
                currentADItem = adItem;
            }
            if (adItem == null) {//无广告，广告位是空的  则显示默认图片
                ivDefualt.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                surfaceView.setVisibility(View.INVISIBLE);
                if(autoFadeInWhenNoAD){
                    setAlpha(0.1f);
                }
                stopAliPlayer();
                delayTime = (System.currentTimeMillis() % 4 + 3) * 1000;

            }else if(!adItem.isFileExsits()){
                LogDebugUtil.appendLog(String.format("(%s)广告正在下载中，请稍后：-->%s",2==adItem.getType()?"视频":"图片",adItem.getLocationFileName()));
                //有广告，但是广告还没下载完成
                ivDefualt.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                surfaceView.setVisibility(View.INVISIBLE);
                stopAliPlayer();
                if(0 == adItem.getType()){
                    delayTime = (System.currentTimeMillis() % 3 + 5) * 1000;
                }else{
                    delayTime = (System.currentTimeMillis() % 5 + 5) * 1000;
                }
                if(autoFadeInWhenNoAD){
                    setAlpha(0.3f);
                }
            } else {//广告已经下载完成了，可以正常显示了
                LogDebugUtil.appendLog(String.format("(%s)广告即将播放：-->%s",2==adItem.getType()?"视频":"图片",adItem.getLocationFileName()));
                if(autoFadeInWhenNoAD){
                    setAlpha(1);
                }
                ivDefualt.setVisibility(View.INVISIBLE);
                if (2 == adItem.getType()) {
//              等视频开始渲染了在隐藏图片控件
                    playLocalVideo(new File(DownloadManager.getDownloadRootPath(getContext().getApplicationContext()), adItem.getLocationFileName()));
                } else {
                    surfaceView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    stopAliPlayer();
                    GlideUtil.loadImage(getContext().getApplicationContext(), adItem.locationFile(),imageView,rotation);
                }
                delayTime = adItem.getDuration() * 1000;
            }
            currentADEndTimeMillis = System.currentTimeMillis() + delayTime;
        }catch (Exception ex){
            LogDebugUtil.appendLog("PlayNextAD--->Exception----->" + ex.getMessage());
            ToastUtils.showToast(getContext().getApplicationContext(),"广告切换异常");
            currentADEndTimeMillis = System.currentTimeMillis() + 3 * 1000;
        }
        lastTryToPlayTime = System.currentTimeMillis();

    }
    public void setDefaultImage(@DrawableRes int resID){
        imageView.setImageResource(resID);
        ivDefualt.setImageResource(resID);
    }

    public void setCountDownTitleColor(int color){
        if(tvCountSecond != null){
            tvCountSecond.setTextColor(color);
        }
    }

    private boolean isPause = false;
    public void resumeAD() {
        if(isPause){
            isPause = false;
            currentADEndTimeMillis = System.currentTimeMillis();
//            playNextAd("resumeAD");
        }
    }

    public void pauseAD() {
        isPause = true;
        stopAliPlayer();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }
    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
        if(tvCountSecond != null){
            tvCountSecond.setRotation(rotation);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)tvCountSecond.getLayoutParams();
        if(rotation == 90){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }else if(rotation == 270){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }else if(rotation == 180){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        tvCountSecond.setLayoutParams(layoutParams);
    }
    public void onDestroy(){

        if(aliListPlayer != null){
            stopAliPlayer();
            aliListPlayer.release();
            aliListPlayer = null;
        }
        cancelCountDownDisposable();
    }
    public void setTvCountSecondTextSize(int spTextSize){
        tvCountSecond.setTextSize(ToolUtils.convert().sp2px(spTextSize));
    }
    public void setTvCountSecondSize(int widthDP,int heightDP){
        ViewGroup.LayoutParams layoutParams = tvCountSecond.getLayoutParams();
        layoutParams.height = ToolUtils.convert().dp2px(heightDP);
        layoutParams.width = ToolUtils.convert().dp2px(widthDP);
        tvCountSecond.setLayoutParams(layoutParams);
    }
}
