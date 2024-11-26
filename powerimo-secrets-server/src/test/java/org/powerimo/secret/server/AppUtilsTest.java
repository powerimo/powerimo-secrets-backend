package org.powerimo.secret.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class AppUtilsTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    void extractUserBrowserInfo_success() {
        String userAgent = "CustomAgent/1.0.0";
        var r = new MockHttpServletRequest();
        r.addHeader("User-Agent", userAgent);

        var result = AppUtils.extractUserBrowserInfo(r);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userAgent, result.getUserAgent());
    }

    @Test
    void extractUserBrowserInfo_noUserAgent() {
        var r = new MockHttpServletRequest();
        var result = AppUtils.extractUserBrowserInfo(r);
        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getUserAgent());
    }
}