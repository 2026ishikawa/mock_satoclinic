package com.example.satoclinic.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.satoclinic.web.form.ReservationForm;

import jakarta.validation.Valid;

@Controller
public class ReservationPageController {
    private static final int SLOT_CAPACITY = 3;
    // Temporary mock: reserved count by "yyyy-MM-dd|HH:mm". Replace with DB query later.
    private static final Map<String, Integer> RESERVED_COUNT_BY_SLOT = Map.of();
    // Temporary in-memory store for completion view. Replace with DB lookup later.
    private static final Map<String, ReservationForm> RESERVATION_SNAPSHOT_BY_CODE = new ConcurrentHashMap<>();

    @ModelAttribute("reservationForm")
    public ReservationForm reservationForm() {
        return new ReservationForm();
    }

    @GetMapping("/reservations/new")
    public String newPage(Model model) {
        addFormOptions(model);
        return "reservation-new";
    }

    @PostMapping("/reservations/new")
    public String backToInput(
            @ModelAttribute("reservationForm") ReservationForm form,
            Model model) {
        addFormOptions(model);
        return "reservation-new";
    }

    @PostMapping("/reservations/confirm")
    public String confirm(
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult bindingResult,
            Model model) {
        addFormOptions(model);

        composeBirthDate(form, bindingResult);

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
    public String register(@ModelAttribute("reservationForm") ReservationForm form) {
        // Registration persistence will be implemented in the next step.
        String reservationCode = "R" + LocalDate.now().toString().replace("-", "") + "0001";
        RESERVATION_SNAPSHOT_BY_CODE.put(reservationCode, form);
        return "redirect:/reservations/complete/" + reservationCode;
    }

    @GetMapping("/reservations/complete/{reservationCode}")
    public String complete(
            @PathVariable("reservationCode") String reservationCode,
            Model model) {
        ReservationForm form = RESERVATION_SNAPSHOT_BY_CODE.get(reservationCode);
        model.addAttribute("reservationCode", reservationCode);
        model.addAttribute("reservationDate", form != null ? form.getReservationDate() : null);
        model.addAttribute("reservationTime", form != null ? form.getReservationTime() : null);
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

    private void addFormOptions(Model model) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeOptions", defaultTimeOptions());
        model.addAttribute("birthYearOptions", IntStream.rangeClosed(1900, LocalDate.now().getYear()).boxed().toList());
        model.addAttribute("birthMonthOptions", IntStream.rangeClosed(1, 12).boxed().toList());
        model.addAttribute("birthDayOptions", IntStream.rangeClosed(1, 31).boxed().toList());
    }

    private void composeBirthDate(ReservationForm form, BindingResult bindingResult) {
        if (form.getBirthYear() == null || form.getBirthMonth() == null || form.getBirthDay() == null) {
            return;
        }
        try {
            LocalDate birthDate = LocalDate.of(form.getBirthYear(), form.getBirthMonth(), form.getBirthDay());
            if (!birthDate.isBefore(LocalDate.now())) {
                bindingResult.rejectValue("birthYear", "birthDate.future", "生年月日は過去日を入力してください。");
                return;
            }
            form.setBirthDate(birthDate);
        } catch (DateTimeException e) {
            bindingResult.rejectValue("birthYear", "birthDate.invalid", "生年月日が正しくありません。");
        }
    }
}
