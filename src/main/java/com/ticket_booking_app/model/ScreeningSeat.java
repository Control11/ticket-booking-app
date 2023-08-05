package com.ticket_booking_app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ticket_booking_app.model.utils.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    @JsonBackReference
    private Screening screening;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

}
