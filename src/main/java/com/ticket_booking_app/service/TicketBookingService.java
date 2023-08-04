package com.ticket_booking_app.service;

import com.ticket_booking_app.DTO.ReservationRequestGuestDTO;
import com.ticket_booking_app.DTO.ReservationRespondDTO;
import com.ticket_booking_app.DTO.view.MovieRepertoireView;
import com.ticket_booking_app.DTO.view.MovieScreeningInfoView;
import com.ticket_booking_app.model.*;
import com.ticket_booking_app.model.utils.SeatStatus;
import com.ticket_booking_app.model.utils.TicketType;
import com.ticket_booking_app.repository.*;
import com.ticket_booking_app.validator.SeatLocationValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Getter
@Setter
@AllArgsConstructor
public class TicketBookingService implements IBooking {

    private MovieRepository movieRepository;
    private ReservationRepository reservationRepository;
    private ScreeningRepository screeningRepository;
    private CustomerRepository customerRepository;
    private TicketRepository ticketRepository;
    private SeatRepository seatRepository;

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
    public void validateReservationTime(ReservationRequestGuestDTO reservationRequestGuestDTO, LocalDateTime now) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        int reservationTimeLimit = 15;

        if (now.toLocalDate().isEqual(screening.getDate())) {
            if (Duration.between(now.toLocalTime(), screening.getTime()).toMinutes() <= reservationTimeLimit) {
                throw new RuntimeException(String.format("Wrong reservation time - reservation time exceeds reservation time limit - limit: %d minutes, exceeding: %d minutes",
                        reservationTimeLimit, Duration.between(now.toLocalTime(), screening.getTime()).toMinutes()));
            }
        } else if (now.isAfter(LocalDateTime.of(screening.getDate(), screening.getTime()))) {
            throw new RuntimeException("Wrong reservation date - reservation date after screening date");
        }
    }

    @Override
    public void validateSeatLocation(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();

        SeatLocationValidator.validateSeatLocation(reservationRequestGuestDTO.getSeats(), screening.getScreeningSeat());
    }


    @Override
    public void changeSeatStatus(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        List<Seat> seats = reservationRequestGuestDTO.getSeats();
        List<ScreeningSeat> screeningSeats = screening.getScreeningSeat();
        seats.forEach(seat -> seat.setRow(seat.getRow().toUpperCase(Locale.ROOT).trim()));

        Map<Seat, ScreeningSeat> seatWithScreeningSeat = new HashMap<>();
        screeningSeats.forEach(screeningSeat -> seatWithScreeningSeat.put(screeningSeat.getSeat(), screeningSeat));

        for (Seat seat : seats) {
            ScreeningSeat screeningSeatToReserve = seatWithScreeningSeat.get(seat);
            if (screeningSeatToReserve.getStatus().equals(SeatStatus.AVAILABLE)) {
                screeningSeatToReserve.setStatus(SeatStatus.RESERVED);
            } else {
                throw new RuntimeException("This seat is already reserved/bought: " + seat);
            }
        }

        screeningRepository.save(screening);
    }

    @Override
    public ReservationRespondDTO createReservation(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        int reservationTimeMinutes = 15;

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
        reservation.setExpirationDate(LocalDateTime.now().plus(reservationTimeMinutes, ChronoUnit.MINUTES));

        Reservation reservationDone = reservationRepository.save(reservation);
        BigDecimal priceToPay = tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        ReservationRespondDTO rdto = new ReservationRespondDTO(
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

//        reservationRepository.delete(reservation);

        return rdto;
    }

}
