package com.gerenciadortarefas.gerenciador.controller;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import com.gerenciadortarefas.gerenciador.controller.dto.*;
import com.gerenciadortarefas.gerenciador.entities.User;
import com.gerenciadortarefas.gerenciador.repositories.UserRepository;

@RestController
@RequestMapping("/auth") // Define um prefixo para organização das rotas
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private static final long EXPIRATION_TIME = 3000L; // 50 minutos

    public TokenController(JwtEncoder jwtEncoder,
                           UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica um usuário com base em seu nome e senha.
     *
     * <p>Se o usuário for encontrado e a senha for válida, gera um token JWT para o usuário.
     * Caso contrário, lan a uma exce o de autentica o inv lida.
     *
     * @param loginRequest dados de login do usu rio
     * @return um objeto {@link LoginResponse} com o token JWT gerado e o tempo de expira o do token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByName(loginRequest.username());

        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("Usuario n o encontrado!");
        }

        User user = optionalUser.get();

        if (!user.isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Senha incorreta!");
        }

        String token = generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token, EXPIRATION_TIME));
    }

    /**
     * Gera um token JWT para o usu rio com base em suas permiss es e dados de login.
     *
     * <p>O token JWT cont m as seguintes informa es:
     *     - Issuer: o nome do sistema que gerou o token (gerenciadortarefas)
     *     - Subject: o id do usu rio
     *     - IssuedAt: o momento em que o token foi gerado
     *     - ExpiresAt: o momento em que o token expira (5 minutos ap s a gera o)
     *     - Scope: as permiss es do usu rio separadas por espa o
     *
     * @param user o usuario que deseja gerar o token
     * @return o token JWT gerado
     */
    private String generateToken(User user) {
        Instant now = Instant.now();

        // Cria o objeto JwtClaimsSet com as informa es do token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gerenciadortarefas") // Nome do sistema
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRATION_TIME))
                .build();

        // Gera o token JWT com as informa es do objeto JwtClaimsSet
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
