package com.ticket_booking_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @OrderBy("time ASC")
    private List<Screening> screening;

}
