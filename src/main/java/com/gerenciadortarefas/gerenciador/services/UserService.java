package com.gerenciadortarefas.gerenciador.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.gerenciadortarefas.gerenciador.repositories.UserRepository;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aqui você pode buscar o usuário no banco de dados
        if ("admin".equals(username)) {
            return new User("admin", "{noop}admin123", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }
    }

    public com.gerenciadortarefas.gerenciador.entities.User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("currentUser: " + principal);
        System.out.println("Tipo de principal: " + principal.getClass().getName());
    
        if (principal instanceof UserDetails) {
            System.out.println("dentro do if: " + principal.toString());
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
        } else {
            System.out.println("Principal não é do tipo UserDetails. Usando: " + principal.getClass().getName());
            return userRepository.findByName(principal.toString()).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
        }
    }
    
}
