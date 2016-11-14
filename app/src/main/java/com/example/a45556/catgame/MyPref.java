package com.example.a45556.catgame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 45556 on 2016-11-13.
 */

public class MyPref {
    private Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    public MyPref(Context context){
        mContext = context;
        pref = mContext.getSharedPreferences("data",Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public int[] getBestScore(){
        int[] data = new int[3];
        data[0] = pref.getInt("easy",88);
        data[1] = pref.getInt("normal",88);
        data[2] = pref.getInt("hard",88);
        return data;
    }

    public void saveBestScore(int diff,int bestScore){
        switch (diff){
            case 0:
                editor.putInt("easy",bestScore);
                break;
            case 1:
                editor.putInt("normal",bestScore);
                break;
            case 2:
                editor.putInt("expert",bestScore);
                break;
        }
        editor.commit();
    }

    public void saveConfig(){
        editor.putBoolean("BGM",MainActivity.BGM);
        editor.commit();
    }

    public boolean getConfig(){
        boolean Bgm =  pref.getBoolean("BGM",true);
        return Bgm;
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }
}
