package com.ticket_booking_app.service;

import com.ticket_booking_app.DTO.ReservationRequestGuestDTO;
import com.ticket_booking_app.model.Movie;
import com.ticket_booking_app.model.Screening;
import com.ticket_booking_app.model.ScreeningSeat;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.SeatStatus;
import com.ticket_booking_app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketBookingServiceTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TicketRepository ticketRepository;
    @InjectMocks
    private TicketBookingService ticketBookingService;
    private ReservationRequestGuestDTO reservationRequestGuestDTO;

    @BeforeEach
    void setUp() {
        reservationRequestGuestDTO = new ReservationRequestGuestDTO();
        reservationRequestGuestDTO.setScreeningId(1);
    }


    //Reservation time tests
    @Test
    void shouldNotAllowsToBookAfterTimeLimit() {
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalTime reservationTime = screeningTime.minus(15, ChronoUnit.MINUTES);
        LocalDateTime reservationDateTime = LocalDateTime.of(screeningDate, reservationTime);


        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening(
                1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime)));

        assertThatThrownBy(() -> ticketBookingService.validateReservationTime(reservationRequestGuestDTO, reservationDateTime))
                .hasMessageStartingWith("Wrong reservation time - reservation time exceeds reservation time limit - ");
    }

    @Test
    void shouldNotAllowsToBookAfterDateOfScreening() {
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalDate reservationDate = screeningDate.plus(1, ChronoUnit.DAYS);
        LocalTime reservationTime = screeningTime.minus(20, ChronoUnit.MINUTES);
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);

        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening(
                1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime)));

        assertThatThrownBy(() -> ticketBookingService.validateReservationTime(reservationRequestGuestDTO, reservationDateTime))
                .hasMessageStartingWith("Wrong reservation date - reservation date after screening date");
    }

    @Test
    void shouldValidateReservationTimeWhenReservationDateEqualsScreeningDateAndReservationTimeDoesNotExceedTimeLimit() {
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalTime reservationTime = screeningTime.minus(20, ChronoUnit.MINUTES);
        LocalDateTime reservationDateTime = LocalDateTime.of(screeningDate, reservationTime);

        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening(
                1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime)));

        assertDoesNotThrow(() -> ticketBookingService.validateReservationTime(reservationRequestGuestDTO, reservationDateTime));
    }

    @Test
    void shouldValidateReservationTimeWhenReservationDateIsBeforeScreeningDate() {
        LocalTime screeningTime = LocalTime.of(12, 0, 0);
        LocalDate screeningDate = LocalDate.of(2023, 1, 1);
        LocalTime reservationTime = screeningTime.minus(20, ChronoUnit.MINUTES);
        LocalDate reservationDate = screeningDate.minus(1, ChronoUnit.DAYS);
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);

        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening(
                1, new Movie(), new ArrayList<>(), 1, screeningDate, screeningTime)));

        assertDoesNotThrow(() -> ticketBookingService.validateReservationTime(reservationRequestGuestDTO, reservationDateTime));
    }


    //Reservation seats tests
    @Test
    void shouldValidateIfThereIsNoSeatBetweenTwoReservedSeats() {
        Screening screening = new Screening();
        String row = "A";

        reservationRequestGuestDTO.setSeats(List.of(new Seat(3, row, 3), new Seat(6, row, 6)));

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seats.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeats = List.of(
                new ScreeningSeat(1, screening, seats.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(1), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seats.get(2), SeatStatus.AVAILABLE), //SeatStatus.RESERVED
                new ScreeningSeat(1, screening, seats.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(5), SeatStatus.AVAILABLE) //SeatStatus.RESERVED
        );


        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening(
                1, new Movie(), screeningSeats, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0))));

        assertDoesNotThrow(() -> ticketBookingService.validateSeatLocation(reservationRequestGuestDTO));
    }

    @Test
    void shouldNotValidateWhenThereIsAvailableSeatBetweenTwoReservedSeats() {
        Screening screening = new Screening();
        String row = "A";

        reservationRequestGuestDTO.setSeats(List.of(new Seat(3, row, 3), new Seat(6, row, 6)));

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seats.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeats = List.of(
                new ScreeningSeat(1, screening, seats.get(0), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seats.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(2), SeatStatus.AVAILABLE), //SeatStatus.RESERVED
                new ScreeningSeat(1, screening, seats.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(4), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seats.get(5), SeatStatus.AVAILABLE) //SeatStatus.RESERVED
        );


        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening(
                1, new Movie(), screeningSeats, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0))));

        assertThatThrownBy(() -> ticketBookingService.validateSeatLocation(reservationRequestGuestDTO))
                .hasMessageContaining("There cannot be a single place left over in a row between two already reserved places: ");
    }

    //Creating reservation tests
    @Test
    void shouldChangeAllSeatStatusForReservation() {
        Screening screening = new Screening();
        String row = "A";

        reservationRequestGuestDTO.setSeats(List.of(new Seat(3, row, 3), new Seat(6, row, 6)));

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seats.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeatsDB = List.of(
                new ScreeningSeat(1, screening, seats.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(2), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(5), SeatStatus.AVAILABLE)
        );

        List<ScreeningSeat> screeningSeatsExpected = List.of(
                new ScreeningSeat(1, screening, seats.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(2), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seats.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(5), SeatStatus.RESERVED)
        );


        Screening screeningDB = new Screening(1, new Movie(), screeningSeatsDB, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0));


        Screening screeningExpected = new Screening(1, new Movie(), screeningSeatsExpected, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0));


        when(screeningRepository.findById(any())).thenReturn(Optional.of(screeningDB));
        when(screeningRepository.save(any(Screening.class))).thenReturn(screeningDB);
        Screening screeningAfterChange = ticketBookingService.changeSeatStatus(reservationRequestGuestDTO);

        assertEquals(screeningExpected, screeningAfterChange);
    }

    @Test
    void shouldNotValidateChangingSeatStatusWhenSeatsAreAlreadyReservedOrBought() {
        Screening screening = new Screening();
        String row = "A";

        reservationRequestGuestDTO.setSeats(List.of(new Seat(3, row, 3), new Seat(6, row, 6)));

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            seats.add(new Seat(i + 1, row, i + 1));
        }

        List<ScreeningSeat> screeningSeatsDB = List.of(
                new ScreeningSeat(1, screening, seats.get(0), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(1), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(2), SeatStatus.RESERVED),
                new ScreeningSeat(1, screening, seats.get(3), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(4), SeatStatus.AVAILABLE),
                new ScreeningSeat(1, screening, seats.get(5), SeatStatus.BOUGHT)
        );

        Screening screeningDB = new Screening(1, new Movie(), screeningSeatsDB, 1,
                LocalDate.of(2023, 1, 1), LocalTime.of(12, 0, 0));

        when(screeningRepository.findById(any())).thenReturn(Optional.of(screeningDB));

        assertThatThrownBy(() -> ticketBookingService.changeSeatStatus(reservationRequestGuestDTO))
                .hasMessageContaining("This seat is already reserved/bought: ");
    }


}