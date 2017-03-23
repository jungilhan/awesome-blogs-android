package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Supplier;

import org.petabytes.api.DataSource;

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
import rx.functions.Func1;
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
            .filter(new Func1<Response<Feed>, Boolean>() {
                @Override
                public Boolean call(Response<Feed> response) {
                    return response.isSuccessful();
                }
            })
            .map(new Func1<Response<Feed>, Feed>() {
                @Override
                public Feed call(Response<Feed> response) {
                    return response.body();
                }
            })
            .map(new Func1<Feed, org.petabytes.api.source.local.Feed>() {
                @Override
                public org.petabytes.api.source.local.Feed call(Feed feed) {
                    return feed.toPersist(category);
                }
            });
    }

    public void markAsRead(@NonNull final org.petabytes.api.source.local.Entry entry) {
        awesomeBlogs.read(entry.getLink())
            .onErrorResumeNext(Observable.<Response<Object>>empty())
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
