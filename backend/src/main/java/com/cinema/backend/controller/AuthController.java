package com.cinema.backend.controller;

import com.cinema.backend.dto.ForgotPasswordRequest;
import com.cinema.backend.dto.LoginRequest;
import com.cinema.backend.dto.LoginResult;
import com.cinema.backend.dto.RegisterRequest;
import com.cinema.backend.model.User;
import com.cinema.backend.service.AuthService;
import com.cinema.backend.dto.ResetPasswordRequest;

import jakarta.servlet.http.HttpSession;

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
    public ResponseEntity<?> verifyAccount(
            @RequestParam String token) {

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
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        try {

            LoginResult result = authService.login(
                    request.getEmailAddress(),
                    request.getPassword()
            );

            session.setAttribute(
                    "userId",
                    result.getUserId()
            );

            session.setAttribute(
                    "emailAddress",
                    request.getEmailAddress()
            );

            session.setAttribute(
                    "role",
                    result.getRole()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Login successful.",
                            "userId", result.getUserId(),
                            "role", result.getRole()
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Logout successful."
                )
        );
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(
            HttpSession session) {

        Object userId =
                session.getAttribute("userId");

        Object role =
                session.getAttribute("role");

        if (userId == null) {

            return ResponseEntity.ok(
                    Map.of(
                            "loggedIn", false
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "loggedIn", true,
                        "userId", userId,
                        "role", role
                )
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        try {

            String token = authService.requestPasswordReset(
                    request.getEmailAddress()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Password reset request created.",
                            "resetToken", token
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

    @PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(
        @RequestBody ResetPasswordRequest request) {

    try {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Password reset successfully."
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