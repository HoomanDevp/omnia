package ir.stts.bajet.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import ir.stts.bajet.client.authentication.ClientAuthenticator;
import ir.stts.bajet.client.authentication.IGatewayTokenManager;
import ir.stts.bajet.client.exception.IGatewayExceptionHandler;
import ir.stts.bajet.client.interceptor.CacheInterceptor;
import ir.stts.bajet.client.interceptor.DefaultInterceptor;
import ir.stts.bajet.client.interceptor.LoggingInterceptor;
import ir.stts.bajet.client.interceptor.RetryInterceptor;
import ir.stts.bajet.core.resilience.exception.ExitException;
import ir.stts.bajet.log.LogSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class RetrofitClientFactory {

    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;
    private final List<Interceptor> interceptors;
    private final List<IGatewayTokenManager> tokenManagers;
    private final List<IGatewayExceptionHandler> exceptionHandlers;

    public Retrofit getInstance(String beanName, ClientProperties.RetrofitProperties properties) {

        String exceptionHandlerBeanName = beanName + "ExceptionHandler";
        IGatewayExceptionHandler exceptionHandler = exceptionHandlers
                .stream()
                .filter(h -> h
                        .getClass()
                        .getSimpleName()
                        .equals(exceptionHandlerBeanName))
                .findFirst()
                .orElse(null);
        if (exceptionHandler == null) {

            String defaultExceptionHandlerName = properties.getDefaultExceptionHandlerName();
            exceptionHandler = exceptionHandlers
                    .stream()
                    .filter(h -> h
                            .getClass()
                            .getSimpleName()
                            .equals(defaultExceptionHandlerName))
                    .findFirst()
                    .orElse(null);
        }

        if (exceptionHandler == null) {

            RuntimeException ex = new RuntimeException("No exception handler found with name: " + beanName + "ExceptionHandler");
            log.error("{}", LogSpec.ofException("No exception handler found with name: " + beanName + "ExceptionHandler", ex));
            throw ex;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getBaseUrl())
                .client(createOkHttpClient(beanName, properties))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(new BCallAdapterFactory(exceptionHandler))
                .build();
        HttpUrl httpUrl = retrofit.baseUrl();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(httpUrl.host(), httpUrl.port());
        if (properties.isConnectionCheck()) {
            try (Socket socket = new Socket()) {
                socket.connect(inetSocketAddress);
            } catch (IOException e) {
                throw new ExitException("Could not connect to " + httpUrl.host() + ":" + httpUrl.port(), e);
            }
        }
        return retrofit;
    }

    private OkHttpClient createOkHttpClient(String beanName, ClientProperties.RetrofitProperties properties) {

        ClientProperties.RetrofitProperties.OkHttpProperties okhttpProperties =
                properties.getOkhttp();
        if (okhttpProperties == null)
            return new OkHttpClient.Builder().build();

        Cache cache = null;
        ClientProperties.RetrofitProperties.OkHttpProperties.CacheProperties cacheProperties =
                okhttpProperties.getCache();
        if (cacheProperties != null)
            cache = new Cache(new File(cacheProperties.getFileDir()), cacheProperties.getSize());

        ConnectionPool connectionPool = null;
        ClientProperties.RetrofitProperties.OkHttpProperties.ConnectionPoolProperties connectionPoolProperties =
                properties.getOkhttp().getConnectionPool();
        if (connectionPoolProperties != null)
            connectionPool = new ConnectionPool(
                    connectionPoolProperties.getMaxIdle(),
                    connectionPoolProperties.getKeepAliveDuration(), TimeUnit.SECONDS);

        Dispatcher dispatcher = null;
        ClientProperties.RetrofitProperties.OkHttpProperties.DispatcherProperties dispatcherProperties =
                properties.getOkhttp().getDispatcher();
        if (dispatcherProperties != null) {

            dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(dispatcherProperties.getMaxRequests());
            dispatcher.setMaxRequestsPerHost(dispatcherProperties.getMaxRequestsPerHost());
        }

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        okHttpBuilder.callTimeout(okhttpProperties.getCallTimeout(), TimeUnit.SECONDS)
                .readTimeout(okhttpProperties.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(okhttpProperties.getWriteTimeout(), TimeUnit.SECONDS)
                .connectTimeout(okhttpProperties.getConnectionTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(okhttpProperties.isRetryOnConnectionFailure());

        if (okhttpProperties.getProxy() != null) {
            ClientProperties.RetrofitProperties.OkHttpProperties.ProxyProperties prop = okhttpProperties.getProxy();
            Proxy proxy = new Proxy(prop.getType(), new InetSocketAddress(prop.getHost(), prop.getPort()));
            okHttpBuilder.proxy(proxy);
        }
        if (dispatcher != null)
            okHttpBuilder.dispatcher(dispatcher);

        if (connectionPool != null)
            okHttpBuilder.connectionPool(connectionPool);

        if (okhttpProperties.isEnableAuthenticator()) {

            String tokenManagerBeanName = beanName + "TokenManager";
            IGatewayTokenManager tokenManager = tokenManagers
                    .stream()
                    .filter(g -> g
                            .getClass()
                            .getSimpleName()
                            .equals(tokenManagerBeanName))
                    .findFirst().orElse(null);
            if (tokenManager == null) {

                String defaultTokenManagerName = okhttpProperties.getDefaultTokenManagerName();
                tokenManager = tokenManagers
                        .stream()
                        .filter(h -> h
                                .getClass()
                                .getSimpleName()
                                .equals(defaultTokenManagerName))
                        .findFirst()
                        .orElse(null);
            }

            if (tokenManager == null) {

                RuntimeException ex = new RuntimeException("No token manager found with name: " + beanName + "TokenManager");
                log.error("{}", LogSpec.ofException("No token manager found with name: " + beanName + "TokenManager", ex));
                throw ex;
            }

            ClientAuthenticator clientAuthenticator = new ClientAuthenticator(tokenManager);
            okHttpBuilder.authenticator(clientAuthenticator);
            okHttpBuilder.addInterceptor(clientAuthenticator);
        }

        okHttpBuilder.addInterceptor(new DefaultInterceptor(properties.isPropagateHeaders()));

        if (okhttpProperties.isEnableLogging())
            okHttpBuilder.addInterceptor(new LoggingInterceptor(meterRegistry));

        if (cache != null) {
            okHttpBuilder.cache(cache);
            okHttpBuilder.addInterceptor(new CacheInterceptor(cacheProperties.getMaxAge()));
        }

        ClientProperties.RetrofitProperties.OkHttpProperties.RetryProperties retryProperties =
                okhttpProperties.getRetry();
        if (retryProperties != null)
            okHttpBuilder.addInterceptor(new RetryInterceptor(
                    retryProperties.getRetryDelay(),
                    retryProperties.getMaxRetryCount()));

        for (String otherInterceptor : okhttpProperties.getInterceptors()) {

            Interceptor interceptor = interceptors
                    .stream()
                    .filter(g -> g
                            .getClass()
                            .getSimpleName()
                            .equals(otherInterceptor))
                    .findFirst().orElse(null);

            if (interceptor == null) {

                RuntimeException ex = new RuntimeException("No interceptor found with name: " + otherInterceptor);
                log.error("{}", LogSpec.ofException("No interceptor found with name: " + otherInterceptor, ex));
                throw ex;
            }

            okHttpBuilder.addInterceptor(interceptor);
        }

        if (okhttpProperties.isAllowInsecureConnection()) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                        }
                };

                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                okHttpBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                okHttpBuilder.hostnameVerifier((hostname, session) -> true);

                log.warn("SSL verification is bypassed! This should only be used in development environments.");
            } catch (Exception e) {
                throw new RuntimeException("Failed to configure unsafe SSL", e);
            }
        }

        return okHttpBuilder.build();
    }
}