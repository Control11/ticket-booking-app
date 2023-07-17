package com.ticket_booking_app.service;

import com.ticket_booking_app.DTO.view.MovieRepertoireView;
import com.ticket_booking_app.DTO.view.MovieScreeningInfoView;
import com.ticket_booking_app.DTO.ReservationRequestGuestDTO;
import com.ticket_booking_app.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IBooking {
    MovieScreeningInfoView getMovieScreeningInfo(int screeningId);
    List<MovieRepertoireView> getMoviesByDate(LocalDate date);
    List<MovieRepertoireView> getMoviesByDateTime(LocalDate date, LocalTime time);

    Reservation createReservation(ReservationRequestGuestDTO reservationRequestGuestDTO);
}
