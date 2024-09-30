package org.powerimo.secret.server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.powerimo.secret.api.SecretInfo;
import org.powerimo.secret.api.SecretRequest;
import org.powerimo.secret.server.config.AppProperties;
import org.powerimo.secret.server.exceptions.LimitExceededException;
import org.powerimo.secret.server.exceptions.NotFoundException;
import org.powerimo.secret.server.exceptions.ServerException;
import org.powerimo.secret.server.generators.CodeGenerator;
import org.powerimo.secret.server.persistance.entities.SecretEntity;
import org.powerimo.secret.server.persistance.repositories.SecretRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecretManagerService {
    private final SecretRepository secretRepository;
    private final CodeGenerator codeGenerator;
    private final AppProperties appProperties;
    private final CryptService cryptService;

    public SecretInfo createSecret(SecretRequest request) {
        var entity = addSecret(request);

        return SecretInfo.builder()
                .code(entity.getCode())
                .url("http://localhost:8080/secret/" + entity.getCode())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .hitCount(entity.getHitCount())
                .build();
    }

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
        throw new ServerException("Max attempts reached: " + maxAttempts);
    }

    private SecretEntity addSecret(SecretRequest request) {
        try {
            var code = generateCode();
            Integer hitLimit = request.getHitLimit() != null ? request.getHitLimit().intValue() : 1;
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
                    .hitCount(0)
                    .hitLimit(hitLimit)
                    .expiresAt(expiresAt)
                    .build();
            log.info("[{}] created secret: id={}", code, entity.getId());
            return secretRepository.save(entity);
        } catch (Exception e) {
            log.error("Exception on creating secret", e);
            throw new ServerException("Exception on creating secret", e);
        }
    }

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

    public String hitSecret(String code) {
        var entity = getSecret(code);

        // check hit
        if (entity.getHitCount() != null && entity.getHitCount() >= entity.getHitLimit()) {
            log.info("[{}] limit hits reached: {}", code, entity.getHitLimit());
            throw new LimitExceededException("Limit hits is exceeded");
        }

        try {
            var data = cryptService.decrypt(entity.getData());
            // register hit
            var hitCount = entity.getHitCount() == null ? 0 : entity.getHitCount();
            entity.setHitCount(hitCount + 1);
            secretRepository.save(entity);

            log.info("[{}] secret hit: id={}", code, entity.getId());

            return data;
        } catch (Exception e) {
            log.error("Exception on decrypting secret", e);
            throw new ServerException("Exception on decrypting secret. code=" + code, e);
        }
    }

}
