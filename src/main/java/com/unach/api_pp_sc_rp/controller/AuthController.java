package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.config.JwtService;
import com.unach.api_pp_sc_rp.dto.auth.LoginRequest;
import com.unach.api_pp_sc_rp.dto.auth.PasswordResetRequest;
import com.unach.api_pp_sc_rp.dto.auth.SignupRequest;
import com.unach.api_pp_sc_rp.exception.BadCredentialsException;
import com.unach.api_pp_sc_rp.service.auth.AuthService;
import com.unach.api_pp_sc_rp.service.BlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;
    private final BlacklistService blacklistService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, BlacklistService blacklistService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signupRequest) {
        try {
            logger.info("Register request: {}", signupRequest);
            authService.registerUser(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito");
        } catch (RuntimeException ex) {
            logger.error("Error during registration: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Login request: {}", loginRequest);
        try {
            LoginRequest.LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {

            logger.error("Authentication failed for user: {}", loginRequest.getUsername(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            authService.requestPasswordReset(request);
            return ResponseEntity.ok("Solicitud de restablecimiento de contraseña enviada");
        } catch (RuntimeException ex) {
            logger.error("Error requesting password reset: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Instant expiresAt = jwtService.getExpiration(token).toInstant();
            blacklistService.blacklistToken(token, expiresAt);
            logger.info("Token added to blacklist: {}", token);
            return ResponseEntity.ok("Logout exitoso");
        }
        return ResponseEntity.badRequest().body("Token inválido");
    }
}
