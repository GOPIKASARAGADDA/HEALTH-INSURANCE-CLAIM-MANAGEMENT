package com.genc.healthinsurance.auth.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.auth.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {
	private static final Logger logger=LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;
 
    // ----------------- Show Login Form -----------------
    @GetMapping("/login")
    public String showLoginForm(Model model) {
    	logger.info("displaying login form");
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
        	logger.info("request for login by {}",username);

            User user = authService.loginUser(username, password);
            session.setAttribute("loggedInUser", user);
            session.setAttribute("loggedInUserId", user.getUserId());
            session.setAttribute("userName", user.getUsername());
            session.setAttribute("userRole", user.getRole().name());
            return "redirect:/home";
        } catch (RuntimeException e) {
        	logger.warn("login unsuccessfull");
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }
 
    // ----------------- Show Register Form -----------------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
    	logger.info("displaying register form");

        model.addAttribute("user", new User()); 
        return "auth/register";
    }
 
    // ----------------- Register -----------------
    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
        	logger.error("registration failed");
            model.addAttribute("user", user);
            return "auth/register"; // updated path
        }

        try {
        	logger.info("request for registration by {}",user);
            authService.registerUser(user); 
            return "redirect:/auth/login"; 
        } catch (IllegalStateException e) {
        	logger.warn("re-attempt to register as admin");
            model.addAttribute("user", user);
            model.addAttribute("adminExistsError", true);
            return "auth/register"; // updated path
        } catch (Exception e) {
        	logger.warn("re-attempt to register as duplicate user");
            model.addAttribute("user", user);
            model.addAttribute("duplicateError", true);
            return "auth/register"; // updated path
        }
    }
    
    
    @GetMapping("/profile")
    public String showProfile(Model model,HttpSession session) {
 Object userObj=session.getAttribute("loggedInUser");
 if(userObj==null) {
	 logger.warn("profile display failed");
	 return "redirect:/auth/login";
 }
 logger.warn("profile display request");

    	model.addAttribute("user",userObj);
    	return "auth/profile";
    }
 
    // ----------------- Logout -----------------

@GetMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate();
    String message = authService.logoutUser(); // Call the service method
    logger.info(message); // Log the message from service
    return "redirect:/home";
}

}
 