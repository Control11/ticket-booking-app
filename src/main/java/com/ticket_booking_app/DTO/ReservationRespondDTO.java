package com.ticket_booking_app.DTO;

import com.ticket_booking_app.model.Seat;
import com.ticket_booking_app.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReservationRespondDTO {
    private int id;
    private int roomNumber;
    private String movieTitle;
    private List<Seat> seats;
    private List<Ticket> tickets;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime expirationDate;
    private BigDecimal priceToPay;
}
