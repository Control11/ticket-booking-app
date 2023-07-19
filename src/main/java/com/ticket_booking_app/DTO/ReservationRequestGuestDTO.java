package com.ticket_booking_app.DTO;

import com.ticket_booking_app.model.Customer;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ReservationRequestGuestDTO {
    @NotNull
    private Integer screeningId;

    @NotNull
    @Valid
    private Customer customer;

    @NotNull
    private List<TicketType> ticketTypes;

    @NotEmpty(message = "Reservation should have at least one seat selected")
    private List<Seat> seats;

    @NotNull
    private Instant expirationDateTime;
}
