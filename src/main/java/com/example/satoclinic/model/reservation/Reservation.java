package com.example.satoclinic.model.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private String reservationCode;
    private Long reservationSlotId;
    private String patientName;
    private String patientKana;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private String visitType;
    private String symptom;
    private String status;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private String deleteReason;
    private boolean agreedToPrivacyPolicy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public Long getReservationSlotId() {
        return reservationSlotId;
    }

    public void setReservationSlotId(Long reservationSlotId) {
        this.reservationSlotId = reservationSlotId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientKana() {
        return patientKana;
    }

    public void setPatientKana(String patientKana) {
        this.patientKana = patientKana;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public boolean isAgreedToPrivacyPolicy() {
        return agreedToPrivacyPolicy;
    }

    public void setAgreedToPrivacyPolicy(boolean agreedToPrivacyPolicy) {
        this.agreedToPrivacyPolicy = agreedToPrivacyPolicy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
