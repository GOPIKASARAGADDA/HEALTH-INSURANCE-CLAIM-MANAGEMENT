package com.genc.healthinsurance.policy.repository;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.policy.entity.Policy;
 
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
 
    // Find all policies a user is enrolled in
    List<Policy> findByEnrolledUsersContaining(User user);
 
    // Get all active policies
    @Query("SELECT p FROM Policy p WHERE p.policyStatus = 'ACTIVE'")
    List<Policy> findAllActivePolicies();
 
    // Optional: check if a user is enrolled in a policy
    @Query("SELECT CASE WHEN :user MEMBER OF p.enrolledUsers THEN true ELSE false END FROM Policy p WHERE p = :policy")
    boolean existsByUserAndPolicy(@Param("user") User user, @Param("policy") Policy policy);
}