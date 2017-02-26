package com.example.faizan.crunchtime;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class NBAService extends Service {

    private Thread newThread;
    public NBAService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        newThread = new Thread(new NotificationIssuer(NBAService.this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NBASoSC", "Top of onStartCommand");
        startThread();
        return super.onStartCommand(intent, flags, startId);
    }

    public void startThread(){
        newThread.start();
    }
}
