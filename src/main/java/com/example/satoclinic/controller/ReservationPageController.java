package com.example.satoclinic.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.satoclinic.model.reservation.ReservationDetail;
import com.example.satoclinic.service.ReservationService;
import com.example.satoclinic.service.ReservationSlotService;
import com.example.satoclinic.web.form.ReservationForm;

import jakarta.validation.Valid;

@Controller
public class ReservationPageController {

    private final ReservationSlotService reservationSlotService;
    private final ReservationService reservationService;

    public ReservationPageController(
            ReservationSlotService reservationSlotService,
            ReservationService reservationService) {
        this.reservationSlotService = reservationSlotService;
        this.reservationService = reservationService;
    }

    @ModelAttribute("reservationForm")
    public ReservationForm reservationForm() {
        return new ReservationForm();
    }

    @GetMapping("/reservations/new")
    public String newPage(Model model) {
        addFormOptions(model, null);
        return "reservation-new";
    }

    @PostMapping("/reservations/new")
    public String backToInput(
            @ModelAttribute("reservationForm") ReservationForm form,
            Model model) {
        addFormOptions(model, form.getReservationDate());
        return "reservation-new";
    }

    @PostMapping("/reservations/confirm")
    public String confirm(
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult bindingResult,
            Model model) {
        addFormOptions(model, form.getReservationDate());

        composeBirthDate(form, bindingResult);

        if (form.getReservationDate() != null && form.getReservationDate().isBefore(LocalDate.now())) {
            bindingResult.rejectValue("reservationDate", "reservationDate.past", "過去日は予約できません。");
        }
        if (form.getReservationDate() != null) {
            String restrictionMessage = reservationSlotService.getReservationRestrictionMessage(form.getReservationDate());
            if (restrictionMessage != null && form.getReservationDate().getDayOfWeek().getValue() != 6) {
                bindingResult.rejectValue("reservationDate", "reservationDate.closed", restrictionMessage);
            }
        }
        if (form.getReservationDate() != null
                && form.getReservationTime() != null
                && !form.getReservationTime().isBlank()
                && !reservationSlotService.isSlotAvailable(form.getReservationDate(), form.getReservationTime())) {
            bindingResult.rejectValue("reservationTime", "reservationTime.full", "この時間帯は予約できません。");
        }

        if (bindingResult.hasErrors()) {
            return "reservation-new";
        }
        model.addAttribute("reservationForm", form);
        return "reservation-confirm";
    }

    @PostMapping("/reservations")
    public String register(@ModelAttribute("reservationForm") ReservationForm form, Model model) {
        try {
            String reservationCode = reservationService.register(form);
            return "redirect:/reservations/complete/" + reservationCode;
        } catch (IllegalStateException e) {
            model.addAttribute("registrationError", e.getMessage());
            model.addAttribute("reservationForm", form);
            addFormOptions(model, form.getReservationDate());
            return "reservation-confirm";
        }
    }

    @GetMapping("/reservations/complete/{reservationCode}")
    public String complete(
            @PathVariable("reservationCode") String reservationCode,
            Model model) {
        ReservationDetail detail = reservationService.findDetailByReservationCode(reservationCode);
        model.addAttribute("reservationCode", reservationCode);
        model.addAttribute("reservationDate", detail != null ? detail.getReservationDate() : null);
        model.addAttribute("reservationTime", detail != null ? detail.getReservationTime() : null);
        return "reservation-complete";
    }

    private void addFormOptions(Model model, LocalDate selectedDate) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("timeOptions", reservationSlotService.findAvailableTimeOptions(selectedDate));
        model.addAttribute("reservationRestrictionMessage", reservationSlotService.getReservationRestrictionMessage(selectedDate));
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
