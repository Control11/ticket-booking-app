package com.ticket_booking_app.dto;

import com.ticket_booking_app.model.Customer;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReservationRequestGuestDTO {
    @NotNull
    private Integer screeningId;

    @NotNull
    @Valid
    private Customer customer;

    @NotEmpty(message = "Reservation should have at least one ticket type selected")
    private List<TicketType> ticketTypes;

    @NotEmpty(message = "Reservation should have at least one seat selected")
    @Valid
    private List<Seat> seats;

    @AssertTrue(message = "Amount of tickets should be the same as amount of seats")
    public boolean isSeatsValid() {
     return seats.size() == ticketTypes.size();
    }
}
