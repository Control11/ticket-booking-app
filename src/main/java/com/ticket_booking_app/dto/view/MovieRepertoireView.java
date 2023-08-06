package com.ticket_booking_app.dto.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MovieRepertoireView {
    int getId();
    String getTitle();

    @JsonProperty("screenings")
    List<ScreeningDateTimeView> getScreening();
    interface ScreeningDateTimeView {
        int getId();
        LocalDate getDate();
        LocalTime getTime();
    }

}