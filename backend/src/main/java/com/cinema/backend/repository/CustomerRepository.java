package com.cinema.backend.repository;

import com.cinema.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByVerificationToken(String verificationToken);
}