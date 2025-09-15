package com.omnia.client.authentication;

import com.omnia.log.AppLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class ClientAuthenticator implements IGatewayAuthenticator {
    private final AppLogger appLogger = new AppLogger(ClientAuthenticator.class);
    private String currentToken;
    private final IGatewayTokenManager gatewayTokenManager;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request request = chain.request();
        if (StringUtils.hasText(currentToken))
            request = request.newBuilder()
                    .header(HttpHeaders.AUTHORIZATION, currentToken)
                    .build();

        Response proceed = chain.proceed(request);
        if (!proceed.isSuccessful()) {
            if (proceed.code()== 401) {
                authenticate(null,proceed);
                return chain.proceed(request);
            }
        }
        return proceed;
    }

    @Override
    public Request authenticate(Route route, Response response) {

        if (responseCount(response) >= 2) {

            appLogger.warnF("Too many authentication attempts for url={} ", response.request().url().toString());
            return null;
        }

        synchronized (this) {
            if (!StringUtils.hasText(currentToken) ||
                    (currentToken).equals(response.request().header(HttpHeaders.AUTHORIZATION)))
                try {
                    refreshToken();
                } catch (Exception e) {
                    return null;
                }
        }

        return response.request().newBuilder()
                .header(HttpHeaders.AUTHORIZATION, currentToken)
                .header(HttpHeaders.CONNECTION, "close")
                .build();
    }

    private void refreshToken() throws Exception {

        try {

            this.currentToken = gatewayTokenManager.getToken();
        } catch (Exception e) {

            appLogger.error("Failed to refresh token", e);
            throw e;
        }
    }

    private int responseCount(Response response) {

        int count = 1;
        while ((response = response.priorResponse()) != null)
            count++;

        return count;
    }
}
