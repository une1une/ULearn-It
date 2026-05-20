package com.ulearnit.ulearnit;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;

public class SessionManager {
    private static final String PREFS_NAME = "ULearnItPrefs";
    private static final String DAILY_TIME_PREFIX = "daily_time_";
    private static long sessionStartTime = 0;

    public static void startSession() {
        sessionStartTime = System.currentTimeMillis();
    }

    public static void endSession(Context context) {
        if (sessionStartTime == 0) return;

        long elapsedMillis = System.currentTimeMillis() - sessionStartTime;
        sessionStartTime = 0;

        // Get current day of the week (1=Sunday, 2=Monday, ..., 7=Saturday)
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = DAILY_TIME_PREFIX + dayOfWeek;
        
        long totalMillis = prefs.getLong(key, 0);
        totalMillis += elapsedMillis;

        prefs.edit().putLong(key, totalMillis).apply();
    }

    public static float getDailyHours(Context context, int dayOfWeek) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long totalMillis = prefs.getLong(DAILY_TIME_PREFIX + dayOfWeek, 0);
        return totalMillis / (1000f * 60 * 60);
    }
}
