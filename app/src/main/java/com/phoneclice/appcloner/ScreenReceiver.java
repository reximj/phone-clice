package com.phoneclice.appcloner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                KeepAliveService.startService(context);
            } else if (Intent.ACTION_SCREEN_ON.equals(action) || 
                       Intent.ACTION_USER_PRESENT.equals(action)) {
                KeepAliveService.startService(context);
            }
        }
    }
}
