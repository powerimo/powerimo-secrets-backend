package org.powerimo.secret.server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.powerimo.secret.server.config.AppProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptService {
    private final AppProperties appProperties;

    // Method for generate secret key base on text key
    public SecretKey getKeyFromText(String keyText) {
        if (keyText == null || keyText.isEmpty())
            throw new IllegalArgumentException("Crypt key is not set");

        byte[] keyBytes = keyText.getBytes();
        keyBytes = java.util.Arrays.copyOf(keyBytes, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String data) throws Exception {
        SecretKey key = getKeyFromText(appProperties.getCryptKey());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws Exception {
        SecretKey key = getKeyFromText(appProperties.getCryptKey());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
}
