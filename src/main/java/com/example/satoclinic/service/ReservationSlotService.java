package com.example.satoclinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.satoclinic.mapper.ReservationSlotMapper;
import com.example.satoclinic.model.reservation.ReservationSlotAvailability;

@Service
public class ReservationSlotService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservationSlotMapper reservationSlotMapper;

    public ReservationSlotService(ReservationSlotMapper reservationSlotMapper) {
        this.reservationSlotMapper = reservationSlotMapper;
    }

    public List<ReservationSlotAvailability> findAvailabilityByDate(LocalDate date) {
        return reservationSlotMapper.findAvailabilityByDate(date);
    }

    public boolean isSlotAvailable(LocalDate date, String time) {
        return findAvailabilityByDate(date).stream()
                .anyMatch(slot -> slot.isAvailable() && formatTime(slot.getStartTime()).equals(time));
    }

    public List<String> findTimeOptions() {
        return reservationSlotMapper.findDistinctStartTimes().stream()
                .map(this::formatTime)
                .toList();
    }

    private String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }
}
