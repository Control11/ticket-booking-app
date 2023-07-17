package com.ticket_booking_app.repository;

import com.ticket_booking_app.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
}
