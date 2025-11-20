package com.example.health_log.network;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // If no user is logged in, proceed with the original request
            return chain.proceed(originalRequest);
        }

        try {
            // Get the token, blocking the thread.
            // Note: This is a simplification. In a real production app,
            // you might want to handle the async nature of getToken more gracefully.
            String token = Tasks.await(user.getIdToken(true)).getToken();

            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);

        } catch (ExecutionException | InterruptedException e) {
            // Handle error, for example, by proceeding with the original request
            // or by throwing an IOException.
            e.printStackTrace();
            return chain.proceed(originalRequest);
        }
    }
}
