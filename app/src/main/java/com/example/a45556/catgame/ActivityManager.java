package com.example.a45556.catgame;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 45556 on 2016-11-14.
 */

public class ActivityManager{
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
