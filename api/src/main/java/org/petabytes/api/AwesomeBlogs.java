package org.petabytes.api;

import org.petabytes.api.model.Feed;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public class AwesomeBlogs {

    private final Api api;

    public AwesomeBlogs(boolean loggable) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(loggable ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("http://awesome-blogs.petabytes.org")
            .build();

        api = retrofit.create(Api.class);
    }

    public Api api() {
        return api;
    }

    public interface Api {

        @GET("/feeds.json")
        Observable<Response<Feed>> feeds(@Query("group") String group);
    }
}
