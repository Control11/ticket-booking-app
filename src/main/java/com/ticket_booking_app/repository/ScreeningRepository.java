package com.ticket_booking_app.repository;

import com.ticket_booking_app.model.Screening;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Integer> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Screening> findById(int id);

}
