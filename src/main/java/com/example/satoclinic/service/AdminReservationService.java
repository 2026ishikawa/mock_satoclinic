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

    public List<AdminReservationSummary> findReservations(LocalDate date, String status) {
        return reservationMapper.findAdminSummaries(date, status);
    }

    public ReservationDetail findDetail(Long id) {
        return reservationMapper.findDetailById(id);
    }

    @Transactional
    public boolean cancel(Long id) {
        return reservationMapper.cancelById(id) > 0;
    }
}
