package com.example.a45556.catgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

/**
 * Created by 45556 on 2016-11-11.
 */

class Sounder {
    private SoundPool soundPool;
    private static MediaPlayer bgPlayer;
    private static Context mContext;


    private static Sounder sounder = null;
    public static Sounder getInstance(Context context){
        mContext = context;
        if(sounder == null){							//多次判断,稍微提升了用锁旗位的效率
            synchronized(Sounder.class){
                if(sounder== null)
                    sounder = new Sounder();     //延迟加载
            }
        }
        return sounder;
    }

    public void initSound(){
        bgPlayer = MediaPlayer.create(mContext,R.raw.bg);
        bgPlayer.setLooping(true);
        //bgPlayer.set
    }

    public void startBgSound(){
        Log.d("Kilo", "startBgSound: is Playing? " + bgPlayer.isPlaying());
        if (!bgPlayer.isPlaying()) {
            /*
            try {
                bgPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            bgPlayer.start();
        }
    }

    public void stopSound(){
        bgPlayer.stop();
        bgPlayer.release();
    }
}
