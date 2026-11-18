-- 앱 시작 시 자동 실행하지 않는 수동 DDL입니다.
-- users 테이블 준비 및 앱 실행(JPA의 user_interests 생성/갱신) 후 실행합니다.
-- 최종 ERD의 user_interests 3개 컬럼은 그대로 두고 아래만 보완합니다.
--   1) 원본 export에는 AUTO_INCREMENT가 없습니다. 기존 엔티티와 같은 IDENTITY 방식으로 맞춥니다.
--   2) ERD에 없는 UK(user_id, category_id)로 같은 관심사의 중복 저장을 막습니다.
--   3) 회원 엔티티 없이 user_id를 users에 DB 외래키로 연결합니다.
-- 같은 회원·카테고리 중복 행이나 users에 없는 user_id가 있으면 해당 단계가 실패하므로 먼저 확인하세요.
USE pickit;

ALTER TABLE user_interests
    MODIFY user_interest_id BIGINT NOT NULL AUTO_INCREMENT;

SET @pickit_interest_uk_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_interests'
      AND CONSTRAINT_TYPE = 'UNIQUE'
      AND CONSTRAINT_NAME = 'uk_user_interests_user_category'
);

SET @pickit_interest_uk_sql = IF(
    @pickit_interest_uk_exists = 0,
    'ALTER TABLE user_interests ADD CONSTRAINT uk_user_interests_user_category UNIQUE (user_id, category_id)',
    'SELECT ''user_interests UK already exists'' AS message'
);

PREPARE pickit_interest_uk FROM @pickit_interest_uk_sql;
EXECUTE pickit_interest_uk;
DEALLOCATE PREPARE pickit_interest_uk;

SET @pickit_interest_user_fk_exists = (
    SELECT COUNT(*)
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_interests'
      AND COLUMN_NAME = 'user_id'
      AND REFERENCED_TABLE_NAME = 'users'
      AND REFERENCED_COLUMN_NAME = 'user_id'
);

SET @pickit_interest_user_fk_sql = IF(
    @pickit_interest_user_fk_exists = 0,
    'ALTER TABLE user_interests ADD CONSTRAINT fk_user_interests_user FOREIGN KEY (user_id) REFERENCES users (user_id)',
    'SELECT ''users FK already exists'' AS message'
);

PREPARE pickit_interest_user_fk FROM @pickit_interest_user_fk_sql;
EXECUTE pickit_interest_user_fk;
DEALLOCATE PREPARE pickit_interest_user_fk;
