package com.ticket_booking_app.repository;

import com.ticket_booking_app.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket findByType(String type);
}
