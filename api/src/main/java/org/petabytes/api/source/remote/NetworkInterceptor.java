package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Supplier;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

class NetworkInterceptor implements Interceptor {

    private final Supplier<String> userAgentSupplier;
    private final Supplier<String> deviceIdSupplier;
    private final Supplier<String> fcmTokenSupplier;
    private final Supplier<String> accessTokenSupplier;

    NetworkInterceptor(@NonNull Supplier<String> userAgentSupplier, @NonNull Supplier<String> deviceIdSupplier,
                       @NonNull Supplier<String> fcmTokenSupplier, @NonNull Supplier<String> accessTokenSupplier) {
        this.userAgentSupplier = userAgentSupplier;
        this.deviceIdSupplier = deviceIdSupplier;
        this.fcmTokenSupplier = fcmTokenSupplier;
        this.accessTokenSupplier = accessTokenSupplier;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request()
            .newBuilder()
            .header("User-Agent", userAgentSupplier.get())
            .header("X-Device-Uid", deviceIdSupplier.get())
            .header("X-Push-Token", fcmTokenSupplier.get())
            .header("X-Access-Token", accessTokenSupplier.get())
            .build());
    }
}
