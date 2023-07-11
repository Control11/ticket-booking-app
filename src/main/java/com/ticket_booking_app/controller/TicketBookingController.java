package com.ticket_booking_app.controller;

import com.ticket_booking_app.model.Movie;
import com.ticket_booking_app.model.Reservation;
import com.ticket_booking_app.model.ReservationRequest;
import com.ticket_booking_app.model.Screening;
import com.ticket_booking_app.service.IBooking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class TicketBookingController {
    private IBooking ticketBookingService;

    public TicketBookingController(@Qualifier("ticketBookingService") final IBooking ticketBookingService) {
        this.ticketBookingService = ticketBookingService;
    }

    @GetMapping("/movies")
    public List<Movie> getMoviesByDateTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDateTime date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime time) {
        return ticketBookingService.getMoviesByDateTime(date, time);
    }

    @GetMapping("/booking/info/{movieId}/{screeningId}")
    public Screening getScreeningInfoForMovie(@PathVariable final int movieId, @PathVariable final int screeningId) {
        return ticketBookingService.getScreeningInfo(movieId, screeningId);
    }

    @PostMapping("/booking/reservation")
    public Reservation createReservation(@RequestBody ReservationRequest reservationRequest) {
        return ticketBookingService.createReservation(reservationRequest);
    }

}