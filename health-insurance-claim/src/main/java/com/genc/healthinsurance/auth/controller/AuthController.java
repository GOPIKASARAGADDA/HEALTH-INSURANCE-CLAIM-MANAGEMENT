package com.genc.healthinsurance.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
 
    @Autowired
    private UserRepository userRepository;
 
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
 
        Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);
 
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);
            session.setAttribute("loggedInUserId", user.getUserId());
            session.setAttribute("userRole", user.getRole().name().toUpperCase());
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
 
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
 