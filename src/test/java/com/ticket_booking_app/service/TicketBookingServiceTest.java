package com.ticket_booking_app.service;

import com.ticket_booking_app.dto.ReservationRequestGuestDTO;
import com.ticket_booking_app.model.Screening;
import com.ticket_booking_app.repository.ScreeningRepository;
import com.ticket_booking_app.validator.ReservationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketBookingServiceTest {
    @Mock
    private ReservationUtils reservationUtils;
    @Mock
    private ScreeningRepository screeningRepository;
    @InjectMocks
    private TicketBookingService ticketBookingService;
    private final ReservationRequestGuestDTO reservationRequestGuestDTO = new ReservationRequestGuestDTO();

    //    Creating reservation tests
    @Test
    void shouldNotAllowToCreateReservationWhenReservationTimeIsNotValid() {
        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening()));
        doThrow(new RuntimeException("Wrong reservation date - reservation date after screening date"))
                .when(reservationUtils).validateReservationTime(any(), any(), any());

        assertThatThrownBy(() -> ticketBookingService.createReservation(reservationRequestGuestDTO))
                .hasMessageContaining("Wrong reservation date - reservation date after screening date");

    }

    @Test
    void shouldNotAllowToCreateReservationWhenSeatLocationIsNotValid() {
        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening()));
        doNothing().when(reservationUtils).validateReservationTime(any(), any(), any());
        doThrow(new RuntimeException("There cannot be a single place left over in a row between two already reserved places: "))
                .when(reservationUtils).validateSeatLocation(any(), any());

        assertThatThrownBy(() -> ticketBookingService.createReservation(reservationRequestGuestDTO))
                .hasMessageContaining("There cannot be a single place left over in a row between two already reserved places: ");

    }

    @Test
    void shouldNotAllowToCreateReservationWhenSeatsAreAlreadyReservedOrBought() {
        when(screeningRepository.findById(any())).thenReturn(Optional.of(new Screening()));
        doNothing().when(reservationUtils).validateReservationTime(any(), any(), any());
        doNothing().when(reservationUtils).validateSeatLocation(any(), any());
        doThrow(new RuntimeException("This seat is already reserved/bought: "))
                .when(reservationUtils).changeSeatStatus(any(), any());

        assertThatThrownBy(() -> ticketBookingService.createReservation(reservationRequestGuestDTO))
                .hasMessageContaining("This seat is already reserved/bought: ");

    }

}