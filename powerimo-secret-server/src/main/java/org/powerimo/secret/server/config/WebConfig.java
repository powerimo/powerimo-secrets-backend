package org.powerimo.secret.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig {

    private WebMvcConfigurer localConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Content-Type", "Authorization")
                        .allowCredentials(true);
            }
        };
    }

    private WebMvcConfigurer networkConfigurer() {
        return new WebMvcConfigurer() {
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(AppProperties appProperties) {
        if (appProperties.isCorsDisabled()) {
            log.info("CORS disabled, local webMvcConfigurer will be applied");
            return localConfigurer();
        } else {
            log.info("CORS enabled");
            return networkConfigurer();
        }
    }
}
