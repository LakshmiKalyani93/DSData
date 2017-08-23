package com.mtuity.sensordetections.network;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author :paradigmcreatives
 */
public class NetworkUtil extends ContextWrapper {

    private static NetworkUtil networkUtil;

    public NetworkUtil(Context base) {
        super(base);
    }

    public static NetworkUtil getInstance(Context context) {
        if (networkUtil == null) {
            networkUtil = new NetworkUtil(context);
        }
        return networkUtil;
    }


    /**
     * Checks if is online.
     *
     * @param context the context
     * @return true, if is online
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null) {
            return netInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
