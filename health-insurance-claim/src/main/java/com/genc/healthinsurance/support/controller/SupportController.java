package com.genc.healthinsurance.support.controller;
 
import java.time.LocalDate;
import java.util.List;

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
import com.genc.healthinsurance.support.entity.SupportTicket.TicketStatus;
import com.genc.healthinsurance.support.service.SupportService;

import jakarta.servlet.http.HttpSession;
 
@Controller
@RequestMapping("/support")
public class SupportController {
 
    @Autowired
    private SupportService supportService;
 
    // ---------------- Show all tickets for logged-in user (policyholder) ----------------
    @GetMapping
    public String showUserTickets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
 
        List<SupportTicket> tickets = supportService.getAllTicketsByUser(user.getUserId());
        model.addAttribute("tickets", tickets);
        model.addAttribute("user", user);
        model.addAttribute("ticket", new SupportTicket()); // For raise ticket form
        return "support/user-tickets";
    }
 
    // ---------------- Create new ticket ----------------
    @PostMapping("/create")
    public String createTicket(@ModelAttribute("newTicket") SupportTicket ticket,
                               HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:auth/login";
        }
 
        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.OPEN);
        ticket.setCreatedDate(LocalDate.now());
        supportService.createTicket(ticket);
 
        return "redirect:/support"; // reload tickets page
    }
 
    // ---------------- View single ticket details ----------------
    @GetMapping("/{ticketId}")
    public String viewTicket(@PathVariable Integer ticketId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:auth/login";
        }
 
        SupportTicket ticket = supportService.getTicketDetails(ticketId);
        model.addAttribute("ticket", ticket);
        model.addAttribute("user", user);
        return "support/view-ticket";
    }
 
    // ---------------- Resolve ticket (Admin/Agent/Adjuster) ----------------
    @PostMapping("/{ticketId}/resolve")
    public String resolveTicket(@PathVariable Integer ticketId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
 
        // Only non-policyholders can resolve
        if (!user.getRole().name().equalsIgnoreCase("POLICYHOLDER")) {
            supportService.resolveTicket(ticketId);
        }
 
        return "redirect:/support/admin"; // reload admin ticket page
    }
 
    // ---------------- Show all tickets for Admin/Agent/Adjuster ----------------
    @GetMapping("/admin")
    public String showAllTicketsForAdmin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
 
        // Only non-policyholders can access
        if (user.getRole().name().equalsIgnoreCase("POLICYHOLDER")) {
            return "redirect:/support";
        }
 
        List<SupportTicket> tickets = supportService.getAllTickets(); // All tickets
        model.addAttribute("tickets", tickets);
        model.addAttribute("user", user);
        return "support/admin-tickets";
    }
}
 