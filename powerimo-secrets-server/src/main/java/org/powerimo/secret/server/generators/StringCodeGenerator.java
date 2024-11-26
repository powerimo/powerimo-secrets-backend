package org.powerimo.secret.server.generators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.powerimo.secret.server.config.AppProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class StringCodeGenerator implements CodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final AppProperties appProperties;
    private final static int DEFAULT_LENGTH = 64;

    public String generate() {
        SecureRandom random = new SecureRandom();
        int length = appProperties.getDesiredCodeLength();
        if (length <= 0) {
            log.warn("Desired length of the code is not specified. Default length will be used: {}", DEFAULT_LENGTH);
            length = DEFAULT_LENGTH;
        }
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

}
