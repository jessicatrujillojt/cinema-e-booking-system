package com.cinema.backend.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @GetMapping("/check")
    public ResponseEntity<?> checkAdmin(HttpSession session) {

        Object userId = session.getAttribute("userId");
        Object role = session.getAttribute("role");

        if (userId == null) {
            return ResponseEntity.status(401).body(
                    Map.of(
                            "success", false,
                            "message", "You must be logged in."
                    )
            );
        }

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(
                    Map.of(
                            "success", false,
                            "message", "Admin access required."
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Admin access granted.",
                        "userId", userId
                )
        );
    }
}