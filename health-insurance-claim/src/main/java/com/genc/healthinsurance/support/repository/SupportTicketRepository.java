package com.genc.healthinsurance.support.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.genc.healthinsurance.auth.entity.User;
import com.genc.healthinsurance.support.entity.SupportTicket;
 
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUser(User user);
    List<SupportTicket> findByUserUserId(Integer userId);
}