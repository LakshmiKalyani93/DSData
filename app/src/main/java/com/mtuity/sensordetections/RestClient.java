package com.mtuity.sensordetections;

import android.content.Context;

import com.squareup.okhttp.CipherSuite;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.TlsVersion;

import java.util.Collections;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Kalyani.
 */
public class RestClient implements RequestInterceptor {

    private static OkClient client;
    private static ConnectionSpec spec;
    private SensorServices sensorServices;

    public static final String TAG = RestClient.class.getSimpleName();

    public enum InstanceType {
        JDA, LIGHT_HOUSE
    }


    public static RestClient getInstance() {

        if (spec == null) {
            spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                            CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                    .build();
        }
        OkHttpClient client1 = SelfSigningClientBuilder.createClient();
        client = new OkClient(client1);
        client1.setConnectionSpecs(Collections.singletonList(spec));

        return new RestClient();
    }



    /**
     * Creating rest adapter
     * for search
     *
     * @param context
     */
    public SensorServices getSearchService(Context context) {
        final String cpaBaseUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places";
        //client=null;
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(cpaBaseUrl)
                .setLogLevel(RestAdapter.LogLevel.FULL).setRequestInterceptor(this)
                //.setClient(new retrofit.client.UrlConnectionClient())
                .setClient(client == null ? new retrofit.client.UrlConnectionClient() : client)
                .build();
        sensorServices = restAdapter.create(SensorServices.class);
        return sensorServices;
    }


    @Override
    public void intercept(RequestFacade request) {
        //do nothing
    }


}
