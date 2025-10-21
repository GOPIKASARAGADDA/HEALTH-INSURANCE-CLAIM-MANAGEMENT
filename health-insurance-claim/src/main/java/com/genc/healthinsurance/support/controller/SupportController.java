package com.genc.healthinsurance.support.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
 
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.support.entity.SupportTicket;
import com.genc.healthinsurance.support.service.SupportService;
 
import jakarta.servlet.http.HttpSession;
 
@Controller
@RequestMapping("/support")
public class SupportController {
 
    @Autowired
    private SupportService supportService;
 
    // ---------------- Show all tickets for logged-in user ----------------
    @GetMapping
    public String showUserTickets(HttpSession session, Model model) {
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
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
 
        supportService.createTicket(ticket, user);
        return "redirect:/support";
    }
 
    // ---------------- View single ticket details ----------------
    @GetMapping("/{ticketId}")
    public String viewTicket(@PathVariable Integer ticketId, Model model, HttpSession session) {
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
 
        List<SupportTicket> tickets = supportService.getAllTickets();
        model.addAttribute("tickets", tickets);
        model.addAttribute("user", user);
        return "support/admin-tickets";
    }
}
 