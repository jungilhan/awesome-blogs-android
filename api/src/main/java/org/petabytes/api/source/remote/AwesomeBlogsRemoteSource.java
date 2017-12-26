package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Supplier;

import org.petabytes.api.DataSource;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AwesomeBlogsRemoteSource implements DataSource {

    private final AwesomeBlogs awesomeBlogs;

    public AwesomeBlogsRemoteSource(@NonNull Supplier<String> userAgentSupplier, @NonNull Supplier<String> deviceIdSupplier,
                                    @NonNull Supplier<String> fcmTokenSupplier, @NonNull Supplier<String> accessTokenSupplier, boolean loggable) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(loggable ? HttpLoggingInterceptor.Level.HEADERS : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new NetworkInterceptor(userAgentSupplier, deviceIdSupplier, fcmTokenSupplier, accessTokenSupplier))
            .addNetworkInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("https://awesome-blogs.petabytes.org")
            .build();

        awesomeBlogs = retrofit.create(AwesomeBlogs.class);
    }

    @Override
    public Observable<org.petabytes.api.source.local.Feed> getFeed(@NonNull final String category) {
        return awesomeBlogs.feeds(category)
            .filter(Response::isSuccessful)
            .map(Response::body)
            .map(feed -> feed.toPersist(category));
    }

    public void markAsRead(@NonNull final org.petabytes.api.source.local.Entry entry) {
        awesomeBlogs.read(entry.getLink())
            .onErrorResumeNext(Observable.empty())
            .subscribeOn(Schedulers.io())
            .subscribe();
    }

    interface AwesomeBlogs {

        @GET("/feeds.json")
        Observable<Response<Feed>> feeds(@Query("group") String group);

        @FormUrlEncoded
        @POST("feeds/read.json")
        Observable<Response<Object>> read(@Field("url") String url);
    }
}
