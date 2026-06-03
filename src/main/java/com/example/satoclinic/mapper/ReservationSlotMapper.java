package com.example.satoclinic.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.satoclinic.model.reservation.ReservationSlot;
import com.example.satoclinic.model.reservation.ReservationSlotAvailability;

@Mapper
public interface ReservationSlotMapper {

    @Select("""
            SELECT
              rs.id AS slot_id,
              rs.slot_date AS date,
              rs.start_time,
              rs.end_time,
              rs.capacity - COUNT(r.id) AS remaining,
              CASE WHEN rs.capacity - COUNT(r.id) > 0 THEN TRUE ELSE FALSE END AS available
            FROM reservation_slots rs
            LEFT JOIN reservations r
              ON rs.id = r.reservation_slot_id
              AND r.status = 'RESERVED'
            WHERE rs.slot_date = #{date}
              AND rs.is_active = TRUE
            GROUP BY
              rs.id,
              rs.slot_date,
              rs.start_time,
              rs.end_time,
              rs.capacity
            ORDER BY rs.start_time
            """)
    List<ReservationSlotAvailability> findAvailabilityByDate(@Param("date") LocalDate date);

    @Select("""
            SELECT
              id,
              slot_date,
              start_time,
              end_time,
              capacity,
              is_active,
              created_at,
              updated_at
            FROM reservation_slots
            WHERE slot_date = #{date}
              AND start_time = #{startTime}
              AND is_active = TRUE
            """)
    ReservationSlot findActiveSlotByDateAndStartTime(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime);

    @Select("""
            SELECT
              id,
              slot_date,
              start_time,
              end_time,
              capacity,
              is_active,
              created_at,
              updated_at
            FROM reservation_slots
            WHERE slot_date = #{date}
              AND start_time = #{startTime}
              AND is_active = TRUE
            FOR UPDATE
            """)
    ReservationSlot lockActiveSlotByDateAndStartTime(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime);

    @Select("""
            SELECT DISTINCT start_time
            FROM reservation_slots
            WHERE is_active = TRUE
            ORDER BY start_time
            """)
    List<LocalTime> findDistinctStartTimes();
}
