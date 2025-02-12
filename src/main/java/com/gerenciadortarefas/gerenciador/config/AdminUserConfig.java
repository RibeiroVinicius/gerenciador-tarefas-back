package com.gerenciadortarefas.gerenciador.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gerenciadortarefas.gerenciador.entities.*;
import com.gerenciadortarefas.gerenciador.repositories.RoleRepository;
import com.gerenciadortarefas.gerenciador.repositories.UserRepository;

import jakarta.transaction.Transactional;

public class AdminUserConfig implements CommandLineRunner {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByName("admin");

        userAdmin.ifPresentOrElse(
            user -> {
                System.out.println("User admin already exists");
            },
            () -> {
                var user = new User();
                user.setName("admin");
                user.setEmail("admin@localhost");
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setRoles(Set.of(roleAdmin));
                userRepository.save(user);
            }
        );
    }


}
