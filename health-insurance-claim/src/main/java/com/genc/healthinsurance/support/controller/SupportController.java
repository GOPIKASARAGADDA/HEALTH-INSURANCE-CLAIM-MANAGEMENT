package com.genc.healthinsurance.support.controller;
 
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.support.entity.SupportTicket;
import com.genc.healthinsurance.support.service.SupportService;

import jakarta.servlet.http.HttpSession;
 
@Controller
@RequestMapping("/support")
public class SupportController {
	private static final Logger logger=LoggerFactory.getLogger(SupportController.class);

	
    @Autowired
    private SupportService supportService;
 
    // ---------------- Show all tickets for logged-in user ----------------
    @GetMapping
    public String showUserTickets(HttpSession session, Model model) {
    	logger.info("display tickets for user");
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
 
        List<SupportTicket> tickets = supportService.getAllTicketsByUser(user.getUserId());
        model.addAttribute("tickets", tickets);
        model.addAttribute("user", user);
        model.addAttribute("ticket", new SupportTicket()); // for raise ticket form
        return "support/user-tickets";
    }
 
    // ---------------- Create new ticket ----------------
    @PostMapping("/create")
    public String createTicket(@ModelAttribute("ticket") SupportTicket ticket, HttpSession session) {
    	logger.info("create ticket by user");

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
 
        supportService.createTicket(ticket, user);
        return "redirect:/support";
    }
 
    // ---------------- View single ticket details ----------------
    @GetMapping("/{ticketId}")
    public String viewTicket(@PathVariable Integer ticketId, Model model, HttpSession session) {
    	logger.info("request to view ticket details for ticketId{}",ticketId);

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
 
        SupportTicket ticket = supportService.getTicketDetails(ticketId);
        model.addAttribute("ticket", ticket);
        model.addAttribute("user", user);
        return "support/view-ticket";
    }
 
    // ---------------- Resolve ticket (Admin/Agent/Adjuster) ----------------
    @PostMapping("/{ticketId}/resolve")
    public String resolveTicket(@PathVariable Integer ticketId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
 
        try {
        	logger.info("request to resolve ticket with ticketId{}",ticketId);

            supportService.resolveTicket(ticketId, user);
        } catch (RuntimeException ex) {
            // Optionally, pass error message to model/session for frontend
        }
 
        return "redirect:/support/admin";
    }
 
    // ---------------- Show all tickets for Admin/Agent/Adjuster ----------------
    @GetMapping("/admin")
    public String showAllTicketsForAdmin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
 
        if (user.getRole().name().equalsIgnoreCase("POLICYHOLDER")) {
            return "redirect:/support";
        }
    	logger.info("request to view all tickets details for admin/adjuster");

        List<SupportTicket> tickets = supportService.getAllTickets();
        model.addAttribute("tickets", tickets);
        model.addAttribute("user", user);
        return "support/admin-tickets";
    }
}
 