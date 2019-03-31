package com.vv.uweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.vv.uweather.receiver.AutoUpdateReceiver;
import com.vv.uweather.util.HttpCallbackListener;
import com.vv.uweather.util.HttpUtil;
import com.vv.uweather.util.Utility;

/**
 * 后台自动更新天气
 * 需要创建一个长期在后台运行的定时任务，在 service 包下新建一个 AutoUpdateService 继承自 Service
 */

public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 在 onStartCommand()方法中先是开启了一个子线程，
     * 然后在子线程中调用 updateWeather()方法来更新天气，
     * 仍然会将服务器返回的天气数据交给 Utility 的 handleWeatherResponse()方法去处理，
     * 这样就可以把最新的天气信息存储到 SharedPreferences 文件中
     * 在 showWeather()方法的最后加入启动 AutoUpdateService 这个服务的代码，
     * 这样只要一旦选中了某个城市并成功更新天气之后，AutoUpdateService 就会一直在后台运行，并保证每 2 小时更新一次天气
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        // 8小时的毫秒数 暂时不在乎流量，设置为每两个小时更新一次数据
        int anHour = 2 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code","");
        String address =  "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
