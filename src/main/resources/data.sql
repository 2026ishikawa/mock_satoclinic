INSERT INTO reservation_slots
  (slot_date, start_time, end_time, capacity, is_active)
SELECT
  DATEADD('DAY', day_offset.x, DATE '2026-06-01'),
  time_slots.start_time,
  time_slots.end_time,
  3,
  TRUE
FROM SYSTEM_RANGE(0, 578) AS day_offset
CROSS JOIN (
  SELECT TIME '09:00:00' AS start_time, TIME '09:30:00' AS end_time
  UNION ALL
  SELECT TIME '09:30:00', TIME '10:00:00'
  UNION ALL
  SELECT TIME '10:00:00', TIME '10:30:00'
  UNION ALL
  SELECT TIME '10:30:00', TIME '11:00:00'
  UNION ALL
  SELECT TIME '11:00:00', TIME '11:30:00'
  UNION ALL
  SELECT TIME '11:30:00', TIME '12:00:00'
  UNION ALL
  SELECT TIME '14:00:00', TIME '14:30:00'
  UNION ALL
  SELECT TIME '14:30:00', TIME '15:00:00'
  UNION ALL
  SELECT TIME '15:00:00', TIME '15:30:00'
  UNION ALL
  SELECT TIME '15:30:00', TIME '16:00:00'
  UNION ALL
  SELECT TIME '16:00:00', TIME '16:30:00'
  UNION ALL
  SELECT TIME '16:30:00', TIME '17:00:00'
  UNION ALL
  SELECT TIME '17:00:00', TIME '17:30:00'
  UNION ALL
  SELECT TIME '17:30:00', TIME '18:00:00'
  UNION ALL
  SELECT TIME '18:00:00', TIME '18:30:00'
  UNION ALL
  SELECT TIME '18:30:00', TIME '19:00:00'
) AS time_slots
WHERE EXTRACT(DAY_OF_WEEK FROM DATEADD('DAY', day_offset.x, DATE '2026-06-01')) NOT IN (1, 5)
  AND NOT (
    EXTRACT(DAY_OF_WEEK FROM DATEADD('DAY', day_offset.x, DATE '2026-06-01')) = 7
    AND time_slots.start_time >= TIME '14:00:00'
  );

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
    'R202606010001',
    1,
    '山田 太郎',
    'ヤマダ タロウ',
    '1985-04-10',
    '090-1234-5678',
    'taro@example.com',
    'FIRST',
    '腹痛と軽い発熱があります。',
    'RESERVED',
    TRUE
  ),
  (
    'R202606010002',
    2,
    '佐藤 花子',
    'サトウ ハナコ',
    '1990-08-20',
    '080-1111-2222',
    'hanako@example.com',
    'RETURN',
    '咳が続いています。',
    'RESERVED',
    TRUE
  );
