package com.repol.mfec.mfec_test.github;

import java.io.IOException;

import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 21/9/2560.
 */
public class GitHubProvider {
    private static final String BASE_URL = "https://api.github.com";
    private static GitHubService gitHubService;

    private GitHubProvider() {
    }

    public static GitHubService getGitHubService() {
        if (gitHubService == null) {
            final OkHttpClient.Builder httpClient =
                    new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    final Request original = chain.request();
                    final HttpUrl originalHttpUrl = original.url();
                    final HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("access_token", "29ca986208dd8e4a5f17f1f1c4b33207f4874c4c")
                            .build();

                    final Request.Builder requestBuilder = original.newBuilder()
                            .url(url);
                    final Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));
            Retrofit retrofit = builder.build();
            gitHubService = retrofit.create(GitHubService.class);
        }
        return gitHubService;
    }
}
