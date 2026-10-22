package com.cinema.backend.controller;

import com.cinema.backend.dto.LoginRequest;
import com.cinema.backend.dto.RegisterRequest;
import com.cinema.backend.model.User;
import com.cinema.backend.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            User user = authService.registerCustomer(request);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Account created successfully. Please verify your account.",
                            "userId",
                            user.getUserId()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String token) {

        try {

            authService.verifyAccount(token);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Account verified successfully."
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {

            String role = authService.login(
                    request.getEmailAddress(),
                    request.getPassword()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Login successful.",
                            "role", role
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }
}