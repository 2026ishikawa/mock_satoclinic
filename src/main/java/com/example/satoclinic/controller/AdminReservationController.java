package com.example.satoclinic.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.satoclinic.model.reservation.AdminReservationSummary;
import com.example.satoclinic.model.reservation.ReservationDetail;
import com.example.satoclinic.service.AdminReservationService;

@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        List<AdminReservationSummary> reservations = adminReservationService.findReservations(date, status);
        model.addAttribute("reservations", reservations);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusOptions", List.of("RESERVED", "CANCELLED", "VISITED"));
        return "admin-reservations";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        ReservationDetail reservation = adminReservationService.findDetail(id);
        model.addAttribute("reservation", reservation);
        return "admin-reservation-detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean cancelled = adminReservationService.cancel(id);
        redirectAttributes.addFlashAttribute(
                "message",
                cancelled ? "予約をキャンセルしました。" : "この予約はキャンセルできません。");
        return "redirect:/admin/reservations/" + id;
    }
}
