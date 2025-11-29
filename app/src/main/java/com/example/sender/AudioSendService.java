package com.example.sender;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.sender.firebase.FirebaseCommandListener;
import com.example.sender.utils.NotificationHelper;
import com.example.sender.webrtc.WebRTCManager;

public class AudioSendService extends Service {

    private WebRTCManager rtcManager;
    private FirebaseCommandListener cmdListener;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(1, NotificationHelper.build(this));

        rtcManager = new WebRTCManager(this);

        cmdListener = new FirebaseCommandListener(new FirebaseCommandListener.CommandCallback() {
            @Override
            public void onStart() {
                rtcManager.startCall();
            }

            @Override
            public void onStop() {
                rtcManager.stopCall();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rtcManager != null) rtcManager.stopCall();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
