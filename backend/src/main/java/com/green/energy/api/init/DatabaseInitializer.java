package com.green.energy.api.init;

import com.green.energy.api.entity.RoleEntity;
import com.green.energy.api.entity.UserEntity;
import com.green.energy.api.repository.RoleRepository;
import com.green.energy.api.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        initUsers();
    }


    public void initUsers() {

        RoleEntity adminRole = roleRepository
                .findByName("ADMIN")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("ADMIN");
                    return roleRepository.save(r);
                });

        RoleEntity userRole = roleRepository
                .findByName("USER")
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName("USER");
                    return roleRepository.save(r);
                });

        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPasswordHash(
                    passwordEncoder.encode("admin123")
            );
            admin.setRole(adminRole);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername("user");
            user.setPasswordHash(
                    passwordEncoder.encode("user123")
            );
            user.setRole(userRole);
            userRepository.save(user);
        }
    }

}
