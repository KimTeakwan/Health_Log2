package com.example.health_log.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    // IMPORTANT: Replace with your computer's IP address to connect from a real device.
    // On Windows, run 'ipconfig' in Command Prompt.
    // On macOS/Linux, run 'ifconfig' or 'ip addr' in the terminal.
    // The backend server must be running on your computer and accessible on the same network.
    private static final String BASE_URL = "http://10.0.2.2:8000/";

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