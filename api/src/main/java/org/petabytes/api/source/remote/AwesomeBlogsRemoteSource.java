package org.petabytes.api.source.remote;

import android.support.annotation.NonNull;

import org.petabytes.api.DataSource;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

public class AwesomeBlogsRemoteSource implements DataSource {

    private final AwesomeBlogs awesomeBlogs;

    public AwesomeBlogsRemoteSource(boolean loggable) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(loggable ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addNetworkInterceptor(new UserAgentInterceptor("awesome-blogs-android"))
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

    interface AwesomeBlogs {

        @GET("/feeds.json")
        Observable<Response<Feed>> feeds(@Query("group") String group);
    }
}
