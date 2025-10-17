package com.genc.healthinsurance.auth.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
 
    @Autowired
    private AuthService authService;
 
    // ----------------- Show Login Form -----------------
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // optional: add empty user object if your template uses th:object
        model.addAttribute("user", new User());
        return "auth/login";
    }
 
    // ----------------- Login -----------------
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = authService.loginUser(username, password);
            session.setAttribute("loggedInUser", user);
            session.setAttribute("loggedInUserId", user.getUserId());
            session.setAttribute("userName", user.getUsername());
            session.setAttribute("userRole", user.getRole().name());
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }
 
    // ----------------- Show Register Form -----------------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); // crucial for th:object in template
        return "auth/register";
    }
 
    // ----------------- Register -----------------
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            authService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
    @GetMapping("/profile")
    public String showProfile(Model model,HttpSession session) {
 Object userObj=session.getAttribute("loggedInUser");
 if(userObj==null) {
	 return "redirect:/login";
 }
    	model.addAttribute("user",userObj);
    	return "auth/profile";
    }
 
    // ----------------- Logout -----------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
 