package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

class UserAgentInterceptor implements Interceptor {

    private final String userAgent;

    UserAgentInterceptor(@NonNull String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build());
    }
}
