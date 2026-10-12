package cl.novaretail.carrito.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient productosRestClient(
            @Value("${services.productos.url}") String productosUrl) {

        return RestClient.builder()
                .baseUrl(productosUrl)
                .build();
    }
}
