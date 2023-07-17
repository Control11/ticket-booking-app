package com.ticket_booking_app.DTO;

import com.ticket_booking_app.model.Customer;
import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.utils.TicketType;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ReservationRequestGuestDTO {
    private Integer screeningId;
    private Customer customer;
    private List<TicketType> ticketTypes;
    private List<Seat> seats;
    private Instant expirationDateTime;
}
