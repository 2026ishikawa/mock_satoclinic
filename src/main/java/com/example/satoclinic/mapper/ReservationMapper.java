package com.example.satoclinic.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.satoclinic.model.reservation.AdminReservationSummary;
import com.example.satoclinic.model.reservation.Reservation;
import com.example.satoclinic.model.reservation.ReservationDetail;

@Mapper
public interface ReservationMapper {

    @Select("""
            <script>
            SELECT
              r.id,
              r.reservation_code,
              r.patient_name,
              rs.slot_date AS reservation_date,
              rs.start_time AS reservation_time,
              r.visit_type,
              r.status
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            <where>
              <if test="date != null">
                rs.slot_date = #{date}
              </if>
              <if test="status != null and status != ''">
                AND r.status = #{status}
              </if>
            </where>
            ORDER BY rs.slot_date, rs.start_time, r.id
            </script>
            """)
    java.util.List<AdminReservationSummary> findAdminSummaries(
            @Param("date") LocalDate date,
            @Param("status") String status);

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(r.reservation_code, 10, 4) AS INTEGER)), 0)
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            WHERE rs.slot_date = #{slotDate}
            """)
    int findMaxSequenceBySlotDate(@Param("slotDate") LocalDate slotDate);

    @Select("""
            SELECT COUNT(*)
            FROM reservations
            WHERE reservation_slot_id = #{reservationSlotId}
              AND status = 'RESERVED'
            """)
    int countReservedBySlotId(@Param("reservationSlotId") Long reservationSlotId);

    @Select("""
            SELECT COUNT(*)
            FROM reservations
            WHERE reservation_slot_id = #{reservationSlotId}
              AND REPLACE(REPLACE(TRIM(patient_name), ' ', ''), '　', '') = #{patientName}
              AND REPLACE(REPLACE(TRIM(patient_kana), ' ', ''), '　', '') = #{patientKana}
              AND birth_date = #{birthDate}
              AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(phone_number, '-', ''), ' ', ''), '　', ''), '(', ''), ')', '') = #{phoneNumber}
              AND status = 'RESERVED'
            """)
    int countDuplicateReservedReservation(
            @Param("reservationSlotId") Long reservationSlotId,
            @Param("patientName") String patientName,
            @Param("patientKana") String patientKana,
            @Param("birthDate") LocalDate birthDate,
            @Param("phoneNumber") String phoneNumber);

    @Insert("""
            INSERT INTO reservations
              (
                reservation_code,
                reservation_slot_id,
                patient_name,
                patient_kana,
                birth_date,
                phone_number,
                email,
                visit_type,
                symptom,
                status,
                agreed_to_privacy_policy
              )
            VALUES
              (
                #{reservationCode},
                #{reservationSlotId},
                #{patientName},
                #{patientKana},
                #{birthDate},
                #{phoneNumber},
                #{email},
                #{visitType},
                #{symptom},
                #{status},
                #{agreedToPrivacyPolicy}
              )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Reservation reservation);

    @Select("""
            SELECT
              r.id,
              r.reservation_code,
              r.patient_name,
              r.patient_kana,
              r.birth_date,
              r.phone_number,
              r.email,
              r.visit_type,
              r.symptom,
              r.status,
              r.agreed_to_privacy_policy,
              rs.slot_date AS reservation_date,
              rs.start_time AS reservation_time
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            WHERE r.reservation_code = #{reservationCode}
            """)
    ReservationDetail findDetailByReservationCode(@Param("reservationCode") String reservationCode);

    @Select("""
            SELECT
              r.id,
              r.reservation_code,
              r.patient_name,
              r.patient_kana,
              r.birth_date,
              r.phone_number,
              r.email,
              r.visit_type,
              r.symptom,
              r.status,
              r.agreed_to_privacy_policy,
              rs.slot_date AS reservation_date,
              rs.start_time AS reservation_time
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            WHERE r.id = #{id}
            """)
    ReservationDetail findDetailById(@Param("id") Long id);

    @Update("""
            UPDATE reservations
            SET status = 'CANCELLED',
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status = 'RESERVED'
            """)
    int cancelById(@Param("id") Long id);
}
