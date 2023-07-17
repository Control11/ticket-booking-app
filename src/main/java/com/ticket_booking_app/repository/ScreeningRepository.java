package com.ticket_booking_app.repository;

import com.ticket_booking_app.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningRepository extends JpaRepository<Screening, Integer> {
}
