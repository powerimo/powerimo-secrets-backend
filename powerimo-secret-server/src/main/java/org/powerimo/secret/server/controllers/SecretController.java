package org.powerimo.secret.server.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.powerimo.secret.api.SecretInfo;
import org.powerimo.secret.api.SecretRequest;
import org.powerimo.secret.server.exceptions.LimitExceededException;
import org.powerimo.secret.server.exceptions.NotFoundException;
import org.powerimo.secret.server.services.SecretManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("secret")
public class SecretController {
    private final SecretManagerService secretManagerService;

    @PostMapping
    public ResponseEntity<SecretInfo> postRequest(
            HttpServletRequest request,
            @RequestBody SecretRequest secretRequest) {
        return ResponseEntity.ok(secretManagerService.createSecret(secretRequest));
    }

    @GetMapping("{code}")
    public ResponseEntity<String> getSecret(
            HttpServletRequest request,
            @PathVariable String code) {

        try {
            return ResponseEntity.ok(secretManagerService.hitSecret(code));
        } catch (LimitExceededException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
