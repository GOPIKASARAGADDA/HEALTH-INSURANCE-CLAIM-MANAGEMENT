package com.genc.healthinsurance.auth.service;
 
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.auth.entity.Role;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
 
@Service
public class AuthService {
	private static final Logger logger=LoggerFactory.getLogger(AuthService.class);

	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    // ---------- Authentication / Registration ----------
 
    public void registerUser(User userData) {
    	if (userData.getRole() == Role.ADMIN && userRepository.existsByRole(Role.ADMIN)) {
    		logger.warn("duplicate admin registration call");
    	    throw new IllegalStateException("An admin already exists.");
    	}
    	String encodedPass=passwordEncoder.encode(userData.getPassword());
    	userData.setPassword(encodedPass);
    	logger.info("User registration success");
        userRepository.save(userData);
    }
    //user.getPassword().equals(password)
    public User loginUser(String username, String password) {
        Optional<User> userData = userRepository.findByUsername(username);
        if (userData.isPresent()) {
            User user = userData.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
            	logger.info("{} login success",username);

                return user;
            } else {
            	logger.warn("wrong password");
                throw new RuntimeException("Invalid password");
            }
        } else {
        	logger.warn("no user");
            throw new RuntimeException("User not found");
        }
    }
 
    // ---------- User Management / Info Retrieval ----------
 
    public User getUserProfile(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
 
 
    public List<User> getAllPolicyholders() {
        return userRepository.findByRole(Role.POLICYHOLDER);
    }
 
    public List<User> getAllAdjusters() {
        return userRepository.findByRole(Role.CLAIM_ADJUSTER);
    }
	public String logoutUser() {
		// TODO Auto-generated method stub
		return "logged out successfully";
	}
}
 