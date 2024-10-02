package org.powerimo.secret.server.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.powerimo.secret.server.services.SecretManagerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final SecretManagerService secretManagerService;
    private final static String CLEANUP_SOURCE = "Scheduler";

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 1L)
    public void cleanupExpired() {
        log.info("Planned cleanup initiated");
        secretManagerService.cleanup(CLEANUP_SOURCE);
    }
}
