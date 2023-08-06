package com.ticket_booking_app.repository;

import com.ticket_booking_app.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    Seat findByNumberAndRow(Integer number, String row);
}

