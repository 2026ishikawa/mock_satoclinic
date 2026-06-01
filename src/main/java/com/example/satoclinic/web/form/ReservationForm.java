package com.example.satoclinic.web.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReservationForm {

    @NotBlank(message = "氏名は必須です。")
    @Size(max = 50, message = "氏名は50文字以内で入力してください。")
    private String patientName;

    @NotBlank(message = "フリガナは必須です。")
    @Size(max = 50, message = "フリガナは50文字以内で入力してください。")
    @Pattern(regexp = "^[ァ-ヶー　 ]+$", message = "フリガナは全角カタカナで入力してください。")
    private String patientKana;

    @NotNull(message = "生年月日は必須です。")
    @Past(message = "生年月日は過去日を入力してください。")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotBlank(message = "電話番号は必須です。")
    @Pattern(regexp = "^[0-9\\-]{10,20}$", message = "電話番号の形式が正しくありません。")
    private String phoneNumber;

    @Email(message = "メールアドレスの形式が正しくありません。")
    @Size(max = 255, message = "メールアドレスは255文字以内で入力してください。")
    private String email;

    @NotBlank(message = "初診/再診は必須です。")
    private String visitType;

    @NotNull(message = "予約日は必須です。")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate;

    @NotBlank(message = "予約時間は必須です。")
    private String reservationTime;

    @Size(max = 500, message = "症状は500文字以内で入力してください。")
    private String symptom;

    @AssertTrue(message = "個人情報同意が必要です。")
    private boolean agreedToPrivacyPolicy;

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

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public boolean isAgreedToPrivacyPolicy() {
        return agreedToPrivacyPolicy;
    }

    public void setAgreedToPrivacyPolicy(boolean agreedToPrivacyPolicy) {
        this.agreedToPrivacyPolicy = agreedToPrivacyPolicy;
    }
}
