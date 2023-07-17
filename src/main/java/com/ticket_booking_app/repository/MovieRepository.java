package com.ticket_booking_app.repository;

import com.ticket_booking_app.DTO.view.MovieRepertoireView;
import com.ticket_booking_app.DTO.view.MovieScreeningInfoView;
import com.ticket_booking_app.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<MovieRepertoireView> findByScreeningDateOrderByTitle(LocalDate date);
    List<MovieRepertoireView> findByScreeningDateAndScreeningTimeGreaterThanEqualOrderByTitle(LocalDate date, LocalTime time);
    MovieScreeningInfoView findByScreeningId(int screeningId);
}
