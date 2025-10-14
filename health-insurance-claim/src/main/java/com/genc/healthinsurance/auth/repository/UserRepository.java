package com.genc.healthinsurance.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genc.healthinsurance.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);

}

