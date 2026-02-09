package com.green.energy.api.controller;

import com.green.energy.api.dto.CreateUserRequest;
import com.green.energy.api.entity.RoleEntity;
import com.green.energy.api.entity.UserEntity;
import com.green.energy.api.repository.RoleRepository;
import com.green.energy.api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody CreateUserRequest req) {

        if (userRepository.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already exists"
            );
        }

        RoleEntity role = roleRepository
                .findByName(req.role())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Role not found"
                        )
                );

        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setPasswordHash(
                passwordEncoder.encode(req.password())
        );
        user.setRole(role);

        userRepository.save(user);
    }
}

