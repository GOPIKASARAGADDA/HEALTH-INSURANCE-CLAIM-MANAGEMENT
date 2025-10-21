package com.genc.healthinsurance.support.entity;
 
import java.time.LocalDate;
import com.genc.healthinsurance.auth.entity.User;
import jakarta.persistence.*;
 
@Entity
@Table(name = "Support")
public class SupportTicket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ticketId;
 
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
 
    @Column(columnDefinition = "TEXT")
    private String issueDescription;
 
    public enum TicketStatus {
        OPEN, RESOLVED
    }
    
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;
 
    private LocalDate createdDate;
 
    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
 
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
 
    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
 
    public TicketStatus getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(TicketStatus ticketStatus) { this.ticketStatus = ticketStatus; }
 
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
 

}
 