package com.genc.healthinsurance.support.service;
 
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.support.entity.SupportTicket;
import com.genc.healthinsurance.support.entity.SupportTicket.TicketStatus;
import com.genc.healthinsurance.support.repository.SupportTicketRepository;
 
@Service
public class SupportService {
 
    @Autowired
    private SupportTicketRepository ticketRepository;
 
    // ---------------- Create a new ticket ----------------
    public SupportTicket createTicket(SupportTicket ticket, User user) {
        ticket.setUser(user);
        ticket.setTicketStatus(TicketStatus.OPEN);
        ticket.setCreatedDate(LocalDate.now());
        return ticketRepository.save(ticket);
    }
 
    // ---------------- Get ticket details by ID ----------------
    public SupportTicket getTicketDetails(Integer ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
    }
 
    // ---------------- Resolve a ticket ----------------
    public SupportTicket resolveTicket(Integer ticketId, User user) {
        if (user.getRole().name().equalsIgnoreCase("POLICYHOLDER")) {
            throw new RuntimeException("Policyholders cannot resolve tickets.");
        }
        SupportTicket ticket = getTicketDetails(ticketId);
        ticket.setTicketStatus(TicketStatus.RESOLVED);
        return ticketRepository.save(ticket);
    }
 
    // ---------------- Get all tickets of a specific user ----------------
    public List<SupportTicket> getAllTicketsByUser(Integer userId) {
        return ticketRepository.findByUserUserId(userId);
    }
 
    // ---------------- Get all tickets (for admin/agent/adjuster) ----------------
    public List<SupportTicket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
 