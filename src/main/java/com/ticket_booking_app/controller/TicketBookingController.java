package com.ticket_booking_app.controller;

import com.ticket_booking_app.DTO.ReservationRequestGuestDTO;
import com.ticket_booking_app.DTO.ReservationRespondDTO;
import com.ticket_booking_app.DTO.view.MovieRepertoireView;
import com.ticket_booking_app.DTO.view.MovieScreeningInfoView;
import com.ticket_booking_app.service.IBooking;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class TicketBookingController {
    private final IBooking ticketBookingService;

    public TicketBookingController(@Qualifier("ticketBookingService") final IBooking ticketBookingService) {
        this.ticketBookingService = ticketBookingService;
    }

    @GetMapping("/movies/{date}")
    public List<MovieRepertoireView> getMoviesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {
        return ticketBookingService.getMoviesByDate(date);
    }

    @GetMapping("/movies/{date}/{time}")
    public List<MovieRepertoireView> getMoviesByDateTime(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) final LocalTime time) {
        return ticketBookingService.getMoviesByDateTime(date, time);
    }

    @GetMapping("/booking/info/{screeningId}")
    public MovieScreeningInfoView getScreening(@PathVariable final int screeningId) {
        return ticketBookingService.getMovieScreeningInfo(screeningId);
    }

    @PostMapping("/booking/reservation/guest")
    @ResponseBody
    @Transactional
    public ReservationRespondDTO createReservation(@Valid @RequestBody final ReservationRequestGuestDTO reservationRequestGuestDTO) {
        ticketBookingService.validateReservationTime(reservationRequestGuestDTO, LocalDateTime.now());
        ticketBookingService.validateSeatLocation(reservationRequestGuestDTO);
        ticketBookingService.changeSeatStatus(reservationRequestGuestDTO);

        return ticketBookingService.createReservation(reservationRequestGuestDTO);
    }

}