package com.xingyeda.ad.module.main.widget;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.UrlSource;
import com.xingyeda.ad.module.datamanager.ADListManager;
import com.xingyeda.ad.module.datamanager.DownloadManager;
import com.xingyeda.ad.R;
import com.xingyeda.ad.util.GlideUtil;
import com.xingyeda.ad.util.MyLog;
import com.xingyeda.ad.util.RotateTransformation;
import com.xingyeda.ad.vo.AdItem;
import com.xingyeda.ad.vo.AdListResponseData;
import com.zz9158.app.common.utils.LoggerHelper;
import com.zz9158.app.common.widget.CustomView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ADView extends CustomView {

    public interface IADViewCallBack{
        void playAd(String sourceID);
    }
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

    private IADViewCallBack iadViewCallBack;

    private WeakReference<TextView> mWeakTvCountSecond; //显示倒计时的文字  用弱引用 防止内存泄漏



    public void setIadViewCallBack(IADViewCallBack iadViewCallBack) {
        this.iadViewCallBack = iadViewCallBack;
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

    private Runnable toNextAdRunnable = new Runnable() {
        @Override
        public void run() {
            playNextAd();
        }
    };

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

            mAliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
                @Override
                public void onPrepared() {
                    imageView.setVisibility(View.INVISIBLE);
                }
            });
            mAliyunVodPlayer.setDisplay(surfaceView.getHolder());
            mHandler.postDelayed(toNextAdRunnable, 2 * 1000);
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
                        mWeakTvCountSecond.get().setText(lastSecond + "秒");
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

            mAliyunVodPlayer.setDataSource(urlSource);
            mAliyunVodPlayer.prepare();
            mAliyunVodPlayer.start();

            surfaceView.setVisibility(View.VISIBLE);
        }
    }

    private int currentShowAdIndex = -1;
    private CountDownTimer countDownTimer;
    private long lastTryToPlayNextAdTimeMillis = 0;
    private long currentADEndTimeMillis = 0;
    private synchronized void playNextAd() {
        //避免视频广告播放后 导致后续广告过来两处理回调异常  一个是定时器发出的 一个是播放结束发出的
        if(System.currentTimeMillis() - lastTryToPlayNextAdTimeMillis < 500){
            return;
        }
        long delayTime = 0;
        mHandler.removeCallbacks(toNextAdRunnable);
        AdItem adItem = getNextADItem();
        if(isPause){
            adItem = null;
        }
        if (adItem == null) {
            ivDefualt.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            surfaceView.setVisibility(View.INVISIBLE);
            stopVideo();
            delayTime = 10000;
        } else {
            if(iadViewCallBack != null){
                iadViewCallBack.playAd(adItem.getId() + "");
            }
            ivDefualt.setVisibility(View.INVISIBLE);
            if ("2".equals(adItem.getFiletype())) {
//                pic.setVisibility(View.VISIBLE);
                playLocalVideo(new File(DownloadManager.getDownloadRootPath(getContext()), adItem.getLocationFileName()));
            } else {
                surfaceView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                stopVideo();
                GlideUtil.loadImage(getContext(), adItem.locationFile(DownloadManager.getDownloadRootPath(getContext())),imageView,new RotateTransformation(getContext(),rotation));
            }
            delayTime = adItem.getDuration() * 1000;
        }

        mHandler.postDelayed(toNextAdRunnable, delayTime);
        currentADEndTimeMillis = System.currentTimeMillis() + delayTime;
        lastTryToPlayNextAdTimeMillis = System.currentTimeMillis();
    }
    public void setDefaultImage(@DrawableRes int resID){
        ivDefualt.setImageResource(resID);
    }
    public void setCountDownTitleColor(int color){
        if(tvCountSecond != null){
            tvCountSecond.setTextColor(color);
        }
    }
    private synchronized AdItem getNextADItem() {
        AdItem adItem = null;
        List<AdItem> tempAdItemList = new ArrayList<>();
        AdListResponseData adListResponseData = ADListManager.getInstance(getContext()).getAdListResponseData();

        if (adListResponseData != null && adListResponseData.getObj() != null) {
            tempAdItemList.addAll(adListResponseData.getObj());
        }
        int tempShowIndex = -1;
        for (int index = 0; index < tempAdItemList.size(); index++) {
            AdItem tempAdItem = tempAdItemList.get(index);
            if (index > currentShowAdIndex) {
                if (tempAdItem.isFileExsits(DownloadManager.getDownloadRootPath(getContext()))) {
                    adItem = tempAdItem;
                    tempShowIndex = index;
                    break;
                }
            } else {
                if (adItem == null) {
                    if (tempAdItem.isFileExsits(DownloadManager.getDownloadRootPath(getContext()))) {
                        adItem = tempAdItem;
                        tempShowIndex = index;
                    }
                }
            }
        }
        currentShowAdIndex = tempShowIndex;
        return adItem;
    }
    private boolean isPause = false;
    public void resumeAD() {
        isPause = false;
        playNextAd();
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
}
