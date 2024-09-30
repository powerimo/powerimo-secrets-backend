package org.powerimo.secret.server.config;

import lombok.Getter;
import lombok.Setter;
import org.powerimo.secret.server.generators.StringCodeGenerator;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "powerimo.secret")
@Getter
@Setter
public class AppProperties {
    private String domain;
    private long defaultTtl = 60 * 24 * 7L;
    private boolean allowCustomTtl = true;
    private String generatorClass = StringCodeGenerator.class.getCanonicalName();
    private boolean cleanup = true;
    private String frontendContextPath = "/app";
    private int minimumCodeLength = 16;
    private int desiredCodeLength = 64;
    private String cryptKey = "SecretKey";
}
