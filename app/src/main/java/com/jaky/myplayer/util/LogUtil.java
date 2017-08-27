package com.jaky.myplayer.util;

import android.util.Log;

/**
 * Created by jaky on 2017/8/11.
 */

public class LogUtil {

    public static final int LOG_LEVEL_NONE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARN = 3;
    public static final int LOG_LEVEL_ERROR = 4;
    public static final int LOG_LEVEL_ALL = 5;

    public static final String TAG = "LogUtil";

    private static int logLevel = LOG_LEVEL_ALL;

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int level) {
        LogUtil.logLevel = level;
    }

    public static void o(String msg) {
        w(msg);
    }

    public static void w(String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN) {
            Log.w(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (getLogLevel() >= LOG_LEVEL_DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR) {
            Log.e(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL) {
            Log.v(TAG, msg);
        }
    }
}
