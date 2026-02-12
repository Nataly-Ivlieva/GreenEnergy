package com.green.energy.api.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void shouldGenerateAndParseToken() {

        String token = jwtService.generateToken("admin", "ADMIN");

        JwtService.JwtUser user = jwtService.parse(token);

        assertEquals("admin", user.username());
        assertEquals("ADMIN", user.role());
    }
}

