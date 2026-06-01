package com.example.satoclinic.controller;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping("/reservations/confirm")
    public String confirm(
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeOptions", defaultTimeOptions());
        if (bindingResult.hasErrors()) {
            return "reservation-new";
        }
        model.addAttribute("reservationForm", form);
        return "reservation-confirm";
    }

    private List<String> defaultTimeOptions() {
        return List.of("09:00", "09:30", "10:00", "10:30", "11:00", "11:30");
    }
}
