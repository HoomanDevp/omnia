package ir.stts.bajet.client.config;

import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.Map;

public class RetrofitClient {

    private final Map<String, Retrofit> clients = new HashMap<>();

    void addClient(String beanName, Retrofit retrofit) {
        clients.put(beanName, retrofit);
    }

    public Retrofit get(String clientName) {
        return clients.get(clientName);
    }
}