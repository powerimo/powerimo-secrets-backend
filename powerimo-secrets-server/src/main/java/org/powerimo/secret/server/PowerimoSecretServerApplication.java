package org.powerimo.secret.server;

import org.powerimo.secret.server.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PowerimoSecretServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PowerimoSecretServerApplication.class, args);
    }

}
