package org.powerimo.secret.server.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.powerimo.secret.api.enums.SecretActionEnum;
import org.powerimo.secret.api.models.SecretInfo;
import org.powerimo.secret.api.models.SecretRequest;
import org.powerimo.secret.server.AppUtils;
import org.powerimo.secret.server.config.AppProperties;
import org.powerimo.secret.server.exceptions.LimitExceededException;
import org.powerimo.secret.server.exceptions.NotFoundException;
import org.powerimo.secret.server.exceptions.ServerException;
import org.powerimo.secret.server.generators.CodeGenerator;
import org.powerimo.secret.server.models.UserBrowserInfo;
import org.powerimo.secret.server.persistance.entities.SecretEntity;
import org.powerimo.secret.server.persistance.entities.SecretActionEntity;
import org.powerimo.secret.server.persistance.repositories.SecretActionRepository;
import org.powerimo.secret.server.persistance.repositories.SecretRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecretManagerService {
    public static final String CLEANUP_IS_ALREADY_RUNNING = "Cleanup is already running";
    private final SecretRepository secretRepository;
    private final SecretActionRepository secretActionRepository;
    private final CodeGenerator codeGenerator;
    private final AppProperties appProperties;
    private final CryptService cryptService;

    @Getter
    private boolean cleanupIsRunning = false;

    /**
     * Register a secret in database and returns DTO model
     * @param request parameters
     * @param browserInfo requester info
     * @return SecretInfo model
     */
    public SecretInfo createSecret(@NonNull SecretRequest request, UserBrowserInfo browserInfo) {
        var entity = addSecret(request, browserInfo);

        var url = appProperties.getBaseUrl() + "/" + entity.getCode();

        return SecretInfo.builder()
                .code(entity.getCode())
                .url(url)
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .hitCount(entity.getHitCount())
                .hitLimit(entity.getHitLimit())
                .build();
    }

    /**
     * Wrapper fof Code generation by CodeGenerator
     * @return generated code
     */
    private String generateCode() {
        var maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            var code = codeGenerator.generate();
            var isUnique = !secretRepository.findByCode(code).isPresent();
            if (isUnique) {
                log.debug("Code generation attempt [{}] of [{}] success: {}", i, maxAttempts, code);
                return code;
            } else {
                log.debug("Code generation attempt [{}] of [{}] unsuccess: {}", i, maxAttempts, code);
            }
        }
        throw new ServerException("Max attempts generation of code reached: " + maxAttempts);
    }

    /**
     * Adds secret to database and returns created entity
     * @param request parameters
     * @param browserInfo requester info
     * @return created entity
     */
    private SecretEntity addSecret(@NonNull SecretRequest request, UserBrowserInfo browserInfo) {
        try {
            var code = generateCode();
            long hitLimit = request.getHitLimit() != null ? request.getHitLimit() : 1L;
            Instant expiresAt;
            var data = cryptService.encrypt(request.getSecret());

            if (appProperties.isAllowCustomTtl() && request.getTtl() != null) {
                expiresAt = Instant.now().plus(request.getTtl(), ChronoUnit.SECONDS);
            } else {
                expiresAt = Instant.now().plus(appProperties.getDefaultTtl(), ChronoUnit.SECONDS);
            }

            var entity = SecretEntity.builder()
                    .id(UUID.randomUUID())
                    .code(code)
                    .data(data)
                    .hitCount(0L)
                    .hitLimit(hitLimit)
                    .expiresAt(expiresAt)
                    .createdAt(AppUtils.nowUtc())
                    .build();

            // fill browser info
            if (browserInfo != null) {
                entity.setRegistrarHost(browserInfo.getRemoteHost());
                entity.setRegistrarAgent(browserInfo.getUserAgent());
            }

            log.info("[{}] created secret: id={}", code, entity.getId());
            return secretRepository.save(entity);
        } catch (Exception e) {
            log.error("Exception on creating secret", e);
            throw new ServerException("Exception on creating secret", e);
        }
    }

    /**
     * Get {@link SecretEntity} from database by code.
     * @param code Code
     * @return SecretEntity if found, {@link NotFoundException} if not, {@link ServerException} on IO exceptions.
     */
    public SecretEntity getSecret(String code) {
        Optional<SecretEntity> entityOptional;

        try {
            entityOptional = secretRepository.findByCode(code);
        } catch (Exception ex) {
            log.error("[{}] Exception on getting secret", code, ex);
            throw new ServerException("Exception on getting secret. code=" + code, ex);
        }

        if (entityOptional.isPresent()) {
            return entityOptional.get();
        } else {
            log.info("[{}] secret not found", code);
            throw new NotFoundException("Secret not found: " + code);
        }
    }

    /**
     * Get SecretInfo DTO by code
     * @param code code
     * @return Secret DTO
     */
    public SecretInfo getSecretInfo(String code) {
        return getSecret(code).dto();
    }

    /**
     * Register hit of the link
     * @param code Code
     * @param browserInfo requester info
     * @return Decrypted data. If limits or TTL are reached {@link LimitExceededException} will be raised.
     * If Code is not found {@link NotFoundException} will be raised. On other IO exceptions {@link ServerException}
     * will be raised.
     */
    @Transactional
    public String hitSecret(@NonNull String code, UserBrowserInfo browserInfo) {
        var entity = getSecret(code);

        // check hit by hit count
        if (entity.getHitCount() != null && entity.getHitCount() >= entity.getHitLimit()) {
            log.info("[{}] limit hits reached: {}", code, entity.getHitLimit());
            throw new LimitExceededException("Limit hits is exceeded");
        }

        // check TTL
        var now = AppUtils.nowUtc();
        if (entity.getExpiresAt().isBefore(now)) {
            log.info("[{}] TTL reached: {} against {}", code, entity.getExpiresAt(), now);
            throw new LimitExceededException("Link is expired");
        }

        try {
            var data = cryptService.decrypt(entity.getData());

            // register hit
            var hitCount = entity.getHitCount() == null ? 0 : entity.getHitCount();
            entity.setHitCount(hitCount + 1);
            secretRepository.save(entity);

            SecretActionEntity secretActionEntity = SecretActionEntity.builder()
                    .id(UUID.randomUUID())
                    .secretId(entity.getId())
                    .action(SecretActionEnum.HIT)
                    .actionTime(AppUtils.nowUtc())
                    .build();
            if (browserInfo != null) {
                secretActionEntity.setAgent(browserInfo.getUserAgent());
                secretActionEntity.setHost(browserInfo.getRemoteHost());
            }
            secretActionRepository.save(secretActionEntity);

            log.info("[{}] secret hit: {}", code, browserInfo);

            return data;
        } catch (Exception e) {
            log.error("Exception on decrypting secret", e);
            throw new ServerException("Exception on decrypting secret. code=" + code, e);
        }
    }

    /**
     * Burns the existing secret
     * @param code Code
     * @param browserInfo requester info
     */
    @Transactional
    public void burnSecret(@NonNull String code, UserBrowserInfo browserInfo) {
        var entity = getSecret(code);
        secretActionRepository.deleteAllBySecretId(entity.getId());
        secretRepository.delete(entity);
        log.info("[{}] burn completed by {}", code, browserInfo);
    }

    /**
     * Remove expired secrets (expires < now)
     */
    @Transactional
    public void cleanupExpiredSecrets(String source) {
        if (cleanupIsRunning) {
            log.debug(CLEANUP_IS_ALREADY_RUNNING);
            return;
        }

        try {
            var n = AppUtils.nowUtc();
            log.info("cleanup expired secrets below {} started. Source: {}", n, source);
            var deletedActionsCount = secretActionRepository.deleteExpired(n);
            var deletedSecretsCount = secretRepository.deleteExpired(n);
            log.info("cleanup expired secrets below {} completed. Source: {}. Secrets deleted: {}. Actions deleted: {}",
                    n,
                    source,
                    deletedSecretsCount,
                    deletedActionsCount);
        } catch (Exception e) {
            log.error("Exception on cleanup expired secrets", e);
        } finally {
            cleanupIsRunning = false;
        }
    }

    /**
     * Remove shown secrets (hit limit = hit count)
     */
    @Transactional
    public void cleanupUsedSecrets(String source) {
        if (cleanupIsRunning) {
            log.debug(CLEANUP_IS_ALREADY_RUNNING);
            return;
        }

        try {
            log.info("cleanup used secrets started. Source: {}", source);
            var deletedActionsCount = secretActionRepository.deleteUsedSecretByHits();
            var deletedSecretsCount = secretRepository.deleteUsedSecretByHits();
            log.info("cleanup used secrets completed. Source: {}. Secrets deleted: {}. Actions deleted: {}",
                    source,
                    deletedSecretsCount,
                    deletedActionsCount);
        } catch (Exception e) {
            log.error("Exception on cleanup used secrets", e);
        } finally {
            cleanupIsRunning = false;
        }
    }

    @Transactional
    public void cleanup(String source) {
        cleanupExpiredSecrets(source);
        cleanupUsedSecrets(source);
    }

}
