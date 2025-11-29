package com.example.health_log.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Create an OkHttpClient and add the AuthInterceptor
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Use the custom client
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}