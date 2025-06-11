package ir.stts.bajet.client.authentication;

import ir.stts.bajet.log.LogSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class ClientAuthenticator implements Authenticator, Interceptor {

    private String currentToken;
    private final IGatewayTokenManager gatewayTokenManager;

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (StringUtils.hasText(currentToken))
            request = request.newBuilder()
                    .header(HttpHeaders.AUTHORIZATION, currentToken)
                    .build();

        return chain.proceed(request);
    }

    @Override
    public Request authenticate(Route route, Response response) {

        if (responseCount(response) >= 2) {

            log.warn("{}", LogSpec.ofMessage("Too many authentication attempts for: ", response.request().url().toString()));
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

            log.error(LogSpec.ofException("Failed to refresh token", e).toString());
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
