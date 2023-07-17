package com.ticket_booking_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Entity
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Seat> Seat;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ticket> ticket;

    private Instant expirationDate;

}
