package ir.stts.bajet.client.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@RequiredArgsConstructor
public class RetryInterceptor implements Interceptor {

    private final int retryDelay;
    private final int maxRetryCount;

    @Override
    public Response intercept(Chain chain) throws IOException {

        int retryCount = 0;
        Response response;
        Request request = chain.request();
        String url = request.url().toString();
        if (url.contains("RETRYABLE:")) {

            request = request
                    .newBuilder()
                    .url(url.replace("RETRYABLE:", ""))
                    .build();
            do {
                response = chain.proceed(request);
                if (response.isSuccessful())
                    break;
                else
                    retryCount++;

                if (retryDelay != 0) {

                    long backoffDelay = retryDelay < 0 ? (long) Math.pow(2, retryCount) * 1000 : retryDelay;
                    try {
                        Thread.sleep(backoffDelay);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            } while (retryCount < maxRetryCount);
        } else
            response = chain.proceed(request);

        return response;
    }
}