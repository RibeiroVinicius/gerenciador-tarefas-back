package com.gerenciadortarefas.gerenciador.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gerenciadortarefas.gerenciador.controller.dto.CreateUserDto;
import com.gerenciadortarefas.gerenciador.entities.*;
import com.gerenciadortarefas.gerenciador.repositories.*;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class UserController {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Handles the POST request for creating a new user.
     * 
     * @param dto The {@link CreateUserDto} object that contains the user data.
     * @return A {@link ResponseEntity} with the status code 201 Created.
     *         If the username already exists, a 422 Unprocessable Entity status is returned.
     */
    @Transactional
    @PostMapping("/user")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto) {

        // Check if the username already exists
        userRepository.findByName(dto.username()).ifPresent(user -> 
        {throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);});

        // Create the user
        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmail(dto.email());

        // Save the user
        userRepository.save(user);

        // Return a 201 Created status
        return ResponseEntity.ok().build();
    }
    
    
}
