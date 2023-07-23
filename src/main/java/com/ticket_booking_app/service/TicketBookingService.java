package com.ticket_booking_app.service;

import com.ticket_booking_app.DTO.ReservationRequestGuestDTO;
import com.ticket_booking_app.DTO.ReservationRespondDTO;
import com.ticket_booking_app.DTO.view.MovieRepertoireView;
import com.ticket_booking_app.DTO.view.MovieScreeningInfoView;
import com.ticket_booking_app.model.*;
import com.ticket_booking_app.model.utils.SeatStatus;
import com.ticket_booking_app.model.utils.TicketType;
import com.ticket_booking_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
        List<Seat> seats = reservationRequestGuestDTO.getSeats();
        Set<String> rowsInReservation = new HashSet<>();
        seats.forEach(seat -> rowsInReservation.add(seat.getRow()));

        for (String row : rowsInReservation) {
            Set<Seat> seatInRow = seats.stream()
                    .filter(seat -> seat.getRow().equals(row))
                    .collect(Collectors.toSet());

            HashMap<Integer, SeatStatus> seatNumberAndStatusInRow = new HashMap<>();
            screening.getScreeningSeat().stream()
                    .filter(screeningSeat -> screeningSeat.getSeat().getRow().equals(row))
                    .forEach(screeningSeat -> seatNumberAndStatusInRow.put(
                            screeningSeat.getSeat().getNumber(), screeningSeat.getStatus()
                    ));

            seatInRow.forEach(seat -> seatNumberAndStatusInRow.replace(seat.getNumber(), SeatStatus.AVAILABLE, SeatStatus.RESERVED));

            for (Seat seat : seatInRow) {
                int seatNumber = seat.getNumber();

                seatNumberAndStatusInRow.forEach((key, value) -> {
                    if (key == seatNumber) {
                        SeatStatus firstSeatFromLeft = seatNumberAndStatusInRow.getOrDefault(seatNumber - 1, SeatStatus.AVAILABLE);
                        SeatStatus secondSeatFromLeft = seatNumberAndStatusInRow.getOrDefault(seatNumber - 2, SeatStatus.AVAILABLE);
                        SeatStatus firstSeatFromRight = seatNumberAndStatusInRow.getOrDefault(seatNumber + 1, SeatStatus.AVAILABLE);
                        SeatStatus secondSeatFromRight = seatNumberAndStatusInRow.getOrDefault(seatNumber + 2, SeatStatus.AVAILABLE);

                        if ((firstSeatFromLeft.equals(SeatStatus.AVAILABLE) && secondSeatFromLeft.equals(SeatStatus.RESERVED))
                                || (firstSeatFromRight.equals(SeatStatus.AVAILABLE) && secondSeatFromRight.equals(SeatStatus.RESERVED))) {
                            throw new RuntimeException("There cannot be a single place left over in a row between two already reserved places: " + seat);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void changeSeatStatus(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        List<Seat> seats = reservationRequestGuestDTO.getSeats();
        List<ScreeningSeat> screeningSeats = screening.getScreeningSeat();

        seats.forEach(seat -> seat.setRow(seat.getRow().toUpperCase(Locale.ROOT).trim()));

        for (Seat seat : seats) {
            screeningSeats.stream()
                    .filter(screeningSeat -> seat.equals(screeningSeat.getSeat()))
                    .forEach(screeningSeat -> {
                        if (screeningSeat.getStatus().equals(SeatStatus.AVAILABLE)) {
                            screeningSeat.setStatus(SeatStatus.RESERVED);
                        } else {
                            throw new RuntimeException("This seat is already reserved/bought: " + screeningSeat.getSeat());
                        }
                    });
        }

        screeningRepository.save(screening);
    }

    @Override
    public ReservationRespondDTO createReservation(ReservationRequestGuestDTO reservationRequestGuestDTO) {
        Screening screening = screeningRepository.findById(reservationRequestGuestDTO.getScreeningId()).orElseThrow();
        List<Seat> seats = reservationRequestGuestDTO.getSeats();
        int reservationTimeMinutes = 15;

        List<Ticket> tickets = new ArrayList<>();
        for (TicketType ticketType : reservationRequestGuestDTO.getTicketTypes()) {
            tickets.add(ticketRepository.findByType(ticketType.name()));
        }

        Customer customer = customerRepository.save(reservationRequestGuestDTO.getCustomer());

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setScreening(screening);
        reservation.setSeat(seats);
        reservation.setTicket(tickets);
        reservation.setExpirationDate(LocalDateTime.now().plus(reservationTimeMinutes, ChronoUnit.MINUTES));

        Reservation reservationDone = reservationRepository.save(reservation);
        BigDecimal priceToPay = tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReservationRespondDTO(
                reservationDone.getId(),
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
