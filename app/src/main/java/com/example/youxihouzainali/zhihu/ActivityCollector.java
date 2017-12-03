package com.example.youxihouzainali.zhihu;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youxihouzainali on 2017/12/3.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }
    public static void finishAll() {
        for(Activity activity : activities) {
            if(!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
