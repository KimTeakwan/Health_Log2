package com.example.health_log.network;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Tasks;

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

        // If the user is not logged in, proceed with the original request
        if (user == null) {
            return chain.proceed(originalRequest);
        }

        try {
            // This is a blocking call to get the token.
            // OkHttp interceptors run on a background thread, so this is acceptable.
            String idToken = Tasks.await(user.getIdToken(false)).getToken();

            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + idToken)
                    .build();
            return chain.proceed(newRequest);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            // Propagate the failure instead of proceeding without auth
            throw new IOException("Failed to get Firebase ID token", e);
        }
    }
}