package com.cinema.backend.config;

import com.cinema.backend.model.Admin;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.AdminRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DemoDataInitializer(
            UserRepository userRepository,
            AdminRepository adminRepository) {

        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) {

        String adminEmail = "admin@cinema.com";

        if (userRepository.existsByEmailAddress(adminEmail)) {
            return;
        }

        User adminUser = new User();

        adminUser.setFirstName("Cinema");
        adminUser.setLastName("Admin");
        adminUser.setEmailAddress(adminEmail);
        adminUser.setPhoneNumber("7065550000");
        adminUser.setPassword(
                passwordEncoder.encode("Admin123!")
        );

        User savedAdmin =
                userRepository.save(adminUser);

        Admin admin = new Admin();

        admin.setUserId(savedAdmin.getUserId());

        adminRepository.save(admin);

        System.out.println(
                "Demo admin account created: " + adminEmail
        );
    }
}