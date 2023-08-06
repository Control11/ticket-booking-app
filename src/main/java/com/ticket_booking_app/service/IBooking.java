package com.ticket_booking_app.service;

import com.ticket_booking_app.dto.ReservationRequestGuestDTO;
import com.ticket_booking_app.dto.ReservationRespondDTO;
import com.ticket_booking_app.dto.view.MovieRepertoireView;
import com.ticket_booking_app.dto.view.MovieScreeningInfoView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IBooking {
    List<MovieRepertoireView> getMoviesByDate(LocalDate date);

    List<MovieRepertoireView> getMoviesByDateTime(LocalDate date, LocalTime time);

    MovieScreeningInfoView getMovieScreeningInfo(int screeningId);

    ReservationRespondDTO createReservation(ReservationRequestGuestDTO reservationRequestGuestDTO);

    void validateReservationTime(ReservationRequestGuestDTO reservationRequestGuestDTO, LocalDateTime now);

    void validateSeatLocation(ReservationRequestGuestDTO reservationRequestGuestDTO);

    void changeSeatStatus(ReservationRequestGuestDTO reservationRequestGuestDTO);

}
