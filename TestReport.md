# 佐藤医院 Web予約ページ テスト報告書

## 1. テスト概要

### 1.1 対象機能

本テストは、佐藤医院Web予約ページの以下機能を対象とする。

- 予約入力画面
- 予約確認画面
- 予約登録機能
- 予約完了画面
- 予約可能時間帯取得API
- 管理者用予約一覧取得
- 管理者用予約詳細取得
- 予約キャンセル機能
- 予約枠上限チェック
- 入力バリデーション

### 1.2 対象テーブル

- `reservation_slots`
- `reservations`

### 1.3 テスト観点

- 正常に予約登録できること
- 必須項目や形式チェックが正しく動作すること
- 予約枠の上限を超えて予約できないこと
- 予約一覧・詳細が正しく取得できること
- 予約キャンセル時にステータスが `CANCELLED` に更新されること
- APIが仕様どおりのJSONを返却すること
- DB設計どおりに登録・参照・更新されること

---

## 2. テストケース一覧

### 2.1 正常系テストケース

| No | テスト対象 | テスト内容 | 前提条件 | 入力値 | 期待結果 |
|---|---|---|---|---|---|
| TC-N-001 | 予約入力画面 | 予約入力画面を表示する | アプリケーションが起動している | GET `/reservations/new` | 予約フォームが表示される |
| TC-N-002 | 予約確認 | 正しい入力値で確認画面へ遷移する | 予約枠に空きがある | 患者情報、予約日、予約時間、同意チェック | 入力内容が確認画面に表示される |
| TC-N-003 | 予約登録 | 正しい入力値で予約を登録する | 予約枠に空きがある | 正常な予約情報 | `reservations` に予約情報が登録される |
| TC-N-004 | 予約完了 | 予約登録後に完了画面を表示する | 予約登録済み | 予約番号 | 完了メッセージと予約番号が表示される |
| TC-N-005 | 予約番号発行 | 予約登録時に予約番号を発行する | 予約登録処理を実行する | 予約情報 | `RyyyyMMddNNNN` 形式の予約番号が作成される |
| TC-N-006 | 予約可能枠取得 | 指定日の予約可能枠を取得する | `reservation_slots` に対象日データがある | `date=2026-06-01` | 時間帯、空き状況、残数が返却される |
| TC-N-007 | 予約枠残数表示 | 予約済み件数を差し引いた残数を取得する | 同一枠に予約が1件ある | 対象日 | `remaining = capacity - 予約済み件数` になる |
| TC-N-008 | 管理者予約一覧 | 管理者用予約一覧を表示する | 予約データが登録済み | GET `/admin/reservations` | 予約番号、氏名、予約日時、ステータスが表示される |
| TC-N-009 | 管理者予約詳細 | 予約詳細を表示する | 予約データが登録済み | 予約ID | 患者情報、症状、予約日時、ステータスが表示される |
| TC-N-010 | 予約キャンセル | 予約をキャンセルする | 予約ステータスが `RESERVED` | 予約ID | ステータスが `CANCELLED` に更新される |
| TC-N-011 | 初診予約 | 初診区分で予約登録する | 予約枠に空きがある | `visitType=FIRST` | 初診として登録される |
| TC-N-012 | 再診予約 | 再診区分で予約登録する | 予約枠に空きがある | `visitType=RETURN` | 再診として登録される |
| TC-N-013 | 任意メール未入力 | メール未入力で予約登録する | メールが任意項目の場合 | `email=null` | 予約登録できる |
| TC-N-014 | 症状未入力 | 症状未入力で予約登録する | 症状が任意項目の場合 | `symptom=null` | 予約登録できる |
| TC-N-015 | 日付検索 | 管理者一覧で日付検索する | 複数日の予約が存在する | `date=2026-06-01` | 指定日の予約のみ表示される |
| TC-N-016 | ステータス検索 | 管理者一覧でステータス検索する | 複数ステータスの予約が存在する | `status=RESERVED` | 予約済みの予約のみ表示される |

---

### 2.2 異常系テストケース

| No | テスト対象 | テスト内容 | 前提条件 | 入力値 | 期待結果 |
|---|---|---|---|---|---|
| TC-E-001 | 入力チェック | 氏名未入力 | 予約入力画面を表示済み | `patientName=""` | 「氏名を入力してください。」を表示する |
| TC-E-002 | 入力チェック | フリガナ未入力 | 予約入力画面を表示済み | `patientKana=""` | フリガナの入力エラーを表示する |
| TC-E-003 | 入力チェック | 生年月日未入力 | 予約入力画面を表示済み | `birthDate=null` | 生年月日の入力エラーを表示する |
| TC-E-004 | 入力チェック | 電話番号未入力 | 予約入力画面を表示済み | `phoneNumber=""` | 電話番号の入力エラーを表示する |
| TC-E-005 | 入力チェック | メール形式不正 | 予約入力画面を表示済み | `email="test"` | 「正しいメールアドレスを入力してください。」を表示する |
| TC-E-006 | 入力チェック | 予約日未入力 | 予約入力画面を表示済み | `reservationDate=null` | 予約日の入力エラーを表示する |
| TC-E-007 | 入力チェック | 予約時間未入力 | 予約入力画面を表示済み | `reservationTime=null` | 予約時間の入力エラーを表示する |
| TC-E-008 | 入力チェック | 過去日を指定する | 現在日より前の日付を選択 | 過去日 | 「過去の日付は選択できません。」を表示する |
| TC-E-009 | 入力チェック | 個人情報同意なし | 予約入力画面を表示済み | `agreedToPrivacyPolicy=false` | 同意チェックのエラーを表示する |
| TC-E-010 | 予約枠上限 | 満員の予約枠に予約する | 対象枠の予約数が `capacity` に到達済み | 満員枠 | 予約不可エラーを表示する |
| TC-E-011 | 予約可能枠取得API | 存在しない日付の枠を取得する | 対象日の枠なし | `date=2099-01-01` | 空の `slots` を返却する |
| TC-E-012 | 予約登録API | 存在しない予約枠IDで登録する | 対象枠なし | 不正な `reservationSlotId` | 404または400エラーを返却する |
| TC-E-013 | 予約登録API | 不正な初診/再診区分で登録する | なし | `visitType=OTHER` | 400エラーを返却する |
| TC-E-014 | 予約登録API | 症状が500文字を超える | なし | 501文字以上 | 400エラーを返却する |
| TC-E-015 | 管理者詳細API | 存在しない予約IDを指定する | 対象予約なし | `id=9999` | 404エラーを返却する |
| TC-E-016 | キャンセルAPI | 存在しない予約IDをキャンセルする | 対象予約なし | `id=9999` | 404エラーを返却する |
| TC-E-017 | キャンセルAPI | すでにキャンセル済みの予約をキャンセルする | `status=CANCELLED` | 予約ID | エラーまたはキャンセル済みとして返却する |
| TC-E-018 | DB制約 | 同一予約番号を登録する | 同じ予約番号が存在する | 重複 `reservationCode` | UNIQUE制約エラーになる |
| TC-E-019 | DB制約 | 不正ステータスを登録する | なし | `status=INVALID` | CHECK制約エラーになる |
| TC-E-020 | DB制約 | 予約枠IDなしで予約登録する | なし | `reservation_slot_id=null` | NOT NULL制約エラーになる |

---

## 3. API単体テスト項目

### 3.1 GET `/api/reservation-slots`

指定日の予約可能時間帯を取得するAPI。

| No | テスト内容 | リクエスト | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-001 | 予約可能枠を取得する | `/api/reservation-slots?date=2026-06-01` | 200 | `date` と `slots` が返却される |
| API-002 | 予約残数が正しく計算される | 予約済み1件の枠を取得 | 200 | `remaining` が `capacity - 1` になる |
| API-003 | 満員枠は `available=false` になる | capacity分の予約済みデータあり | 200 | `available=false`, `remaining=0` |
| API-004 | 予約枠が存在しない日付を指定する | `/api/reservation-slots?date=2099-01-01` | 200 | `slots=[]` |
| API-005 | 日付パラメータ未指定 | `/api/reservation-slots` | 400 | 入力エラーが返却される |
| API-006 | 日付形式不正 | `/api/reservation-slots?date=2026/06/01` | 400 | 入力エラーが返却される |

---

### 3.2 POST `/api/reservations/validate`

予約内容の入力チェックを行うAPI。

| No | テスト内容 | リクエスト内容 | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-101 | 正常な予約情報を検証する | 正常な予約JSON | 200 | 検証成功 |
| API-102 | 氏名未入力 | `patientName=""` | 400 | `patientName` のエラー |
| API-103 | メール形式不正 | `email="sample"` | 400 | `email` のエラー |
| API-104 | 過去日指定 | 過去日 | 400 | `reservationDate` のエラー |
| API-105 | 個人情報同意なし | `agreedToPrivacyPolicy=false` | 400 | 同意チェックのエラー |
| API-106 | 予約枠満員 | 満員枠を指定 | 400 | 予約枠上限エラー |

---

### 3.3 POST `/api/reservations`

予約を登録するAPI。

| No | テスト内容 | リクエスト内容 | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-201 | 正常に予約登録する | 正常な予約JSON | 201 | `reservationCode`, `status=RESERVED` |
| API-202 | 登録後DBに保存される | 正常な予約JSON | 201 | `reservations` に1件追加 |
| API-203 | 満員枠に予約する | 満員枠 | 400 | 予約不可エラー |
| API-204 | 存在しない予約枠を指定する | 不正な枠ID | 404 | 予約枠なしエラー |
| API-205 | 不正なJSON形式 | JSON構文エラー | 400 | リクエストエラー |
| API-206 | 不正な初診/再診区分 | `visitType=OTHER` | 400 | 入力エラー |
| API-207 | メール未入力で登録する | `email=null` | 201 | 予約登録成功 |
| API-208 | 症状未入力で登録する | `symptom=null` | 201 | 予約登録成功 |

---

### 3.4 GET `/api/reservations/{reservationCode}`

予約番号から予約情報を取得するAPI。

| No | テスト内容 | リクエスト | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-301 | 予約情報を取得する | 登録済み予約番号 | 200 | 予約番号、氏名、予約日時、ステータス |
| API-302 | 存在しない予約番号を指定する | `R999999999999` | 404 | 予約なしエラー |
| API-303 | キャンセル済み予約を取得する | キャンセル済み予約番号 | 200 | `status=CANCELLED` |

---

### 3.5 GET `/api/admin/reservations`

管理者用予約一覧を取得するAPI。

| No | テスト内容 | リクエスト | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-401 | 予約一覧を取得する | `/api/admin/reservations` | 200 | 予約一覧 |
| API-402 | 日付で絞り込む | `date=2026-06-01` | 200 | 指定日の予約のみ |
| API-403 | ステータスで絞り込む | `status=RESERVED` | 200 | 予約済みのみ |
| API-404 | 日付とステータスで絞り込む | `date`, `status` 指定 | 200 | 条件一致のみ |
| API-405 | 予約が存在しない条件で検索 | 該当なし | 200 | 空配列 |
| API-406 | 不正ステータスを指定 | `status=INVALID` | 400 | 入力エラー |

---

### 3.6 GET `/api/admin/reservations/{id}`

管理者用予約詳細を取得するAPI。

| No | テスト内容 | リクエスト | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-501 | 予約詳細を取得する | 登録済み予約ID | 200 | 患者情報、予約日時、症状、ステータス |
| API-502 | 存在しない予約IDを指定する | `id=9999` | 404 | 予約なしエラー |
| API-503 | ID形式不正 | `id=abc` | 400 | 入力エラー |

---

### 3.7 PATCH `/api/admin/reservations/{id}/cancel`

予約をキャンセルするAPI。

| No | テスト内容 | リクエスト | 期待ステータス | 期待レスポンス |
|---|---|---|---|---|
| API-601 | 予約をキャンセルする | 登録済み予約ID | 200 | `status=CANCELLED` |
| API-602 | DBのステータスが更新される | 登録済み予約ID | 200 | DB上も `CANCELLED` |
| API-603 | 存在しない予約IDを指定する | `id=9999` | 404 | 予約なしエラー |
| API-604 | すでにキャンセル済みの予約をキャンセルする | `status=CANCELLED` | 400または200 | キャンセル済みとして扱う |
| API-605 | 来院済み予約をキャンセルする | `status=VISITED` | 400 | キャンセル不可エラー |

---

## 4. 結合テスト項目

### 4.1 予約登録フロー

| No | テスト内容 | 手順 | 期待結果 |
|---|---|---|---|
| IT-001 | 予約入力から完了までの一連の流れ | 予約入力画面を表示 → 正常値入力 → 確認画面 → 登録 → 完了画面 | 予約番号が発行され、DBに保存される |
| IT-002 | 入力エラー時に確認画面へ進まない | 必須項目を空にして送信 | 入力画面に戻りエラーが表示される |
| IT-003 | 満員枠は予約できない | 満員枠を選択して予約 | 予約不可エラーが表示され、DBに登録されない |
| IT-004 | 予約登録後に管理者一覧へ反映される | 予約登録 → 管理者一覧表示 | 登録した予約が一覧に表示される |
| IT-005 | 予約登録後に予約詳細へ反映される | 予約登録 → 詳細画面表示 | 登録した患者情報・症状が表示される |

---

### 4.2 予約枠・残数連携

| No | テスト内容 | 手順 | 期待結果 |
|---|---|---|---|
| IT-101 | 予約登録後に残数が減る | 枠取得 → 予約登録 → 再度枠取得 | `remaining` が1減る |
| IT-102 | キャンセル後に残数が戻る | 予約登録 → キャンセル → 枠取得 | キャンセル済み予約は残数計算から除外される |
| IT-103 | 満員枠の表示 | capacity分登録 → 枠取得 | `available=false` になる |
| IT-104 | 非アクティブ枠は表示されない | `is_active=false` の枠を作成 → 枠取得 | 対象枠が返却されない |

---

### 4.3 管理者機能連携

| No | テスト内容 | 手順 | 期待結果 |
|---|---|---|---|
| IT-201 | 予約一覧から詳細を確認する | 管理者一覧 → 対象予約詳細 | 詳細画面が表示される |
| IT-202 | 予約詳細からキャンセルする | 詳細画面 → キャンセル | ステータスが `CANCELLED` になる |
| IT-203 | キャンセル後に一覧へ反映される | キャンセル → 一覧再表示 | ステータスが `CANCELLED` と表示される |
| IT-204 | 日付検索とステータス検索を組み合わせる | 日付・ステータスを指定して検索 | 条件一致データのみ表示される |

---

### 4.4 DB連携テスト

| No | テスト内容 | 手順 | 期待結果 |
|---|---|---|---|
| IT-301 | 予約登録時に外部キーが正しく設定される | 予約登録 | `reservation_slot_id` が `reservation_slots.id` を参照する |
| IT-302 | 予約番号が一意である | 複数予約を登録 | `reservation_code` が重複しない |
| IT-303 | ステータス制約が守られる | 不正ステータス登録 | 登録できない |
| IT-304 | 初診/再診区分制約が守られる | 不正区分登録 | 登録できない |
| IT-305 | 個人情報同意必須が守られる | 同意なし登録 | 登録できない |

---

## 5. JUnitテストコードの雛形

### 5.1 `ReservationServiceTest`

```java
package com.example.clinicreservation.service;

import com.example.clinicreservation.entity.Reservation;
import com.example.clinicreservation.entity.ReservationSlot;
import com.example.clinicreservation.form.ReservationForm;
import com.example.clinicreservation.repository.ReservationRepository;
import com.example.clinicreservation.repository.ReservationSlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationSlotRepository reservationSlotRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Nested
    @DisplayName("予約登録")
    class CreateReservationTest {

        @Test
        @DisplayName("予約枠に空きがある場合、予約を登録できる")
        void createReservation_success() {
            ReservationSlot slot = new ReservationSlot();
            slot.setId(1L);
            slot.setSlotDate(LocalDate.of(2026, 6, 1));
            slot.setStartTime(LocalTime.of(9, 0));
            slot.setEndTime(LocalTime.of(9, 30));
            slot.setCapacity(3);
            slot.setActive(true);

            ReservationForm form = new ReservationForm();
            form.setReservationSlotId(1L);
            form.setPatientName("山田 太郎");
            form.setPatientKana("ヤマダ タロウ");
            form.setBirthDate(LocalDate.of(1985, 4, 10));
            form.setPhoneNumber("090-1234-5678");
            form.setEmail("taro@example.com");
            form.setVisitType("FIRST");
            form.setSymptom("発熱と喉の痛みがあります。");
            form.setAgreedToPrivacyPolicy(true);

            when(reservationSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
            when(reservationRepository.countByReservationSlotIdAndStatus(1L, "RESERVED")).thenReturn(1L);
            when(reservationRepository.save(any(Reservation.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Reservation result = reservationService.createReservation(form);

            assertThat(result).isNotNull();
            assertThat(result.getPatientName()).isEqualTo("山田 太郎");
            assertThat(result.getStatus()).isEqualTo("RESERVED");

            verify(reservationRepository, times(1)).save(any(Reservation.class));
        }

        @Test
        @DisplayName("予約枠が満員の場合、予約登録できない")
        void createReservation_fullSlot() {
            ReservationSlot slot = new ReservationSlot();
            slot.setId(1L);
            slot.setCapacity(3);
            slot.setActive(true);

            ReservationForm form = new ReservationForm();
            form.setReservationSlotId(1L);
            form.setPatientName("山田 太郎");
            form.setAgreedToPrivacyPolicy(true);

            when(reservationSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
            when(reservationRepository.countByReservationSlotIdAndStatus(1L, "RESERVED")).thenReturn(3L);

            assertThatThrownBy(() -> reservationService.createReservation(form))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("予約枠が上限に達しています");

            verify(reservationRepository, never()).save(any(Reservation.class));
        }

        @Test
        @DisplayName("存在しない予約枠の場合、予約登録できない")
        void createReservation_slotNotFound() {
            ReservationForm form = new ReservationForm();
            form.setReservationSlotId(999L);

            when(reservationSlotRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.createReservation(form))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("予約枠が見つかりません");

            verify(reservationRepository, never()).save(any(Reservation.class));
        }
    }

    @Nested
    @DisplayName("予約キャンセル")
    class CancelReservationTest {

        @Test
        @DisplayName("予約済みの予約をキャンセルできる")
        void cancelReservation_success() {
            Reservation reservation = new Reservation();
            reservation.setId(1L);
            reservation.setReservationCode("R202606010001");
            reservation.setStatus("RESERVED");

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any(Reservation.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Reservation result = reservationService.cancelReservation(1L);

            assertThat(result.getStatus()).isEqualTo("CANCELLED");
            verify(reservationRepository, times(1)).save(reservation);
        }

        @Test
        @DisplayName("存在しない予約はキャンセルできない")
        void cancelReservation_notFound() {
            when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.cancelReservation(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("予約が見つかりません");
        }
    }
}
```

---

### 5.2 `ReservationApiControllerTest`

```java
package com.example.clinicreservation.controller.api;

import com.example.clinicreservation.dto.ReservationRequest;
import com.example.clinicreservation.entity.Reservation;
import com.example.clinicreservation.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationApiController.class)
class ReservationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @DisplayName("正常な予約情報で予約登録APIを実行できる")
    void createReservation_success() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setReservationSlotId(1L);
        request.setPatientName("山田 太郎");
        request.setPatientKana("ヤマダ タロウ");
        request.setBirthDate(LocalDate.of(1985, 4, 10));
        request.setPhoneNumber("090-1234-5678");
        request.setEmail("taro@example.com");
        request.setVisitType("FIRST");
        request.setSymptom("発熱と喉の痛みがあります。");
        request.setAgreedToPrivacyPolicy(true);

        Reservation reservation = new Reservation();
        reservation.setReservationCode("R202606010001");
        reservation.setPatientName("山田 太郎");
        reservation.setStatus("RESERVED");

        Mockito.when(reservationService.createReservation(any()))
                .thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationCode").value("R202606010001"))
                .andExpect(jsonPath("$.patientName").value("山田 太郎"))
                .andExpect(jsonPath("$.status").value("RESERVED"));
    }

    @Test
    @DisplayName("氏名未入力の場合、400エラーを返す")
    void createReservation_patientNameBlank() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setReservationSlotId(1L);
        request.setPatientName("");
        request.setPatientKana("ヤマダ タロウ");
        request.setBirthDate(LocalDate.of(1985, 4, 10));
        request.setPhoneNumber("090-1234-5678");
        request.setEmail("taro@example.com");
        request.setVisitType("FIRST");
        request.setAgreedToPrivacyPolicy(true);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("メールアドレス形式が不正な場合、400エラーを返す")
    void createReservation_invalidEmail() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setReservationSlotId(1L);
        request.setPatientName("山田 太郎");
        request.setPatientKana("ヤマダ タロウ");
        request.setBirthDate(LocalDate.of(1985, 4, 10));
        request.setPhoneNumber("090-1234-5678");
        request.setEmail("invalid-email");
        request.setVisitType("FIRST");
        request.setAgreedToPrivacyPolicy(true);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
```

---

### 5.3 `ReservationSlotApiControllerTest`

```java
package com.example.clinicreservation.controller.api;

import com.example.clinicreservation.dto.ReservationSlotResponse;
import com.example.clinicreservation.service.ReservationSlotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationSlotApiController.class)
class ReservationSlotApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationSlotService reservationSlotService;

    @Test
    @DisplayName("指定日の予約可能枠を取得できる")
    void getReservationSlots_success() throws Exception {
        ReservationSlotResponse.Slot slot = new ReservationSlotResponse.Slot();
        slot.setTime(LocalTime.of(9, 0));
        slot.setAvailable(true);
        slot.setRemaining(2);

        ReservationSlotResponse response = new ReservationSlotResponse();
        response.setDate(LocalDate.of(2026, 6, 1));
        response.setSlots(List.of(slot));

        when(reservationSlotService.getAvailableSlots(LocalDate.of(2026, 6, 1)))
                .thenReturn(response);

        mockMvc.perform(get("/api/reservation-slots")
                        .param("date", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-06-01"))
                .andExpect(jsonPath("$.slots[0].time").value("09:00:00"))
                .andExpect(jsonPath("$.slots[0].available").value(true))
                .andExpect(jsonPath("$.slots[0].remaining").value(2));
    }

    @Test
    @DisplayName("日付パラメータ未指定の場合、400エラーを返す")
    void getReservationSlots_dateMissing() throws Exception {
        mockMvc.perform(get("/api/reservation-slots"))
                .andExpect(status().isBadRequest());
    }
}
```

---

### 5.4 `AdminReservationApiControllerTest`

```java
package com.example.clinicreservation.controller.api.admin;

import com.example.clinicreservation.entity.Reservation;
import com.example.clinicreservation.service.AdminReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminReservationApiController.class)
class AdminReservationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminReservationService adminReservationService;

    @Test
    @DisplayName("管理者用予約一覧を取得できる")
    void getReservations_success() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationCode("R202606010001");
        reservation.setPatientName("山田 太郎");
        reservation.setVisitType("FIRST");
        reservation.setStatus("RESERVED");

        when(adminReservationService.findReservations(isNull(), isNull()))
                .thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservations[0].id").value(1))
                .andExpect(jsonPath("$.reservations[0].reservationCode").value("R202606010001"))
                .andExpect(jsonPath("$.reservations[0].patientName").value("山田 太郎"))
                .andExpect(jsonPath("$.reservations[0].status").value("RESERVED"));
    }

    @Test
    @DisplayName("管理者用予約詳細を取得できる")
    void getReservationDetail_success() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationCode("R202606010001");
        reservation.setPatientName("山田 太郎");
        reservation.setStatus("RESERVED");

        when(adminReservationService.findById(1L)).thenReturn(reservation);

        mockMvc.perform(get("/api/admin/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationCode").value("R202606010001"))
                .andExpect(jsonPath("$.patientName").value("山田 太郎"));
    }

    @Test
    @DisplayName("予約をキャンセルできる")
    void cancelReservation_success() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationCode("R202606010001");
        reservation.setStatus("CANCELLED");

        when(adminReservationService.cancelReservation(1L)).thenReturn(reservation);

        mockMvc.perform(patch("/api/admin/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationCode").value("R202606010001"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
```

---

### 5.5 `ReservationRepositoryTest`

```java
package com.example.clinicreservation.repository;

import com.example.clinicreservation.entity.Reservation;
import com.example.clinicreservation.entity.ReservationSlot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationSlotRepository reservationSlotRepository;

    @Test
    @DisplayName("予約枠IDとステータスで予約件数を取得できる")
    void countByReservationSlotIdAndStatus_success() {
        ReservationSlot slot = new ReservationSlot();
        slot.setSlotDate(LocalDate.of(2026, 6, 1));
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(9, 30));
        slot.setCapacity(3);
        slot.setActive(true);
        reservationSlotRepository.save(slot);

        Reservation reservation = new Reservation();
        reservation.setReservationCode("R202606010001");
        reservation.setReservationSlot(slot);
        reservation.setPatientName("山田 太郎");
        reservation.setPatientKana("ヤマダ タロウ");
        reservation.setBirthDate(LocalDate.of(1985, 4, 10));
        reservation.setPhoneNumber("090-1234-5678");
        reservation.setEmail("taro@example.com");
        reservation.setVisitType("FIRST");
        reservation.setSymptom("発熱と喉の痛みがあります。");
        reservation.setStatus("RESERVED");
        reservation.setAgreedToPrivacyPolicy(true);
        reservationRepository.save(reservation);

        long count = reservationRepository.countByReservationSlotIdAndStatus(slot.getId(), "RESERVED");

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("キャンセル済み予約は予約済み件数に含まれない")
    void countByReservationSlotIdAndStatus_cancelledExcluded() {
        ReservationSlot slot = new ReservationSlot();
        slot.setSlotDate(LocalDate.of(2026, 6, 1));
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(9, 30));
        slot.setCapacity(3);
        slot.setActive(true);
        reservationSlotRepository.save(slot);

        Reservation reservation = new Reservation();
        reservation.setReservationCode("R202606010001");
        reservation.setReservationSlot(slot);
        reservation.setPatientName("山田 太郎");
        reservation.setPatientKana("ヤマダ タロウ");
        reservation.setBirthDate(LocalDate.of(1985, 4, 10));
        reservation.setPhoneNumber("090-1234-5678");
        reservation.setVisitType("FIRST");
        reservation.setStatus("CANCELLED");
        reservation.setAgreedToPrivacyPolicy(true);
        reservationRepository.save(reservation);

        long count = reservationRepository.countByReservationSlotIdAndStatus(slot.getId(), "RESERVED");

        assertThat(count).isEqualTo(0);
    }
}
```

---

## 6. テスト実施時の確認ポイント

### 6.1 画面確認

- 予約入力画面の必須項目が分かりやすいこと
- エラーメッセージが項目付近に表示されること
- 確認画面で入力内容が正しく表示されること
- 完了画面で予約番号が表示されること
- 管理者一覧で予約日時順に表示されること
- キャンセル後、一覧・詳細のステータス表示が更新されること

### 6.2 API確認

- HTTPステータスが仕様どおりであること
- JSONのキー名が仕様どおりであること
- 異常時のエラーレスポンス形式が統一されていること
- 入力チェックエラー時に対象フィールド名が返却されること

### 6.3 DB確認

- `reservations.reservation_code` が一意であること
- `reservations.reservation_slot_id` が `reservation_slots.id` を参照していること
- `status` は `RESERVED`, `CANCELLED`, `VISITED` のみ登録できること
- `visit_type` は `FIRST`, `RETURN` のみ登録できること
- 予約枠残数計算では `CANCELLED` を除外すること

---

## 7. テスト結果記録欄

| No | テスト項目 | 実施日 | 結果 | 備考 |
|---|---|---|---|---|
| 1 | 予約入力画面表示 |  | 未実施 |  |
| 2 | 予約確認画面表示 |  | 未実施 |  |
| 3 | 予約登録 |  | 未実施 |  |
| 4 | 予約完了画面表示 |  | 未実施 |  |
| 5 | 予約可能枠取得API |  | 未実施 |  |
| 6 | 管理者予約一覧 |  | 未実施 |  |
| 7 | 管理者予約詳細 |  | 未実施 |  |
| 8 | 予約キャンセル |  | 未実施 |  |
| 9 | 予約枠上限チェック |  | 未実施 |  |
| 10 | 入力バリデーション |  | 未実施 |  |

---

## 8. 総合判定

| 項目 | 内容 |
|---|---|
| 総合結果 | 未実施 |
| 残課題 | 実装後にテスト実施結果を記録する |
| 備考 | JUnitテスト、APIテスト、画面操作テストを実施後に更新する |