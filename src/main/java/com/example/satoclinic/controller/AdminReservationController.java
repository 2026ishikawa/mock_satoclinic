package com.example.satoclinic.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<String, String> CANCEL_REASON_OPTIONS = createCancelReasonOptions();

    private final AdminReservationService adminReservationService;

    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "patientName", required = false) String patientName,
            @RequestParam(value = "reservationCode", required = false) String reservationCode,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            Model model) {
        List<AdminReservationSummary> reservations = adminReservationService.findReservations(
                date,
                status,
                patientName,
                reservationCode,
                phoneNumber);
        model.addAttribute("reservations", reservations);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPatientName", patientName);
        model.addAttribute("selectedReservationCode", reservationCode);
        model.addAttribute("selectedPhoneNumber", phoneNumber);
        model.addAttribute("statusOptions", List.of("RESERVED", "CANCELLED", "VISITED"));
        return "admin-reservations";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        ReservationDetail reservation = adminReservationService.findDetail(id);
        model.addAttribute("reservation", reservation);
        model.addAttribute("cancelReasonOptions", CANCEL_REASON_OPTIONS);
        return "admin-reservation-detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(
            @PathVariable("id") Long id,
            @RequestParam("cancelReason") String cancelReason,
            RedirectAttributes redirectAttributes) {
        boolean cancelled = adminReservationService.cancel(id, cancelReason);
        redirectAttributes.addFlashAttribute("message", cancelled ? "予約をキャンセルしました。" : "予約を更新できませんでした。");
        return "redirect:/admin/reservations/" + id;
    }

    @PostMapping("/{id}/visit")
    public String markVisited(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean visited = adminReservationService.markVisited(id);
        redirectAttributes.addFlashAttribute("message", visited ? "来院済みに変更しました。" : "予約を更新できませんでした。");
        return "redirect:/admin/reservations/" + id;
    }

    @PostMapping("/{id}/restore")
    public String restoreReserved(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean restored = adminReservationService.restoreReserved(id);
        redirectAttributes.addFlashAttribute("message", restored ? "予約済みに戻しました。" : "予約を更新できませんでした。");
        return "redirect:/admin/reservations/" + id;
    }

    private static Map<String, String> createCancelReasonOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("PATIENT_REQUEST", "患者都合");
        options.put("CLINIC_REASON", "クリニック都合");
        options.put("DUPLICATE", "重複予約");
        options.put("OTHER", "その他");
        return options;
    }
}
