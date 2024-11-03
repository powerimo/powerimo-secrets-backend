package org.powerimo.secret.server.config;

import lombok.Getter;
import lombok.Setter;
import org.powerimo.secret.server.generators.StringCodeGenerator;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    /// base URL for secret links
    private String baseUrl = "http://localhost:8080";

    /// default TTL of a secret
    private long defaultTtl = 60 * 60 * 24 * 7L;

    /// allow to specify TTL in a request
    private boolean allowCustomTtl = true;

    /// Implementation of CodeGenerator interface for generation codes
    private String generatorClass = StringCodeGenerator.class.getCanonicalName();

    /// Perform regular cleanup of expired secrets
    private boolean cleanup = true;

    /**
     * Cron-expression for cleanup
     */
    private String cleanupCron = "0 30 * * * *";

    /// if `true`: errors like 404, 400 will be redirected to frontend
    /// if `false`: errors will be returned as JSON (`ActionCompleted` action)
    private boolean redirectToFrontend = false;

    /**
     * Context path, used with `baseUrl` to redirect pages, e.g. for 404 it will be resoled as
     * {baseUrl}/{frontendContextPath}.
     * Applies when `redirectToFrontend=true`
     */
    private String frontendContextPath = "/app";

    /**
     * Desired of key length which CodeGenerator provides
     */
    private int desiredCodeLength = 64;

    /**
     * Secret key used to encrypt\decrypt secret data in database
     */
    private String cryptKey = "SecretKey";

    /**
     * Enables ServiceController (/service) endpoint
     */
    private boolean serviceEndpointEnabled = false;

}
