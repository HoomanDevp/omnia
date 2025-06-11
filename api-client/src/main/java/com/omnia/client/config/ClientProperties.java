package com.omnia.client.config;

import com.omnia.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".client")
public class ClientProperties {

    private Map<String, RetrofitProperties> retrofits = new HashMap<>();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RetrofitProperties {
        private boolean connectionCheck = false;
        private String baseUrl;
        private String defaultExceptionHandlerName;
        private boolean propagateHeaders;
        private OkHttpProperties okhttp;

        @Getter
        @Setter
        @Accessors(chain = true)
        public static class OkHttpProperties {

            private int callTimeout = 15;
            private int readTimeout = 15;
            private int writeTimeout = 15;
            private int connectionTimeout = 10;
            private boolean retryOnConnectionFailure = false;
            private boolean enableLogging = true;
            private boolean enableAuthenticator = true;
            private boolean allowInsecureConnection = false;
            private String defaultTokenManagerName;
            private ProxyProperties proxy;
            private RetryProperties retry;
            private CacheProperties cache;
            private DispatcherProperties dispatcher;
            private ConnectionPoolProperties connectionPool;
            private List<String> interceptors = new ArrayList<>();

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class RetryProperties {

                private int retryDelay = -1;
                private int maxRetryCount = 3;
            }

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class ProxyProperties {
                private int port;
                private String host;
                private Proxy.Type type;
            }

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class CacheProperties {

                private int maxAge = 60;
                private int size = 20971520;
                private String fileDir = "gw-cache";
            }

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class DispatcherProperties {

                private int maxRequests = 200;
                private int maxRequestsPerHost = 50;
            }

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class ConnectionPoolProperties {

                private int maxIdle = 50;
                private int keepAliveDuration = 300;
            }
        }
    }
}