package com.example.satoclinic.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.satoclinic.service.ReservationSlotService;

@RestController
@RequestMapping("/api/reservation-slots")
public class ReservationSlotApiController {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservationSlotService reservationSlotService;

    public ReservationSlotApiController(ReservationSlotService reservationSlotService) {
        this.reservationSlotService = reservationSlotService;
    }

    @GetMapping
    public ReservationSlotsResponse slots(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<SlotResponse> slots = reservationSlotService.findAvailabilityByDate(date).stream()
                .map(slot -> new SlotResponse(
                        slot.getStartTime().format(TIME_FORMATTER),
                        slot.isAvailable(),
                        slot.getRemaining()))
                .toList();
        return new ReservationSlotsResponse(date.toString(), slots);
    }

    public record ReservationSlotsResponse(String date, List<SlotResponse> slots) {
    }

    public record SlotResponse(String time, boolean available, int remaining) {
    }
}
