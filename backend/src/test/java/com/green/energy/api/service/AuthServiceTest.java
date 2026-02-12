package com.green.energy.api.service;

import com.green.energy.api.entity.RoleEntity;
import com.green.energy.api.entity.UserEntity;
import com.green.energy.api.repository.UserRepository;
import com.green.energy.api.service.AuthService;
import com.green.energy.api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {

        UserEntity user = new UserEntity();
        user.setUsername("admin");
        user.setPasswordHash("hashed");
        RoleEntity r = new RoleEntity();
        r.setName("ADMIN");
        user.setRole(r);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(encoder.matches("1234", "hashed"))
                .thenReturn(true);

        when(jwtService.generateToken("admin", "ADMIN"))
                .thenReturn("token123");

        var response = authService.login("admin", "1234");

        assertEquals("token123", response.token());
        assertEquals("ADMIN", response.role());
    }

    @Test
    void login_shouldThrowException_whenPasswordInvalid() {

        UserEntity user = new UserEntity();
        user.setUsername("admin");
        user.setPasswordHash("hashed");

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(encoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.login("admin", "wrong"));
    }
}

