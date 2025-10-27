package com.genc.healthinsurance.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


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


}
