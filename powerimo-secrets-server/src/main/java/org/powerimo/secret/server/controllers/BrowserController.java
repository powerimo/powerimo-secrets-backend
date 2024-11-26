package org.powerimo.secret.server.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.powerimo.secret.server.models.UserBrowserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("browser")
@RequiredArgsConstructor
public class BrowserController {

    @GetMapping
    public ResponseEntity<UserBrowserInfo> getUserBrowserInfo(HttpServletRequest request) {
        UserBrowserInfo data = new UserBrowserInfo(request);
        return ResponseEntity.ok(data);
    }
}
