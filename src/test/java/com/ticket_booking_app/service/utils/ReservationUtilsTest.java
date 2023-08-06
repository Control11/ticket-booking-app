package com.ticket_booking_app.service.utils;

import com.ticket_booking_app.model.Movie;
import com.ticket_booking_app.model.Screening;
import com.ticket_booking_app.model.ScreeningSeat;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.SeatStatus;
import com.ticket_booking_app.service.utils.ReservationUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationUtilsTest {
    private final ReservationUtils reservationUtils = new ReservationUtils();

    //Reservation time tests
    @Test
    void shouldNotAllowToBookWhenReservationTimeExceedsTimeLimit() {
        int reservationTimeLimit = 15;
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalTime reservationTime = screeningTime.minusMinutes(reservationTimeLimit);
        LocalDateTime reservationDateTime = LocalDateTime.of(screeningDate, reservationTime);
        Screening screening = new Screening(1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime);

        assertThatThrownBy(() -> reservationUtils.validateReservationTime(screening, reservationDateTime, reservationTimeLimit))
                .hasMessageStartingWith("Wrong reservation time - reservation time exceeds reservation time limit - ");
    }

    @Test
    void shouldNotAllowToBookAfterDateOfScreening() {
        int reservationTimeLimit = 15;
        int extraMinutes = 5;
        int dayDelay = 1;
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalDate reservationDate = screeningDate.plusDays(dayDelay);
        LocalTime reservationTime = screeningTime.minusMinutes(reservationTimeLimit + extraMinutes);
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);
        Screening screening = new Screening(1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime);

        assertThatThrownBy(() -> reservationUtils.validateReservationTime(screening, reservationDateTime, reservationTimeLimit))
                .hasMessageStartingWith("Wrong reservation date - reservation date after screening date");
    }

    @Test
    void shouldAllowToBookWhenReservationDateEqualsScreeningDateAndReservationTimeDoesNotExceedTimeLimit() {
        int reservationTimeLimit = 15;
        int extraMinutes = 5;
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalTime reservationTime = screeningTime.minusMinutes(reservationTimeLimit + extraMinutes);
        LocalDateTime reservationDateTime = LocalDateTime.of(screeningDate, reservationTime);
        Screening screening = new Screening(1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime);

        assertDoesNotThrow(() -> reservationUtils.validateReservationTime(screening, reservationDateTime, reservationTimeLimit));
    }

    @Test
    void shouldAllowToBookWhenReservationDateIsBeforeScreeningDate() {
        int reservationTimeLimit = 15;
        int extraMinutes = 5;
        int extraDay = 1;
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalTime reservationTime = screeningTime.minusMinutes(reservationTimeLimit + extraMinutes);
        LocalDate reservationDate = screeningDate.minusDays(extraDay);
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);
        Screening screening = new Screening(1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime);

        assertDoesNotThrow(() -> reservationUtils.validateReservationTime(screening, reservationDateTime, reservationTimeLimit));
    }


    //Reservation seats tests
    @Test
    void shouldAllowToBookWhenThereIsNotOnlyOneSeatAvailableBetweenTwoReservedSeats() {
        Screening screening = new Screening();
        String row = "A";

        List<Seat> seats = List.of(new Seat(3, row, 3), new Seat(6, row, 6));

        List<Seat> seatsDB = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seatsDB.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeats = List.of(
                new ScreeningSeat(1, screening, seatsDB.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(1), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seatsDB.get(2), SeatStatus.AVAILABLE), //SeatStatus.RESERVED
                new ScreeningSeat(1, screening, seatsDB.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(5), SeatStatus.AVAILABLE) //SeatStatus.RESERVED
        );

        assertDoesNotThrow(() -> reservationUtils.validateSeatLocation(seats, screeningSeats));
    }

    @Test
    void shouldNotAllowToBookWhenThereIsOnlyOneAvailableSeatBetweenTwoReservedSeats() {
        Screening screening = new Screening();
        String row = "A";

        List<Seat> seats = List.of(new Seat(3, row, 3), new Seat(6, row, 6));

        List<Seat> seatsDB = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seatsDB.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeats = List.of(
                new ScreeningSeat(1, screening, seatsDB.get(0), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seatsDB.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(2), SeatStatus.AVAILABLE), //SeatStatus.RESERVED
                new ScreeningSeat(1, screening, seatsDB.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(4), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seatsDB.get(5), SeatStatus.AVAILABLE) //SeatStatus.RESERVED
        );

        assertThatThrownBy(() -> reservationUtils.validateSeatLocation(seats, screeningSeats))
                .hasMessageContaining("There cannot be a single place left over in a row between two already reserved places: ");
    }

    @Test
    void shouldChangeAllSeatStatusForReservation() {
        Screening screening = new Screening();
        String row = "A";

        List<Seat> seats = List.of(new Seat(3, row, 3), new Seat(6, row, 6));

        List<Seat> seatsDB = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seatsDB.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeatsDB = List.of(
                new ScreeningSeat(1, screening, seatsDB.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(2), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(5), SeatStatus.AVAILABLE)
        );

        List<ScreeningSeat> screeningSeatsExpected = List.of(
                new ScreeningSeat(1, screening, seatsDB.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(2), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seatsDB.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(5), SeatStatus.RESERVED)
        );

        Screening screeningDB = new Screening(1, new Movie(), screeningSeatsDB, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0)
        );


        Screening screeningExpected = new Screening(1, new Movie(), screeningSeatsExpected, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0)
        );

        assertDoesNotThrow(() -> reservationUtils.changeSeatStatus(screeningSeatsDB, seats));
        assertEquals(screeningExpected, screeningDB);
    }

    @Test
    void shouldNotAllowToChangeSeatStatusWhenSeatIsAlreadyReservedOrBought() {
        Screening screening = new Screening();
        String row = "A";

        List<Seat> seats = List.of(new Seat(3, row, 3), new Seat(6, row, 6));

        List<Seat> seatsDB = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seatsDB.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeatsDB = List.of(
                new ScreeningSeat(1, screening, seatsDB.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(2), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seatsDB.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seatsDB.get(5), SeatStatus.BOUGHT)
        );

        assertThatThrownBy(() -> reservationUtils.changeSeatStatus(screeningSeatsDB, seats))
                .hasMessageContaining("This seat is already reserved/bought: ");
    }


}