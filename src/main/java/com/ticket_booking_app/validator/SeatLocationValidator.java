package com.ticket_booking_app.validator;

import com.ticket_booking_app.model.ScreeningSeat;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.SeatStatus;

import java.util.*;
import java.util.stream.Collectors;

public class SeatLocationValidator {
    public static void validateSeatLocation(List<Seat> seats, List<ScreeningSeat> screeningSeats) {
        Map<Seat, ScreeningSeat> seatWithScreeningSeat = new HashMap<>();
        screeningSeats.forEach(screeningSeat -> seatWithScreeningSeat.put(screeningSeat.getSeat(), screeningSeat));

        seats.forEach(seat -> seatWithScreeningSeat.get(seat).setStatus(SeatStatus.RESERVED));

        for (Seat seat : seats) {
            Integer seatNumber = seat.getNumber();
            Map<Integer, SeatStatus> neighbors = getNeighbors(seat, screeningSeats);

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

    private static Map<Integer, SeatStatus> getNeighbors(Seat seat, List<ScreeningSeat> screeningSeats) {
        String row = seat.getRow();
        List<Integer> neighbors = List.of(seat.getNumber() + 1, seat.getNumber() + 2, seat.getNumber() - 1, seat.getNumber() - 2);

        return screeningSeats.stream()
                .filter(screeningSeat -> screeningSeat.getSeat().getRow().equals(row) && neighbors.contains(screeningSeat.getSeat().getNumber()))
                .collect(Collectors.toMap(screeningSeat -> screeningSeat.getSeat().getNumber(), ScreeningSeat::getStatus));
    }
}
