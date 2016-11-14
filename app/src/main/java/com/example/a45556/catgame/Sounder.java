package com.example.a45556.catgame;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by 45556 on 2016-11-11.
 */

class Sounder {
    private SoundPool soundPool;
    private static Context mContext;

    private static Sounder sounder = null;
    public static Sounder getInstance(){
        if(sounder == null){							//多次判断,稍微提升了用锁旗位的效率
            synchronized(Sounder.class){
                if(sounder== null)
                    sounder = new Sounder();     //延迟加载
            }
        }
        return sounder;
    }

    public static void setContext(Context context){
        mContext = context;
    }

    private int ringClick;
    private int ringEnd;
    public void initSound(){
        if(Build.VERSION.SDK_INT>=21){
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(2);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        }else {
            soundPool = new SoundPool(2,AudioManager.STREAM_SYSTEM,0);
        }
        ringClick = soundPool.load(mContext,R.raw.killing,1);
        ringEnd = soundPool.load(mContext,R.raw.end,0);
    }

    public void startClickSound(){
        if (MainActivity.BGM){
            soundPool.play(ringClick,1,1,2,0,1);
        }
    }

    public void startEndSound(){
        if (MainActivity.BGM){
            soundPool.play(ringEnd,1,1,2,0,1);
        }
    }

    public void stopClickSound(){
        soundPool.stop(ringClick);
    }

    public void stopEndSound(){
        soundPool.stop(ringEnd);
    }
}
