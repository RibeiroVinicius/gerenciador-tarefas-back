package com.gerenciadortarefas.gerenciador.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gerenciadortarefas.gerenciador.controller.dto.CreateUserDto;
import com.gerenciadortarefas.gerenciador.entities.*;
import com.gerenciadortarefas.gerenciador.repositories.*;

import jakarta.transaction.Transactional;

import com.gerenciadortarefas.gerenciador.entities.Role;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class UserController {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto) {

        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        userRepository.findByName(dto.name()).ifPresent(user -> 
        {throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);});

        var user = new User();
        user.setName(dto.name());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());
        user.setRoles(Set.of(basicRole));

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
    
    
}
