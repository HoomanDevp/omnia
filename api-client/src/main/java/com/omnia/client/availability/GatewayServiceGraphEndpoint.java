package com.omnia.client.availability;

import com.omnia.client.config.RetrofitClient;
import com.omnia.log.AppLogger;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "gatewayServiceGraphInfo")
@RequiredArgsConstructor
public class GatewayServiceGraphEndpoint {
    private final AppLogger appLogger = new AppLogger(GatewayServiceGraphEndpoint.class);
    private final RetrofitClient retrofitClient;

    @ReadOperation
    public Map<String, Object> graphInfo() {
        Collection<Retrofit> clients = retrofitClient.getClients();
        Map<String, Object> info = new HashMap<>();
        for (Retrofit client : clients) {
            HttpUrl httpUrl = client.baseUrl();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(httpUrl.host(), httpUrl.port());
            String hostAndPort = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
            try (Socket socket = new Socket()) {
                socket.connect(inetSocketAddress);
                info.put(hostAndPort, Boolean.TRUE);
            } catch (IOException e) {
                appLogger.warnF("Actuator service graph endpoint error {}", e.getMessage());
                info.put(hostAndPort, Boolean.FALSE);
            }
        }
        return info;
    }
}
