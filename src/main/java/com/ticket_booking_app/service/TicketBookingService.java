package com.ticket_booking_app.service;

import com.ticket_booking_app.dto.ReservationRequestGuestDTO;
import com.ticket_booking_app.dto.ReservationRespondDTO;
import com.ticket_booking_app.dto.view.MovieRepertoireView;
import com.ticket_booking_app.dto.view.MovieScreeningInfoView;
import com.ticket_booking_app.model.*;
import com.ticket_booking_app.repository.*;
import com.ticket_booking_app.validator.ReservationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
public class TicketBookingService {

    private MovieRepository movieRepository;
    private ReservationRepository reservationRepository;
    private ScreeningRepository screeningRepository;
    private CustomerRepository customerRepository;
    private TicketRepository ticketRepository;
    private SeatRepository seatRepository;
    private ReservationUtils reservationUtils;

    public List<MovieRepertoireView> getMoviesByDate(LocalDate date) {
        return movieRepository.findByScreeningDateOrderByTitle(date);
    }

    public List<MovieRepertoireView> getMoviesByDateTime(LocalDate date, LocalTime time) {
        return movieRepository.findByScreeningDateAndScreeningTimeGreaterThanEqualOrderByTitle(date, time);
    }

    public MovieScreeningInfoView getMovieScreeningInfo(int screeningId) {
        return movieRepository.findByScreeningId(screeningId);
    }

    public ReservationRespondDTO createReservation(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        int reservationExpirationTime = 15;
        int reservationTimeLimit = 15;

        reservationUtils.validateReservationTime(screening, LocalDateTime.now(), reservationTimeLimit);
        reservationUtils.validateSeatLocation(reservationRequestGuestDTO.getSeats(), screening.getScreeningSeat());
        reservationUtils.changeSeatStatus(screening.getScreeningSeat(), reservationRequestGuestDTO.getSeats());

        screeningRepository.save(screening);

        List<Ticket> tickets = reservationRequestGuestDTO.getTicketTypes()
                .stream()
                .map(ticketType -> ticketRepository.findByType(ticketType))
                .collect(Collectors.toList());

        List<Seat> seats = reservationRequestGuestDTO.getSeats()
                .stream()
                .map(seat -> seatRepository.findByNumberAndRow(seat.getNumber(), seat.getRow()))
                .collect(Collectors.toList());

        Customer customer = customerRepository.save(reservationRequestGuestDTO.getCustomer());

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setScreening(screening);
        reservation.setSeat(seats);
        reservation.setTicket(tickets);
        reservation.setExpirationDate(LocalDateTime.now().plusMinutes(reservationExpirationTime));

        Reservation reservationDone = reservationRepository.save(reservation);
        BigDecimal priceToPay = tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReservationRespondDTO(
                reservationDone.getId(),
                reservationDone.getCustomer().getName(),
                reservationDone.getCustomer().getSurname(),
                reservationDone.getScreening().getRoomNumber(),
                reservationDone.getScreening().getMovie().getTitle(),
                reservationDone.getSeat(),
                reservationDone.getTicket(),
                reservationDone.getScreening().getDate(),
                reservationDone.getScreening().getTime(),
                reservationDone.getExpirationDate(),
                priceToPay);
    }

}
