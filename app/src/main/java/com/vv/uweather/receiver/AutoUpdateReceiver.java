package com.vv.uweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vv.uweather.service.AutoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver {

    /**
     * 在 onReceive()方法中再次去启动 AutoUpdateService，就可以实现后台定时更 新的功能
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
