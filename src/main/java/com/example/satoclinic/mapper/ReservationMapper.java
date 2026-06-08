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
              r.phone_number,
              r.symptom,
              rs.slot_date AS reservation_date,
              rs.start_time AS reservation_time,
              r.visit_type,
              r.status,
              r.deleted_at
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            <where>
              <if test="!includeDeleted">
                r.deleted_at IS NULL
              </if>
              <if test="date != null">
                AND rs.slot_date = #{date}
              </if>
              <if test="status != null and status != ''">
                AND r.status = #{status}
              </if>
              <if test="patientName != null and patientName != ''">
                AND r.patient_name LIKE CONCAT('%', #{patientName}, '%')
              </if>
              <if test="reservationCode != null and reservationCode != ''">
                AND r.reservation_code LIKE CONCAT('%', #{reservationCode}, '%')
              </if>
              <if test="phoneNumber != null and phoneNumber != ''">
                AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(r.phone_number, '-', ''), ' ', ''), '　', ''), '(', ''), ')', '') LIKE CONCAT('%', #{phoneNumber}, '%')
              </if>
            </where>
            ORDER BY rs.slot_date ASC, rs.start_time ASC, r.created_at ASC, r.id ASC
            </script>
            """)
    java.util.List<AdminReservationSummary> findAdminSummaries(
            @Param("date") LocalDate date,
            @Param("status") String status,
            @Param("patientName") String patientName,
            @Param("reservationCode") String reservationCode,
            @Param("phoneNumber") String phoneNumber,
            @Param("includeDeleted") boolean includeDeleted);

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
              AND deleted_at IS NULL
            """)
    int countReservedBySlotId(@Param("reservationSlotId") Long reservationSlotId);

    @Select("""
            SELECT COUNT(*)
            FROM reservations
            WHERE reservation_slot_id = #{reservationSlotId}
              AND id <> #{reservationId}
              AND status = 'RESERVED'
              AND deleted_at IS NULL
            """)
    int countReservedBySlotIdExcludingReservation(
            @Param("reservationSlotId") Long reservationSlotId,
            @Param("reservationId") Long reservationId);

    @Select("""
            SELECT COUNT(*)
            FROM reservations
            WHERE reservation_slot_id = #{reservationSlotId}
              AND REPLACE(REPLACE(TRIM(patient_name), ' ', ''), '　', '') = #{patientName}
              AND REPLACE(REPLACE(TRIM(patient_kana), ' ', ''), '　', '') = #{patientKana}
              AND birth_date = #{birthDate}
              AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(phone_number, '-', ''), ' ', ''), '　', ''), '(', ''), ')', '') = #{phoneNumber}
              AND status = 'RESERVED'
              AND deleted_at IS NULL
            """)
    int countDuplicateReservedReservation(
            @Param("reservationSlotId") Long reservationSlotId,
            @Param("patientName") String patientName,
            @Param("patientKana") String patientKana,
            @Param("birthDate") LocalDate birthDate,
            @Param("phoneNumber") String phoneNumber);

    @Select("""
            SELECT COUNT(*)
            FROM reservations
            WHERE reservation_slot_id = #{reservationSlotId}
              AND id <> #{reservationId}
              AND REPLACE(REPLACE(TRIM(patient_name), ' ', ''), '　', '') = #{patientName}
              AND REPLACE(REPLACE(TRIM(patient_kana), ' ', ''), '　', '') = #{patientKana}
              AND birth_date = #{birthDate}
              AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(phone_number, '-', ''), ' ', ''), '　', ''), '(', ''), ')', '') = #{phoneNumber}
              AND status = 'RESERVED'
              AND deleted_at IS NULL
            """)
    int countDuplicateReservedReservationExcludingReservation(
            @Param("reservationSlotId") Long reservationSlotId,
            @Param("reservationId") Long reservationId,
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
              r.reservation_slot_id,
              r.patient_name,
              r.patient_kana,
              r.birth_date,
              r.phone_number,
              r.email,
              r.visit_type,
              r.symptom,
              r.status,
              r.cancel_reason,
              r.deleted_at,
              r.deleted_by,
              r.delete_reason,
              r.agreed_to_privacy_policy,
              rs.slot_date AS reservation_date,
              rs.start_time AS reservation_time
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            WHERE r.reservation_code = #{reservationCode}
              AND r.deleted_at IS NULL
            """)
    ReservationDetail findDetailByReservationCode(@Param("reservationCode") String reservationCode);

    @Select("""
            <script>
            SELECT
              r.id,
              r.reservation_code,
              r.reservation_slot_id,
              r.patient_name,
              r.patient_kana,
              r.birth_date,
              r.phone_number,
              r.email,
              r.visit_type,
              r.symptom,
              r.status,
              r.cancel_reason,
              r.deleted_at,
              r.deleted_by,
              r.delete_reason,
              r.agreed_to_privacy_policy,
              rs.slot_date AS reservation_date,
              rs.start_time AS reservation_time
            FROM reservations r
            INNER JOIN reservation_slots rs
              ON r.reservation_slot_id = rs.id
            WHERE r.id = #{id}
            <if test="!includeDeleted">
              AND r.deleted_at IS NULL
            </if>
            </script>
            """)
    ReservationDetail findDetailById(@Param("id") Long id, @Param("includeDeleted") boolean includeDeleted);

    @Select("""
            SELECT
              id,
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
              agreed_to_privacy_policy,
              deleted_at,
              deleted_by,
              delete_reason,
              created_at,
              updated_at
            FROM reservations
            WHERE id = #{id}
            FOR UPDATE
            """)
    Reservation lockById(@Param("id") Long id);

    @Update("""
            UPDATE reservations
            SET status = 'CANCELLED',
                cancel_reason = #{cancelReason},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status = 'RESERVED'
              AND deleted_at IS NULL
            """)
    int cancelById(@Param("id") Long id, @Param("cancelReason") String cancelReason);

    @Update("""
            UPDATE reservations
            SET status = 'VISITED',
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status = 'RESERVED'
              AND deleted_at IS NULL
            """)
    int markVisitedById(@Param("id") Long id);

    @Update("""
            UPDATE reservations
            SET status = 'RESERVED',
                cancel_reason = NULL,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status IN ('VISITED', 'CANCELLED')
              AND deleted_at IS NULL
            """)
    int restoreReservedById(@Param("id") Long id);

    @Update("""
            UPDATE reservations
            SET reservation_slot_id = #{reservationSlotId},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND status = 'RESERVED'
              AND deleted_at IS NULL
            """)
    int updateReservationSlotById(
            @Param("id") Long id,
            @Param("reservationSlotId") Long reservationSlotId);

    @Update("""
            UPDATE reservations
            SET deleted_at = CURRENT_TIMESTAMP,
                deleted_by = #{deletedBy},
                delete_reason = #{deleteReason},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND deleted_at IS NULL
            """)
    int softDeleteById(
            @Param("id") Long id,
            @Param("deleteReason") String deleteReason,
            @Param("deletedBy") String deletedBy);

    @Update("""
            UPDATE reservations
            SET deleted_at = NULL,
                deleted_by = NULL,
                delete_reason = NULL,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
              AND deleted_at IS NOT NULL
            """)
    int restoreDeletedById(@Param("id") Long id);
}
