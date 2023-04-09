package com.krsoftlab.carwashkiosk;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class WatchdogService extends Service implements Runnable{
    private static final String TAG = "WatchdogService";
    private static final int MANAGER_CHECK_TIME = 30000;
    private static Boolean mRunning = new Boolean(false);
    private Thread mThread;

    @Override
    public void onCreate() {
        super.onCreate();
        mThread = new Thread(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
            mThread.start();
        }
        if("STOP".equals(intent.getAction())){
            mRunning = false;
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        Log.d(TAG, "run() called");
        while(mRunning) {
                watchApp();
            try {
                Thread.sleep(MANAGER_CHECK_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void watchApp() {
        Log.d(TAG, "watchApp() called");
        if(!isAppRunning(this, getPackageName())){
            launchApp();
        }
    }

    private boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void launchApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent == null) {
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
