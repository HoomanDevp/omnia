package com.omnia.client.interceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@RequiredArgsConstructor
public class CacheInterceptor implements Interceptor {

    private final int maxAge;

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String url = request.url().toString();
        if (url.contains("CACHEABLE:"))
            request = request
                    .newBuilder()
                    .url(url.replace("CACHEABLE:", ""))
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();

        else
            request = request.newBuilder()
                    .header("Cache-Control", "no-cache")
                    .build();

        return chain.proceed(request);
    }
}