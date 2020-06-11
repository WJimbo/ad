package com.xingyeda.ad.module.ad.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
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

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.UrlSource;
import com.xingyeda.ad.R;
import com.xingyeda.ad.module.ad.data.AdItem;
import com.xingyeda.ad.module.ad.data.DownloadManager;
import com.xingyeda.ad.util.GlideUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.utils.ToolUtils;
import com.zz9158.app.common.widget.CustomView;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ADView extends CustomView {

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
    private AliPlayer mAliyunVodPlayer;
    private Handler mHandler;

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

    private Runnable toNextAdRunnable;

    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this, rootView);
        mWeakTvCountSecond = new WeakReference<>(tvCountSecond);
        tvCountSecond.setRotation(rotation);
        mHandler = new Handler();
        surfaceView.setZOrderMediaOverlay(true);
        SurfaceHolder holder = surfaceView.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(mAliyunVodPlayer != null){
                    mAliyunVodPlayer.setDisplay(surfaceHolder);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                       int height) {
                if(mAliyunVodPlayer != null){
                    mAliyunVodPlayer.redraw();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(mAliyunVodPlayer != null){
                    mAliyunVodPlayer.setDisplay(null);
                }
            }
        });
        try {
            mAliyunVodPlayer = AliPlayerFactory.createAliPlayer(getContext().getApplicationContext());
            mAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
            mAliyunVodPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
                @Override
                public void onCompletion() {
                    playNextAd();
                }
            });
            mAliyunVodPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
                @Override
                public void onError(ErrorInfo errorInfo) {
                    playNextAd();
                }
            });
            mAliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
                @Override
                public void onRenderingStart() {

                }
            });
            mAliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
                @Override
                public void onStateChanged(int i) {
                    if(i == IPlayer.started){
                        imageView.setVisibility(View.INVISIBLE);
                        imageView.setImageDrawable(null);
                    }
                }
            });

            mAliyunVodPlayer.setDisplay(surfaceView.getHolder());

        }catch (Exception ex){
            MyLog.i("ADView---> Exception:" + ex.getMessage());
        }catch (Error error){
            MyLog.i("ADView---> Error:" + error.getMessage());
        }
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //用弱引用 先判空 避免崩溃
                if (mWeakTvCountSecond.get() == null) {
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                    }
                    return;
                }
                if (mWeakTvCountSecond.get() != null) {
                    long lastSecond = (currentADEndTimeMillis - System.currentTimeMillis()) / 1000;
                    if(lastSecond >= 0 && lastSecond <= 1000){
                        mWeakTvCountSecond.get().setText(lastSecond + "");
                    }else{
                        mWeakTvCountSecond.get().setText("--");
                    }

                }
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
        toNextAdRunnable = new Runnable() {
            @Override
            public void run() {
                playNextAd();
            }
        };
        mHandler.postDelayed(toNextAdRunnable, 1 * 1000);
    }

    private void stopVideo() {
        if(mAliyunVodPlayer != null){
            mAliyunVodPlayer.reset();
            mAliyunVodPlayer.stop();
        }
    }


    private void playLocalVideo(File file) {
        String path = "file://" + file.getPath();
        LoggerHelper.i("playLocalVideo:" + path);
        stopVideo();
        if(mAliyunVodPlayer != null){
            mAliyunVodPlayer.setLoop(false);
            mAliyunVodPlayer.setAutoPlay(true);
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(path);
            mAliyunVodPlayer.setMute(videoMute);
            mAliyunVodPlayer.setDataSource(urlSource);
            mAliyunVodPlayer.prepare();
            mAliyunVodPlayer.start();

            surfaceView.setVisibility(View.VISIBLE);
        }
    }


    private CountDownTimer countDownTimer;
    private long lastTryToPlayNextAdTimeMillis = 0;
    private long currentADEndTimeMillis = 0;
    private AdItem currentADItem = null;
    private synchronized void playNextAd() {
        //避免视频广告播放后 导致后续广告过来两处理回调异常  一个是定时器发出的 一个是播放结束发出的
        if(System.currentTimeMillis() - lastTryToPlayNextAdTimeMillis < 500){
            return;
        }
        long delayTime = 0;
        mHandler.removeCallbacks(toNextAdRunnable);
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
            stopVideo();
            delayTime = ((int)(Math.random() * 4 + 3)) * 1000;

        }else if(!adItem.isFileExsits()){
            //有广告，但是广告还没下载完成
            ivDefualt.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            if(rotation == 0){
                imageView.setImageResource(R.drawable.drawable_ad_loading);
            }else if(rotation == 90){
                imageView.setImageResource(R.drawable.drawable_ad_loading_90);
            }else if(rotation == 180){
                imageView.setImageResource(R.drawable.drawable_ad_loading_180);
            }else if(rotation == 270){
                imageView.setImageResource(R.drawable.drawable_ad_loading_270);
            }
            surfaceView.setVisibility(View.INVISIBLE);
            stopVideo();
            if(0 == adItem.getType()){
                delayTime = ((int)(Math.random() * 3 + 5)) * 1000;
            }else{
                delayTime = ((int)(Math.random() * 5 + 5)) * 1000;
            }
            if(autoFadeInWhenNoAD){
                setAlpha(0.3f);
            }
        } else {//广告已经下载完成了，可以正常显示了
            if(autoFadeInWhenNoAD){
                setAlpha(1);
            }
            ivDefualt.setVisibility(View.INVISIBLE);
            if (2 == adItem.getType()) {
//              等视频开始渲染了在隐藏图片控件
                playLocalVideo(new File(DownloadManager.getDownloadRootPath(getContext()), adItem.getLocationFileName()));
            } else {
                surfaceView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                stopVideo();
                GlideUtil.loadImage(getContext(), adItem.locationFile(),imageView,rotation);
            }
            delayTime = adItem.getDuration() * 1000;
        }

        mHandler.postDelayed(toNextAdRunnable, delayTime);
        currentADEndTimeMillis = System.currentTimeMillis() + delayTime;
        lastTryToPlayNextAdTimeMillis = System.currentTimeMillis();
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
            playNextAd();
        }
    }

    public void pauseAD() {
        isPause = true;
        stopVideo();
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
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if(mAliyunVodPlayer != null){
            stopVideo();
            mAliyunVodPlayer.release();
            mAliyunVodPlayer = null;
        }
        if(mHandler != null && toNextAdRunnable != null){
            mHandler.removeCallbacks(toNextAdRunnable);
        }
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
