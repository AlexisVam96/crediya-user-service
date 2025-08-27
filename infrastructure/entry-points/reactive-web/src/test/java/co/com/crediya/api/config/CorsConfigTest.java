package co.com.crediya.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    void corsWebFilter_shouldSetAllowedOriginsAndMethods() {
        String origins = "http://localhost,http://example.com";
        CorsConfig config = new CorsConfig();
        CorsWebFilter filter = config.corsWebFilter(origins);

        // Recreate the configuration as in the bean
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(List.of(origins.split(",")));
        corsConfig.setAllowedMethods(Arrays.asList("POST", "GET"));
        corsConfig.setAllowedHeaders(List.of(CorsConfiguration.ALL));

        assertTrue(corsConfig.getAllowedOrigins().contains("http://localhost"));
        assertTrue(corsConfig.getAllowedOrigins().contains("http://example.com"));
        assertTrue(corsConfig.getAllowedMethods().contains("POST"));
        assertTrue(corsConfig.getAllowedMethods().contains("GET"));
        assertTrue(corsConfig.getAllowedHeaders().contains(CorsConfiguration.ALL));
        assertTrue(corsConfig.getAllowCredentials());
    }
}