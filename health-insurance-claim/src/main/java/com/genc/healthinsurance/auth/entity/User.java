package com.genc.healthinsurance.auth.entity;

import java.util.HashSet;
import java.util.Set;

import com.genc.healthinsurance.policy.entity.Policy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    
    @Column(nullable = false, unique = true)

    private String username;
    
    @Column(nullable = false)

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)

    private String email;
    
    @ManyToMany(mappedBy = "enrolledUsers")
    private Set<Policy> enrolledPolicies = new HashSet<>();
     
    public Set<Policy> getEnrolledPolicies() {
        return enrolledPolicies;
    }
     
    public void setEnrolledPolicies(Set<Policy> enrolledPolicies) {
        this.enrolledPolicies = enrolledPolicies;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    
}
