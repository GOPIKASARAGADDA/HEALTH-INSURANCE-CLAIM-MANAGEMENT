package com.genc.healthinsurancetest.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.genc.healthinsurance.auth.entity.Role;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.support.entity.SupportTicket;
import com.genc.healthinsurance.support.entity.SupportTicket.TicketStatus;
import com.genc.healthinsurance.support.repository.SupportTicketRepository;
import com.genc.healthinsurance.support.service.SupportService;
 
public class SupportServiceTest {
 
    @Mock private SupportTicketRepository ticketRepository;
 
    @InjectMocks private SupportService supportService;
 
    private SupportTicket ticket;
    private User adminUser;
    private User policyHolderUser;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        // Test ticket
        ticket = new SupportTicket();
        ticket.setTicketId(1);
        ticket.setIssueDescription("Test issue");
 
        // Admin user
        adminUser = new User();
        adminUser.setUserId(100);
        adminUser.setRole(Role.ADMIN);
 
        // Policyholder user
        policyHolderUser = new User();
        policyHolderUser.setUserId(200);
        policyHolderUser.setRole(Role.POLICYHOLDER);
    }
 
    @Test
    void testCreateTicket() {
        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        SupportTicket saved = supportService.createTicket(ticket, adminUser);
 
        assertNotNull(saved);
        assertEquals(TicketStatus.OPEN, saved.getTicketStatus());
        assertEquals(adminUser, saved.getUser());
        assertNotNull(saved.getCreatedDate());
        verify(ticketRepository, times(1)).save(ticket);
    }
 
    @Test
    void testGetTicketDetails_Found() {
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
 
        SupportTicket t = supportService.getTicketDetails(1);
 
        assertNotNull(t);
        assertEquals(1, t.getTicketId());
    }
 
    @Test
    void testGetTicketDetails_NotFound() {
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> supportService.getTicketDetails(1));
        assertEquals("Ticket not found with ID: 1", ex.getMessage());
    }
 
    @Test
    void testResolveTicket_Success() {
        ticket.setTicketStatus(TicketStatus.OPEN);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        SupportTicket resolved = supportService.resolveTicket(1, adminUser);
 
        assertEquals(TicketStatus.RESOLVED, resolved.getTicketStatus());
        verify(ticketRepository, times(1)).save(ticket);
    }
 
    @Test
    void testResolveTicket_Unauthorized() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> supportService.resolveTicket(1, policyHolderUser));
        assertEquals("Policyholders cannot resolve tickets.", ex.getMessage());
    }
 
    @Test
    void testGetAllTicketsByUser() {
        when(ticketRepository.findByUserUserId(100)).thenReturn(List.of(ticket));
 
        List<SupportTicket> tickets = supportService.getAllTicketsByUser(100);
 
        assertEquals(1, tickets.size());
        assertTrue(tickets.contains(ticket));
    }
 
    @Test
    void testGetAllTickets() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
 
        List<SupportTicket> tickets = supportService.getAllTickets();
 
        assertEquals(1, tickets.size());
        assertTrue(tickets.contains(ticket));
    }
}
 