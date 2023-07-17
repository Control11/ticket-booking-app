package com.ticket_booking_app.service;

import com.ticket_booking_app.DTO.ReservationRequestGuestDTO;
import com.ticket_booking_app.DTO.view.MovieRepertoireView;
import com.ticket_booking_app.DTO.view.MovieScreeningInfoView;
import com.ticket_booking_app.model.*;
import com.ticket_booking_app.repository.*;
import com.ticket_booking_app.model.utils.SeatStatus;
import com.ticket_booking_app.model.utils.TicketType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Getter
@Setter
@AllArgsConstructor
@Transactional
public class TicketBookingService implements IBooking {

    private MovieRepository movieRepository;
    private ReservationRepository reservationRepository;
    private ScreeningRepository screeningRepository;
    private CustomerRepository customerRepository;
    private TicketRepository ticketRepository;

    @Override
    public List<MovieRepertoireView> getMoviesByDate(LocalDate date) {
        return movieRepository.findByScreeningDateOrderByTitle(date);
    }

    @Override
    public List<MovieRepertoireView> getMoviesByDateTime(LocalDate date, LocalTime time) {
        return movieRepository.findByScreeningDateAndScreeningTimeGreaterThanEqualOrderByTitle(date, time);
    }

    @Override
    public MovieScreeningInfoView getMovieScreeningInfo(int screeningId) {
        return movieRepository.findByScreeningId(screeningId);
    }

    @Override
    public Reservation createReservation(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        List<Seat> seats = reservationRequestGuestDTO.getSeats();
        int reservationTimeMinutes = 15;

        List<Ticket> tickets = new ArrayList<>();
        for (TicketType ticketType : reservationRequestGuestDTO.getTicketTypes()) {
            tickets.add(ticketRepository.findByType(ticketType.name()));
        }

        seatStatusOperation(screening.getScreeningSeat(), seats);

        Customer customer = customerRepository.save(reservationRequestGuestDTO.getCustomer());
        Screening screeningUpdated = screeningRepository.save(screening);

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setScreening(screeningUpdated);
        reservation.setSeat(seats);
        reservation.setTicket(tickets);
        reservation.setExpirationDate(Instant.now().plus(reservationTimeMinutes, ChronoUnit.MINUTES));

        return reservationRepository.save(reservation);
    }

    private static void seatStatusOperation(List<ScreeningSeat> screeningSeats, List<Seat> seats) {
        seats.forEach(seat -> seat.setRow(seat.getRow().toUpperCase(Locale.ROOT).trim()));

        for (Seat seat : seats) {
            for (ScreeningSeat screeningSeat : screeningSeats) {
                if (seat.equals(screeningSeat.getSeat())) {
                    if (SeatStatus.AVAILABLE.toString().equalsIgnoreCase(screeningSeat.getStatus())) {
                        screeningSeat.setStatus(SeatStatus.RESERVED.name());
                    } else {
                        throw new RuntimeException("This seat is already reserved/bought: " + screeningSeat.getSeat());
                    }
                    break;
                }
            }
        }
    }
}
