package org.powerimo.secret.server.generators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powerimo.secret.server.config.AppProperties;

import static org.junit.jupiter.api.Assertions.*;

class StringCodeGeneratorTest {
    private AppProperties appProperties;
    private StringCodeGenerator generator;

    @BeforeEach
    public void setUp() {
        appProperties = new AppProperties();
        generator = new StringCodeGenerator(appProperties);
    }

    @Test
    void generate_success() {
        var s = generator.generate();

        Assertions.assertNotNull(s);
        assertFalse(s.isEmpty());
        assertEquals(s.length(), appProperties.getDesiredCodeLength());
    }
}