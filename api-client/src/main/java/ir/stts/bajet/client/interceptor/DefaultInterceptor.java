package ir.stts.bajet.client.interceptor;

import ir.stts.bajet.core.header.constant.HeaderKey;
import ir.stts.bajet.core.header.model.ClientInfo;
import ir.stts.bajet.core.security.LegacyUserData;
import ir.stts.bajet.core.security.UserDataHolder;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class DefaultInterceptor implements Interceptor {
    private final boolean propagateHeaders;

    @Override
    public Response intercept(Chain chain) throws IOException {
        LegacyUserData userData = UserDataHolder.get();
        Headers headers = chain.request().headers();
        Request.Builder requestBuilder = chain.request()
                .newBuilder();


        if (userData != null) {

            if (!StringUtils.hasText(headers.get(HeaderKey.JOB_ID.getKey()))) {
                requestBuilder.header(HeaderKey.JOB_ID.getKey(), userData.getJobId());
            }

            if (!StringUtils.hasText(headers.get(HeaderKey.MSG_ID.getKey()))) {
                requestBuilder.header(HeaderKey.MSG_ID.getKey(), userData.getMsgId());
            }
            if (propagateHeaders) {
                if (userData.getClientInfo() != null) {
                    String clientInfo = userData.getHeaders().get(HeaderKey.CLIENT_INFO.getKey());
                    requestBuilder.header(HeaderKey.CLIENT_INFO.getKey(), clientInfo);
                }
                if (userData.getUserInfo() != null) {
                    String userInfo = userData.getHeaders().get(HeaderKey.USER_INFO.getKey());
                    requestBuilder.header(HeaderKey.USER_INFO.getKey(), userInfo);
                }
                if (!StringUtils.hasText(headers.get("deviceId"))) {

                    if (userData.getClientInfo() != null) {
                        ClientInfo clientInfo = userData.getClientInfo();
                        if (StringUtils.hasText(clientInfo.getDeviceId())) {
                            requestBuilder.header("deviceId", clientInfo.getDeviceId());
                        }
                    }

                    if (StringUtils.hasText(userData.getHeaders().get("deviceid"))) {
                        requestBuilder.header("deviceId", userData.getHeaders().get("deviceid"));
                    }

                }

                if (!StringUtils.hasText(headers.get("referenceNumber"))) {
                    if (StringUtils.hasText(userData.getReferenceNumber())) {
                        requestBuilder.header("referenceNumber", userData.getReferenceNumber());
                    }
                }

                if (!StringUtils.hasText(headers.get("traceNumber"))) {
                    if (StringUtils.hasText(userData.getTraceNumber())) {
                        requestBuilder.header("traceNumber", userData.getTraceNumber());
                    }
                }
            }
        }
        requestBuilder.header(HttpHeaders.CONNECTION, "close");

        return chain.proceed(requestBuilder.build());
    }
}
