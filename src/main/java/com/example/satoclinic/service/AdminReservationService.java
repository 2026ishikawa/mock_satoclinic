package com.example.satoclinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.satoclinic.mapper.ReservationMapper;
import com.example.satoclinic.mapper.ReservationSlotMapper;
import com.example.satoclinic.model.reservation.AdminReservationSummary;
import com.example.satoclinic.model.reservation.Reservation;
import com.example.satoclinic.model.reservation.ReservationDetail;
import com.example.satoclinic.model.reservation.ReservationSlot;

@Service
public class AdminReservationService {

    private final ReservationMapper reservationMapper;
    private final ReservationSlotMapper reservationSlotMapper;
    private final ReservationSlotService reservationSlotService;

    public AdminReservationService(
            ReservationMapper reservationMapper,
            ReservationSlotMapper reservationSlotMapper,
            ReservationSlotService reservationSlotService) {
        this.reservationMapper = reservationMapper;
        this.reservationSlotMapper = reservationSlotMapper;
        this.reservationSlotService = reservationSlotService;
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

    @Transactional
    public void reschedule(Long id, LocalDate reservationDate, String reservationTime) {
        if (reservationDate == null || reservationTime == null || reservationTime.isBlank()) {
            throw new IllegalStateException("予約日と予約時間を選択してください。");
        }

        LocalTime startTime = LocalTime.parse(reservationTime);
        reservationSlotService.validateReservationDateTime(reservationDate, startTime);

        Reservation currentReservation = reservationMapper.lockById(id);
        if (currentReservation == null) {
            throw new IllegalStateException("対象の予約データが見つかりません。");
        }
        if (currentReservation.getDeletedAt() != null) {
            throw new IllegalStateException("削除済みの予約データは変更できません。");
        }
        if (!"RESERVED".equals(currentReservation.getStatus())) {
            throw new IllegalStateException("予約変更できるのは予約済みデータのみです。");
        }

        ReservationSlot targetSlot = reservationSlotMapper.lockActiveSlotByDateAndStartTime(reservationDate, startTime);
        if (targetSlot == null) {
            throw new IllegalStateException("変更先の予約枠が存在しません。");
        }

        if (!targetSlot.getId().equals(currentReservation.getReservationSlotId())) {
            int reservedCount = reservationMapper.countReservedBySlotIdExcludingReservation(targetSlot.getId(), id);
            if (reservedCount >= targetSlot.getCapacity()) {
                throw new IllegalStateException("変更先の予約枠に空きがありません。");
            }
        }

        String normalizedPatientName = normalizeName(currentReservation.getPatientName());
        String normalizedPatientKana = normalizeName(currentReservation.getPatientKana());
        String normalizedPhoneNumber = normalizePhone(currentReservation.getPhoneNumber());
        int duplicateCount = reservationMapper.countDuplicateReservedReservationExcludingReservation(
                targetSlot.getId(),
                id,
                normalizedPatientName,
                normalizedPatientKana,
                currentReservation.getBirthDate(),
                normalizedPhoneNumber);
        if (duplicateCount > 0) {
            throw new IllegalStateException("変更先の予約枠に同一患者の重複予約があります。");
        }

        int updated = reservationMapper.updateReservationSlotById(id, targetSlot.getId());
        if (updated == 0) {
            throw new IllegalStateException("予約変更を確定できませんでした。");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeName(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        return normalized.replace(" ", "").replace("　", "");
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
