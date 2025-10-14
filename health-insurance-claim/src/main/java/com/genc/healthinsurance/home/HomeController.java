package com.genc.healthinsurance.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Handles the root and /home mapping. 
     * We simplify the controller: The role-based display is now managed entirely by JavaScript 
     * on the client side (script.js) by reading localStorage, restoring the original logic.
     * The initial role is now always determined client-side.
     */
	@GetMapping("/")
    public String root() {
        // Returns: src/main/resources/templates/loading.html
        return "loading"; 
    }
	
    @GetMapping("/home")
    public String homePage(Model model) {
        // No need to pass 'currentRole' or read cookies here.
        // We ensure the template engine renders successfully, then JS takes over.
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        // Placeholder mapping for the login page link
        return "auth/login"; 
    }
    @GetMapping("/register")
    public String registerPage() {
        // Placeholder mapping for the login page link
        return "register"; 
    }
}
