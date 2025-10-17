package com.genc.healthinsurance.support.service;
 
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genc.healthinsurance.support.entity.SupportTicket;
import com.genc.healthinsurance.support.entity.SupportTicket.TicketStatus;
import com.genc.healthinsurance.support.repository.SupportTicketRepository;
 
@Service
public class SupportService {
 
    @Autowired
    private SupportTicketRepository ticketRepository;
 
    // ---------------- Create a new ticket ----------------
    public SupportTicket createTicket(SupportTicket ticket) {
        return ticketRepository.save(ticket);
    }
 
    // ---------------- Get ticket details by ID ----------------
    public SupportTicket getTicketDetails(Integer ticketId) {
        Optional<SupportTicket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            return ticketOpt.get();
        } else {
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }
 
    // ---------------- Resolve a ticket ----------------
    public SupportTicket resolveTicket(Integer ticketId) {
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
 