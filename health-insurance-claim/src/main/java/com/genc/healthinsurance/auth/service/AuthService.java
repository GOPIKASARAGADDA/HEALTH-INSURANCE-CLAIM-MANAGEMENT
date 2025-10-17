package com.genc.healthinsurance.auth.service;
 
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.auth.entity.Role;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
 
@Service
public class AuthService {
 
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    // ---------- Authentication / Registration ----------
 
    public void registerUser(User userData) {
    	String encodedPass=passwordEncoder.encode(userData.getPassword());
    	userData.setPassword(encodedPass);
        userRepository.save(userData);
    }
    //user.getPassword().equals(password)
    public User loginUser(String username, String password) {
        Optional<User> userData = userRepository.findByUsername(username);
        if (userData.isPresent()) {
            User user = userData.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }
 
    public String logoutUser() {
        return "Logged out successfully";
    }
 
    // ---------- User Management / Info Retrieval ----------
 
    public User getUserProfile(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
 
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
 
    public List<User> getAllPolicyholders() {
        return userRepository.findByRole(Role.POLICYHOLDER);
    }
 
    public List<User> getAllAdjusters() {
        return userRepository.findByRole(Role.CLAIM_ADJUSTER);
    }
}
 