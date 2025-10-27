package com.genc.healthinsurance.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genc.healthinsurance.auth.entity.Role;
import com.genc.healthinsurance.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(Integer userId);
List<User> findByRole(Role role);
Optional<User> findByUsername(String username);
boolean existsByRole(Role role);

}

