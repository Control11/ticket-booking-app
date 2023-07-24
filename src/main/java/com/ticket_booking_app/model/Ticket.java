package com.ticket_booking_app.model;

import com.ticket_booking_app.model.utils.TicketType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TicketType type;
    private BigDecimal price;

}
