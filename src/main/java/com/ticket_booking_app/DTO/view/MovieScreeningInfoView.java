package com.ticket_booking_app.DTO.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ticket_booking_app.model.utils.SeatStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MovieScreeningInfoView {
    int getId();
    String getTitle();

    @JsonProperty("screeningInfo")
    ScreeningView getScreening();

    interface ScreeningView {
        int getId();
        int getRoomNumber();
        LocalDate getDate();
        LocalTime getTime();
        List<ScreeningSeatView> getScreeningSeat();
    }

    interface ScreeningSeatView {
        int getId();
        SeatView getSeat();
        SeatStatus getStatus();
    }

    interface SeatView {
        String getRow();
        int getNumber();
    }

}
