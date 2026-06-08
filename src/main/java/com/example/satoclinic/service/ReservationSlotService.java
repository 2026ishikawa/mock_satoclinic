package com.example.satoclinic.service;

import java.time.DayOfWeek;
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

    public List<String> findAvailableTimeOptions(LocalDate date) {
        if (date == null) {
            return List.of();
        }
        return findAvailabilityByDate(date).stream()
                .filter(ReservationSlotAvailability::isAvailable)
                .map(slot -> formatTime(slot.getStartTime()))
                .toList();
    }

    public boolean isSlotAvailable(LocalDate date, String time) {
        return findAvailabilityByDate(date).stream()
                .anyMatch(slot -> slot.isAvailable() && formatTime(slot.getStartTime()).equals(time));
    }

    public void validateReservationDateTime(LocalDate date, LocalTime startTime) {
        if (date == null || startTime == null) {
            return;
        }
        switch (date.getDayOfWeek()) {
            case THURSDAY -> throw new IllegalStateException("木曜日は休診日のため予約できません。");
            case SUNDAY -> throw new IllegalStateException("日曜日は休診日のため予約できません。");
            case SATURDAY -> {
                if (!startTime.isBefore(LocalTime.of(14, 0))) {
                    throw new IllegalStateException("土曜日の午後は予約できません。");
                }
            }
            default -> {
            }
        }
    }

    public String getReservationRestrictionMessage(LocalDate date) {
        if (date == null) {
            return null;
        }
        if (date.getDayOfWeek() == DayOfWeek.THURSDAY) {
            return "木曜日は休診日のため予約できません。";
        }
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return "日曜日は休診日のため予約できません。";
        }
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return "土曜日は午前のみ予約できます。";
        }
        return null;
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
