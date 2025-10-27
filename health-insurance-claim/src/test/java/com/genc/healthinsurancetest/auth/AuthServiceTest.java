package com.genc.healthinsurancetest.auth;

import com.genc.healthinsurance.auth.entity.Role;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;
import com.genc.healthinsurance.auth.service.AuthService;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
 
import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
class AuthServiceTest {
 
    @Mock
    private UserRepository userRepository;
 
    @Mock
    private PasswordEncoder passwordEncoder;
 
    @InjectMocks
    private AuthService authService;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
 
    // ---------- Registration Tests ----------
 
    @Test
    void registerAdmin_whenAdminExists_shouldThrowException() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        admin.setPassword("admin123");
 
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true);
 
        assertThrows(IllegalStateException.class, () -> authService.registerUser(admin));
    }
 
    @Test
    void registerPolicyholder_shouldEncodePasswordAndSave() {
        User user = new User();
        user.setRole(Role.POLICYHOLDER);
        user.setPassword("plain");
 
        when(passwordEncoder.encode("plain")).thenReturn("encodedPass");
 
        authService.registerUser(user);
 
        assertEquals("encodedPass", user.getPassword());
        verify(userRepository).save(user);
    }
 
    // ---------- Login Tests ----------
 
    @Test
    void loginUser_withValidCredentials_shouldReturnUser() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("encodedPass");
 
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
 
        User result = authService.loginUser("john", "rawPass");
 
        assertEquals(user, result);
    }
 
    @Test
    void loginUser_withWrongPassword_shouldThrowException() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("encodedPass");
 
        when(userRepository.findByUsername("Vishal")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);
 
        assertThrows(RuntimeException.class, () -> authService.loginUser("Vishal", "wrongPass"));
    }
 
    @Test
    void loginUser_withUnknownUsername_shouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class, () -> authService.loginUser("unknown", "anyPass"));
    }
 
    // ---------- Profile Retrieval ----------
 
    @Test
    void getUserProfile_whenUserExists_shouldReturnUser() {
        User user = new User();
        user.setUserId(1);
 
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
 
        User result = authService.getUserProfile(1);
 
        assertEquals(user, result);
    }
 
    @Test
    void getUserProfile_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class, () -> authService.getUserProfile(99));
    }
 
    // ---------- Role-Based Queries ----------
 
    @Test
    void getAllPolicyholders_shouldReturnList() {
        List<User> users = List.of(new User(), new User());
 
        when(userRepository.findByRole(Role.POLICYHOLDER)).thenReturn(users);
 
        List<User> result = authService.getAllPolicyholders();
 
        assertEquals(2, result.size());
    }
 
    @Test
    void getAllAdjusters_shouldReturnList() {
        List<User> users = List.of(new User());
 
        when(userRepository.findByRole(Role.CLAIM_ADJUSTER)).thenReturn(users);
 
        List<User> result = authService.getAllAdjusters();
 
        assertEquals(1, result.size());
    }
 
    // ---------- Logout ----------
 
    @Test
    void logoutUser_shouldReturnSuccessMessage() {
        String result = authService.logoutUser();
        assertEquals("logged out successfully", result);
    }
}
 