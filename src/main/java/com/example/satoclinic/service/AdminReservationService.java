package com.example.satoclinic.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.satoclinic.mapper.ReservationMapper;
import com.example.satoclinic.model.reservation.AdminReservationSummary;
import com.example.satoclinic.model.reservation.ReservationDetail;

@Service
public class AdminReservationService {

    private final ReservationMapper reservationMapper;

    public AdminReservationService(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    public List<AdminReservationSummary> findReservations(
            LocalDate date,
            String status,
            String patientName,
            String reservationCode,
            String phoneNumber,
            boolean includeDeleted) {
        return reservationMapper.findAdminSummaries(
                date,
                status,
                normalize(patientName),
                normalize(reservationCode),
                normalizePhone(phoneNumber),
                includeDeleted);
    }

    public ReservationDetail findDetail(Long id, boolean includeDeleted) {
        return reservationMapper.findDetailById(id, includeDeleted);
    }

    @Transactional
    public boolean cancel(Long id, String cancelReason) {
        return reservationMapper.cancelById(id, cancelReason) > 0;
    }

    @Transactional
    public boolean markVisited(Long id) {
        return reservationMapper.markVisitedById(id) > 0;
    }

    @Transactional
    public boolean restoreReserved(Long id) {
        return reservationMapper.restoreReservedById(id) > 0;
    }

    @Transactional
    public boolean softDelete(Long id, String deleteReason, String deletedBy) {
        String normalizedReason = normalize(deleteReason);
        String normalizedDeletedBy = normalize(deletedBy);
        if (normalizedReason == null || normalizedDeletedBy == null) {
            return false;
        }
        return reservationMapper.softDeleteById(id, normalizedReason, normalizedDeletedBy) > 0;
    }

    @Transactional
    public boolean restoreDeleted(Long id) {
        return reservationMapper.restoreDeletedById(id) > 0;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizePhone(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        return normalized
                .replace("-", "")
                .replace(" ", "")
                .replace("　", "")
                .replace("(", "")
                .replace(")", "");
    }
}
