package com.genc.healthinsurance.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.auth.entity.Role;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User getUserById(Integer userId) {
		return userRepository.findByUserId(userId);
	}
	public List<User> getAllPolicyholders(){
		return userRepository.findByRole(Role.POLICYHOLDER);
	}
	
	public List<User> getAllAdjusters(){
		return userRepository.findByRole(Role.CLAIM_ADJUSTER);
	}
}
