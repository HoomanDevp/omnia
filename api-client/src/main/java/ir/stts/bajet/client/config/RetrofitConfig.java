package ir.stts.bajet.client.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        ClientProperties.class
})
public class RetrofitConfig {

    private final ClientProperties properties;

    @Bean
    public RetrofitClient retrofitClient(RetrofitClientFactory retrofitClientFactory) {

        final RetrofitClient client = new RetrofitClient();
        for (Map.Entry<String, ClientProperties.RetrofitProperties> entry : properties.getRetrofits().entrySet()) {

            String beanName = entry.getKey();
            ClientProperties.RetrofitProperties config = entry.getValue();
            Retrofit retrofit = retrofitClientFactory.getInstance(beanName, config);
            client.addClient(beanName, retrofit);
        }

        return client;
    }
}