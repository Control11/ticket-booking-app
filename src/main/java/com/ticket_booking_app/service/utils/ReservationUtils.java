package com.ticket_booking_app.service.utils;

import com.ticket_booking_app.model.Screening;
import com.ticket_booking_app.model.ScreeningSeat;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.SeatStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReservationUtils {
    public void validateSeatLocation(List<Seat> seats, List<ScreeningSeat> screeningSeats) {
        Map<Seat, ScreeningSeat> seatWithScreeningSeat = new HashMap<>();
        screeningSeats.forEach(screeningSeat -> seatWithScreeningSeat.put(screeningSeat.getSeat(), screeningSeat));

        for (Seat seat : seats) {
            Integer seatNumber = seat.getNumber();
            Set<Integer> neighborsNumbers = Set.of(seatNumber + 1, seatNumber + 2, seatNumber - 1, seatNumber - 2);
            Map<Integer, SeatStatus> neighbors = getNeighborsStatus(screeningSeats, seat.getRow(), neighborsNumbers);

            SeatStatus firstSeatFromLeft = neighbors.getOrDefault(seatNumber - 1, SeatStatus.AVAILABLE);
            SeatStatus secondSeatFromLeft = neighbors.getOrDefault(seatNumber - 2, SeatStatus.AVAILABLE);
            SeatStatus firstSeatFromRight = neighbors.getOrDefault(seatNumber + 1, SeatStatus.AVAILABLE);
            SeatStatus secondSeatFromRight = neighbors.getOrDefault(seatNumber + 2, SeatStatus.AVAILABLE);

            if ((firstSeatFromLeft.equals(SeatStatus.AVAILABLE) && secondSeatFromLeft.equals(SeatStatus.RESERVED))
                    || (firstSeatFromRight.equals(SeatStatus.AVAILABLE) && secondSeatFromRight.equals(SeatStatus.RESERVED))) {
                throw new RuntimeException("There cannot be a single place left over in a row between two already reserved places: " + seat);
            }
        }
    }

    public void validateReservationTime(Screening screening, LocalDateTime now, Integer reservationTimeLimit) {
        if (now.toLocalDate().isEqual(screening.getDate())) {
            if (Duration.between(now.toLocalTime(), screening.getTime()).toMinutes() <= reservationTimeLimit) {
                throw new RuntimeException(String.format("Wrong reservation time - reservation time exceeds reservation time limit - limit: %d minutes, exceeding: %d minutes",
                        reservationTimeLimit, Duration.between(now.toLocalTime(), screening.getTime()).toMinutes()));
            }
        } else if (now.isAfter(LocalDateTime.of(screening.getDate(), screening.getTime()))) {
            throw new RuntimeException("Wrong reservation date - reservation date after screening date");
        }
    }

    public void changeSeatStatus(List<ScreeningSeat> screeningSeats, List<Seat> seats) {
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
    }

    private static Map<Integer, SeatStatus> getNeighborsStatus(List<ScreeningSeat> screeningSeats, String row, Set<Integer> neighborsNumbers) {
        return screeningSeats.stream()
                .filter(screeningSeat -> screeningSeat.getSeat().getRow().equals(row) && neighborsNumbers.contains(screeningSeat.getSeat().getNumber()))
                .collect(Collectors.toMap(screeningSeat -> screeningSeat.getSeat().getNumber(), ScreeningSeat::getStatus));
    }
}
