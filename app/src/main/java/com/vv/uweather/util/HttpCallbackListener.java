package com.vv.uweather.util;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
