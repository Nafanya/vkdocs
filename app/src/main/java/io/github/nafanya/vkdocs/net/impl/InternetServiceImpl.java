package io.github.nafanya.vkdocs.net.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import io.github.nafanya.vkdocs.net.base.InternetService;

public class InternetServiceImpl extends BroadcastReceiver implements InternetService {
    private List<InternetStateListener> listeners = new ArrayList<>();
    private boolean isConnected;

    public InternetServiceImpl(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        isConnected = activeNetInfo != null;
    }

    @Override
    public boolean hasInternetConnection() {
        return isConnected;
    }

    @Override
    public void addListener(InternetStateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InternetStateListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        isConnected = activeNetInfo != null;
        if (!isConnected)
            return;

        if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)
            for (InternetStateListener listener: listeners)
                listener.onEnableWiFi();

        if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            for (InternetStateListener listener: listeners)
                listener.onEnableMobile();


        if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            for (InternetStateListener listener: listeners)
                listener.onEnableNetwork();
    }
}
