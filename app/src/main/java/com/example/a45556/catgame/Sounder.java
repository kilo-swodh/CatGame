package com.example.a45556.catgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

/**
 * Created by 45556 on 2016-11-11.
 */

public class Sounder {
    private SoundPool soundPool;
    private MediaPlayer bgPlayer;
    private Context mContext;

    public Sounder(Context context){
        mContext = context;
    }

    public void initSound(){
        bgPlayer = MediaPlayer.create(mContext,R.raw.bg);
    }

    public void startBgSound(){
        if (!bgPlayer.isPlaying()) {
            try {
                bgPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bgPlayer.start();
        }
    }

    public void stopSound(){
        bgPlayer.pause();
    }
}
