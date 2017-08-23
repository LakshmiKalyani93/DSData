package com.mtuity.sensordetections.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkStateListener extends BroadcastReceiver {

    public static final int NETWORK_CONNECTED = 1;

    public static final int NO_NETWORK_CONNECTIVITY = 2;

    private static final List<NetworkStateChangeListener> LISTENERS = new CopyOnWriteArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle networkStateInfo = intent.getExtras();
        if (networkStateInfo != null) {
            handleBroadCast(intent);
        }
    }

    private void handleBroadCast(Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED && !LISTENERS.isEmpty()) {
            connected();
        } else if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.DISCONNECTED && !LISTENERS.isEmpty()) {
            disconnected();
        }
    }

    private void disconnected() {
        for (NetworkStateChangeListener mListener : LISTENERS) {
            if (mListener != null) {
                mListener.onNetworkStateChanged(NO_NETWORK_CONNECTIVITY);
            }
        }
    }

    private void connected() {
        for (NetworkStateChangeListener mListener : LISTENERS) {
            if (mListener != null) {
                mListener.onNetworkStateChanged(NETWORK_CONNECTED);
            }
        }
    }

    public static void registerNetworkState(NetworkStateChangeListener listener) {
        synchronized (LISTENERS) {
            if (!LISTENERS.contains(listener)) {
                LISTENERS.add(listener);
            }
        }
    }

    public static void unregisterNetworkState(NetworkStateChangeListener listener) {
        LISTENERS.remove(listener);

    }


}
