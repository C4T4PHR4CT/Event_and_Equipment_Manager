package com.SovietHouseholdAppliances.EventManager.model;

import android.annotation.SuppressLint;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance;
    private static final String baseURL = "https://eventmanager.nlevi.dev";

    private final EventManagerApi eventManagerApi;

    private RetrofitClient() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder.baseUrl(baseURL);
        /*OkHttpClient noCert = getUnsafeOkHttpClient();
        if (noCert != null)
            retrofitBuilder.client(noCert);*/
        Retrofit client = retrofitBuilder.build();
        eventManagerApi = client.create(EventManagerApi.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null)
            instance = new RetrofitClient();
        return instance;
    }

    public EventManagerApi getEventManagerApi() {
        return eventManagerApi;
    }

    //code is from https://futurestud.io/tutorials/retrofit-2-how-to-trust-unsafe-ssl-certificates-self-signed-expired
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }
}