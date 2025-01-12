package org.powerimo.secret.server.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.powerimo.secret.api.enums.SecretActionEnum;
import org.powerimo.secret.api.models.ActionCompleted;
import org.powerimo.secret.api.models.SecretInfo;
import org.powerimo.secret.api.models.SecretRequest;
import org.powerimo.secret.server.AppUtils;
import org.powerimo.secret.server.exceptions.LimitExceededException;
import org.powerimo.secret.server.exceptions.LinkPasswordException;
import org.powerimo.secret.server.exceptions.NotFoundException;
import org.powerimo.secret.server.services.SecretManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("secrets")
public class SecretController {
    private final SecretManagerService secretManagerService;

    @PostMapping
    public ResponseEntity<SecretInfo> postRequest(
            HttpServletRequest request,
            @RequestBody SecretRequest secretRequest) {
        var browserInfo = AppUtils.extractUserBrowserInfo(request);
        return ResponseEntity.ok(secretManagerService.createSecret(secretRequest, browserInfo));
    }

    @GetMapping("{code}")
    public ResponseEntity<String> getSecret(
            HttpServletRequest request,
            @PathVariable("code") String code,
            @RequestParam(required = false, name = "password") String password) {
        try {
            var browserInfo = AppUtils.extractUserBrowserInfo(request);
            return ResponseEntity.ok(secretManagerService.hitSecret(code, browserInfo, password));
        } catch (LimitExceededException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LinkPasswordException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("{code}/burn")
    public ResponseEntity<?> postBurnSecret(
            HttpServletRequest request,
            @PathVariable("code") String code) {
        try {
            var browserInfo = AppUtils.extractUserBrowserInfo(request);
            secretManagerService.burnSecret(code, browserInfo);
            var payload = ActionCompleted.builder()
                    .action(SecretActionEnum.BURN.name())
                    .status("COMPLETED")
                    .timestamp(AppUtils.nowUtc())
                    .build();
            return ResponseEntity.ok(payload);
        } catch (LimitExceededException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{code}/info")
    public ResponseEntity<SecretInfo> getSecretInfoByCode(@PathVariable("code") String code) {
        try {
            return ResponseEntity.ok(secretManagerService.getSecretInfo(code));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
