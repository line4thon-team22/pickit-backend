-- 앱 시작 시 자동 실행하지 않는 수동 DDL입니다.
-- users 테이블 준비 및 앱 실행(JPA의 user_location_verifications 생성) 후 실행합니다.
-- user_location_verifications는 최종 ERD에 없는 테이블입니다(#59).
-- 회원이 위치 인증을 한 번이라도 완료했는지 회원당 한 행으로 저장하며 만료 없이 유지합니다.
-- 회원 엔티티 없이 user_id를 users에 DB 외래키로 연결합니다.
-- 기존 데이터에 없는 user_id가 있으면 FK 추가가 실패하므로 그 데이터를 먼저 확인하세요.
USE pickit;

SET @pickit_location_verification_user_fk_exists = (
    SELECT COUNT(*)
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_location_verifications'
      AND COLUMN_NAME = 'user_id'
      AND REFERENCED_TABLE_NAME = 'users'
      AND REFERENCED_COLUMN_NAME = 'user_id'
);

SET @pickit_location_verification_user_fk_sql = IF(
    @pickit_location_verification_user_fk_exists = 0,
    'ALTER TABLE user_location_verifications ADD CONSTRAINT fk_user_location_verifications_user FOREIGN KEY (user_id) REFERENCES users (user_id)',
    'SELECT ''users FK already exists'' AS message'
);

PREPARE pickit_location_verification_user_fk FROM @pickit_location_verification_user_fk_sql;
EXECUTE pickit_location_verification_user_fk;
DEALLOCATE PREPARE pickit_location_verification_user_fk;

-- 이 테이블이 생기기 전에 팝업 인증을 한 회원도 인증 완료로 보이게 기존 기록으로 채웁니다.
-- 이미 행이 있는 회원은 건너뛰므로 여러 번 실행해도 됩니다.
INSERT INTO user_location_verifications (user_id, first_verified_at, last_verified_at)
SELECT v.user_id, MIN(v.verified_at), MAX(v.verified_at)
FROM popup_verifications v
JOIN users u ON u.user_id = v.user_id
LEFT JOIN user_location_verifications l ON l.user_id = v.user_id
WHERE l.user_id IS NULL
GROUP BY v.user_id;
