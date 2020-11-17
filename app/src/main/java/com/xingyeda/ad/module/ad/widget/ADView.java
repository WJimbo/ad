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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    private AliPlayer mAliyunVodPlayer;
    private Disposable disposable;
    private final Object lockObject = new Object();

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

        currentADEndTimeMillis = System.currentTimeMillis() + 1 * 1500;
        startCountDownDisposable();

    }

    private void createAliPlayer(){
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
					imageView.setVisibility(View.INVISIBLE);
                    releaseImageViewResouce(imageView);
                    imageView.setImageDrawable(null);
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
//            mAliyunVodPlayer.enableHardwareDecoder(false);
            mAliyunVodPlayer.setDisplay(surfaceView.getHolder());

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
                        playNextAd();
                    }
                })
                .doOnComplete(() -> {
                    startCountDownDisposable();
                })
                .subscribe();
    }

    private void stopAndRealseAliPlayer() {
        if(mAliyunVodPlayer != null){
            mAliyunVodPlayer.setOnCompletionListener(null);
            mAliyunVodPlayer.setOnErrorListener(null);
            mAliyunVodPlayer.setOnStateChangedListener(null);
            mAliyunVodPlayer.reset();
            mAliyunVodPlayer.stop();
            mAliyunVodPlayer.release();
            mAliyunVodPlayer = null;
        }
    }


    private void playLocalVideo(File file) {
        String path = "file://" + file.getPath();
        LoggerHelper.i("playLocalVideo:" + path);
        stopAndRealseAliPlayer();
        if(mAliyunVodPlayer == null){
            createAliPlayer();
        }
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


    private long currentADEndTimeMillis = 0;
    private AdItem currentADItem = null;
    private void playNextAd() {
        MyLog.i("PlayNextAD--->Start");
        synchronized (lockObject){
            //当前广告播放结束时间比当前时间晚  则认为广告在有效期  无需切换
            if(currentADEndTimeMillis >= System.currentTimeMillis()){
                return;
            }
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
                stopAndRealseAliPlayer();
                delayTime = ((int)(Math.random() * 4 + 3)) * 1000;

            }else if(!adItem.isFileExsits()){
                //有广告，但是广告还没下载完成
                releaseImageViewResouce(imageView);
                ivDefualt.setVisibility(View.INVISIBLE);
           		imageView.setVisibility(View.VISIBLE);
            	imageView.setImageDrawable(getImageViewDrawable(rotation));
                surfaceView.setVisibility(View.INVISIBLE);
                stopAndRealseAliPlayer();
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
                    playLocalVideo(new File(DownloadManager.getDownloadRootPath(getContext().getApplicationContext()), adItem.getLocationFileName()));
                } else {
                    surfaceView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    stopAndRealseAliPlayer();
                    releaseImageViewResouce(imageView);
                    GlideUtil.loadImage(getContext().getApplicationContext(), adItem.locationFile(),imageView,rotation);
                }
                delayTime = adItem.getDuration() * 1000;
            }
            currentADEndTimeMillis = System.currentTimeMillis() + delayTime;
            MyLog.i("PlayNextAD--->END");
        }
    }
    public void setDefaultImage(@DrawableRes int resID){
        imageView.setImageResource(resID);
        ivDefualt.setImageResource(resID);
    }


    private Drawable drawableRotation0,drawableRotation90,drawableRotation180,drawableRotation270;
    private Drawable getImageViewDrawable(float rotation){
        if(rotation == 90){
            if(drawableRotation90 == null){
                drawableRotation90 = getResources().getDrawable(R.drawable.drawable_ad_loading_90);
            }
            return drawableRotation90;
        }else if(rotation == 180){
            if(drawableRotation180 == null){
                drawableRotation180 = getResources().getDrawable(R.drawable.drawable_ad_loading_180);
            }
            return drawableRotation180;
        }else if(rotation == 270){
            if(drawableRotation270 == null){
                drawableRotation270 = getResources().getDrawable(R.drawable.drawable_ad_loading_270);
            }
            return drawableRotation270;
        }else{
            if(drawableRotation0 == null){
                drawableRotation0 = getResources().getDrawable(R.drawable.drawable_ad_loading);
            }
            return drawableRotation0;
        }
    }

    public void setCountDownTitleColor(int color){
        if(tvCountSecond != null){
            tvCountSecond.setTextColor(color);
        }
    }

    public static void releaseImageViewResouce(ImageView imageView) {
//        if (imageView == null) return;
//        Drawable drawable = imageView.getDrawable();
//        if (drawable != null && drawable instanceof BitmapDrawable) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//            Bitmap bitmap = bitmapDrawable.getBitmap();
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//        }
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
        stopAndRealseAliPlayer();
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
        stopAndRealseAliPlayer();
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
