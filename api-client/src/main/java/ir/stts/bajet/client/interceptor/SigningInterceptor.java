package ir.stts.bajet.client.interceptor;

import ir.stts.bajet.core.header.constant.HeaderKey;
import ir.stts.bajet.core.security.LegacyUserData;
import ir.stts.bajet.core.security.UserDataHolder;
import ir.stts.bajet.core.util.RequestSignatureUtils;
import ir.stts.bajet.log.AppLogger;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class SigningInterceptor implements Interceptor {
    private final AppLogger appLogger = new AppLogger(SigningInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        LegacyUserData userData = UserDataHolder.get();
        Request request = chain.request();
        Request.Builder reqBuilder = request.newBuilder();
        Buffer buffer = new Buffer();
        if (request.body() != null) {
            request.body().writeTo(buffer);
        }
        byte[] requestBody = buffer.readByteArray();
        String body = new String(requestBody);
        String method = request.method().toLowerCase();
        String path = request.url().encodedPath();

        if (userData == null) {
            String clientInfo = request.headers().get(HeaderKey.CLIENT_INFO.getKey());
            return signWithoutTimeout(chain, request, reqBuilder, requestBody, body, method, path, clientInfo);
        }

        String clientInfo = request.headers().get(HeaderKey.CLIENT_INFO.getKey());

        if (StringUtils.hasText(userData.getHeaders().get(HeaderKey.REQUEST_TIMEOUT.getKey()))) {
            String timeoutString = userData.getHeaders().get(HeaderKey.REQUEST_TIMEOUT.getKey());
            long timeout = Long.parseLong(timeoutString);

            String signature = RequestSignatureUtils.computeSignature(method, path, timeout, clientInfo, body);
            reqBuilder.header(HeaderKey.REQUEST_SIGNATURE.getKey(), signature);
            reqBuilder.header(HeaderKey.REQUEST_TIMEOUT.getKey(), signature);
            Request orginalRequest = reqBuilder
                    .method(request.method(), requestBody.length != 0 ? RequestBody.create(requestBody, request.body().contentType()) : null)
                    .build();
            return chain.proceed(orginalRequest);
        }

        return signWithoutTimeout(chain, request, reqBuilder, requestBody, body, method, path, clientInfo);
    }

    @NotNull
    private Response signWithoutTimeout(Chain chain, Request request, Request.Builder reqBuilder, byte[] requestBody, String body, String method, String path, String clientInfo) throws IOException {
        String signature = RequestSignatureUtils.computeSignature(method, path, clientInfo, body);
        reqBuilder.header(HeaderKey.REQUEST_SIGNATURE.getKey(), signature);
        Request orginalRequest = reqBuilder
                .method(request.method(), requestBody.length != 0 ? RequestBody.create(requestBody, request.body().contentType()) : null)
                .build();
        return chain.proceed(orginalRequest);
    }

}