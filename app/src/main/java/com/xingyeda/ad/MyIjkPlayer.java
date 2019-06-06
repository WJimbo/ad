package com.xingyeda.ad;

import android.content.Context;

import com.dueeeke.videoplayer.player.IjkPlayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyIjkPlayer extends IjkPlayer {
    private IMediaPlayer.OnInfoListener onInfoListener;

    public void setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    public MyIjkPlayer(Context context) {
        super(context);
    }

    @Override
    public void setOptions() {
        super.setOptions();
        if(onInfoListener != null){
            mMediaPlayer.setOnInfoListener(onInfoListener);
        }
//        mMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//                return false;
//            }
//        });
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
    }
}
