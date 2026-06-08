package com.example.satoclinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.satoclinic.mapper.ReservationMapper;
import com.example.satoclinic.mapper.ReservationSlotMapper;
import com.example.satoclinic.model.reservation.Reservation;
import com.example.satoclinic.model.reservation.ReservationDetail;
import com.example.satoclinic.model.reservation.ReservationSlot;
import com.example.satoclinic.web.form.ReservationForm;

@Service
public class ReservationService {

    private static final DateTimeFormatter RESERVATION_CODE_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final ReservationMapper reservationMapper;
    private final ReservationSlotMapper reservationSlotMapper;
    private final ReservationSlotService reservationSlotService;

    public ReservationService(
            ReservationMapper reservationMapper,
            ReservationSlotMapper reservationSlotMapper,
            ReservationSlotService reservationSlotService) {
        this.reservationMapper = reservationMapper;
        this.reservationSlotMapper = reservationSlotMapper;
        this.reservationSlotService = reservationSlotService;
    }

    @Transactional
    public String register(ReservationForm form) {
        LocalTime startTime = LocalTime.parse(form.getReservationTime());
        reservationSlotService.validateReservationDateTime(form.getReservationDate(), startTime);
        ReservationSlot reservationSlot = reservationSlotMapper.lockActiveSlotByDateAndStartTime(
                form.getReservationDate(), startTime);

        if (reservationSlot == null) {
            throw new IllegalStateException("選択した時間帯は予約できません。");
        }

        int reservedCount = reservationMapper.countReservedBySlotId(reservationSlot.getId());
        if (reservedCount >= reservationSlot.getCapacity()) {
            throw new IllegalStateException("この時間帯は満員です。別の時間を選択してください。");
        }

        String normalizedPatientName = normalizeName(form.getPatientName());
        String normalizedPatientKana = normalizeName(form.getPatientKana());
        String normalizedPhoneNumber = normalizePhoneNumber(form.getPhoneNumber());

        int duplicateCount = reservationMapper.countDuplicateReservedReservation(
                reservationSlot.getId(),
                normalizedPatientName,
                normalizedPatientKana,
                form.getBirthDate(),
                normalizedPhoneNumber);
        if (duplicateCount > 0) {
            throw new IllegalStateException("同じ時間帯に同一患者の予約がすでに入っています。");
        }

        String reservationCode = generateReservationCode(reservationSlot.getSlotDate());

        Reservation reservation = new Reservation();
        reservation.setReservationCode(reservationCode);
        reservation.setReservationSlotId(reservationSlot.getId());
        reservation.setPatientName(normalizedPatientName);
        reservation.setPatientKana(normalizedPatientKana);
        reservation.setBirthDate(form.getBirthDate());
        reservation.setPhoneNumber(normalizedPhoneNumber);
        reservation.setEmail(form.getEmail());
        reservation.setVisitType(form.getVisitType());
        reservation.setSymptom(form.getSymptom());
        reservation.setStatus("RESERVED");
        reservation.setAgreedToPrivacyPolicy(form.isAgreedToPrivacyPolicy());

        reservationMapper.insert(reservation);
        return reservationCode;
    }

    public ReservationDetail findDetailByReservationCode(String reservationCode) {
        return reservationMapper.findDetailByReservationCode(reservationCode);
    }

    private String generateReservationCode(LocalDate reservationDate) {
        int nextSequence = reservationMapper.findMaxSequenceBySlotDate(reservationDate) + 1;
        return "R" + reservationDate.format(RESERVATION_CODE_DATE) + String.format("%04d", nextSequence);
    }

    private String normalizeName(String value) {
        return value == null ? null : value.trim().replace(" ", "").replace("　", "");
    }

    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber == null
                ? null
                : phoneNumber.trim()
                        .replace("-", "")
                        .replace(" ", "")
                        .replace("　", "")
                        .replace("(", "")
                        .replace(")", "");
    }
}
