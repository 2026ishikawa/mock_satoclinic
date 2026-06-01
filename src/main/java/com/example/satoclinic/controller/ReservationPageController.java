package com.example.satoclinic.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.satoclinic.web.form.ReservationForm;

import jakarta.validation.Valid;

@Controller
public class ReservationPageController {
    private static final int SLOT_CAPACITY = 3;
    // Temporary mock: reserved count by "yyyy-MM-dd|HH:mm". Replace with DB query later.
    private static final Map<String, Integer> RESERVED_COUNT_BY_SLOT = Map.of();

    @ModelAttribute("reservationForm")
    public ReservationForm reservationForm() {
        return new ReservationForm();
    }

    @GetMapping("/reservations/new")
    public String newPage(Model model) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeOptions", defaultTimeOptions());
        return "reservation-new";
    }

    @PostMapping("/reservations/new")
    public String backToInput(
            @ModelAttribute("reservationForm") ReservationForm form,
            Model model) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeOptions", defaultTimeOptions());
        return "reservation-new";
    }

    @PostMapping("/reservations/confirm")
    public String confirm(
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeOptions", defaultTimeOptions());

        if (form.getReservationDate() != null && form.getReservationDate().isBefore(LocalDate.now())) {
            bindingResult.rejectValue("reservationDate", "reservationDate.past", "過去日は予約できません。");
        }
        if (form.getReservationDate() != null
                && form.getReservationTime() != null
                && !form.getReservationTime().isBlank()
                && !isSlotAvailable(form.getReservationDate(), form.getReservationTime())) {
            bindingResult.rejectValue("reservationTime", "reservationTime.full", "この時間帯は予約枠が上限です。");
        }

        if (bindingResult.hasErrors()) {
            return "reservation-new";
        }
        model.addAttribute("reservationForm", form);
        return "reservation-confirm";
    }

    @PostMapping("/reservations")
    public String register(
            @ModelAttribute("reservationForm") ReservationForm form,
            Model model) {
        // Registration persistence will be implemented in the next step.
        model.addAttribute("reservationCode", "R" + LocalDate.now().toString().replace("-", "") + "0001");
        model.addAttribute("reservationForm", form);
        return "reservation-complete";
    }

    private List<String> defaultTimeOptions() {
        return List.of("09:00", "09:30", "10:00", "10:30", "11:00", "11:30");
    }

    private boolean isSlotAvailable(LocalDate date, String time) {
        String key = date + "|" + time;
        int reservedCount = RESERVED_COUNT_BY_SLOT.getOrDefault(key, 0);
        return reservedCount < SLOT_CAPACITY;
    }
}
