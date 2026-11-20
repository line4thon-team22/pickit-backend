-- 채팅 화면(채팅 목록·채팅방 메시지) 연동 확인용 테스트 데이터입니다.
-- 메인 테스트 계정은 pickittest1@example.com이고, 상대 계정은 pickittest2~4@example.com입니다. (비밀번호는 변경하지 않습니다.)
-- 실행 전에 네 계정이 회원가입되어 있어야 합니다. 하나라도 없으면 아무것도 추가하지 않습니다.
--
-- 추가되는 데이터 (pickittest1로 로그인하면 채팅 목록에 방 3개가 보입니다)
--   방 1: pickittest2의 교환글에 pickittest1이 채팅 (pickittest1 = 구매자)
--   방 2: pickittest3의 교환글에 pickittest1이 채팅 (pickittest1 = 구매자, 사진 메시지 포함)
--   방 3: pickittest1의 교환글에 pickittest4가 채팅 (pickittest1 = 판매자)
--
-- 교환글은 진행 중인 팝업 중 가장 앞 번호 팝업에 일반 교환(GENERAL)으로 추가하고, 사진은 그 팝업 썸네일을 씁니다.
-- 이미 추가된 교환글·채팅방·메시지는 건너뛰므로 여러 번 실행해도 중복으로 쌓이지 않습니다.
USE pickit;
SET NAMES utf8mb4;
SET @demo_now = DATE_ADD(UTC_TIMESTAMP(), INTERVAL 9 HOUR);
SET @demo_desc = '채팅 테스트용 교환글입니다.';

SET @u1 = (SELECT user_id FROM auth_accounts WHERE provider = 'LOCAL' AND provider_user_id = 'pickittest1@example.com');
SET @u2 = (SELECT user_id FROM auth_accounts WHERE provider = 'LOCAL' AND provider_user_id = 'pickittest2@example.com');
SET @u3 = (SELECT user_id FROM auth_accounts WHERE provider = 'LOCAL' AND provider_user_id = 'pickittest3@example.com');
SET @u4 = (SELECT user_id FROM auth_accounts WHERE provider = 'LOCAL' AND provider_user_id = 'pickittest4@example.com');
SET @demo_ready = (@u1 IS NOT NULL AND @u2 IS NOT NULL AND @u3 IS NOT NULL AND @u4 IS NOT NULL);

SET @popup_id = COALESCE((SELECT MIN(popup_id) FROM popups WHERE status = 'ONGOING'), (SELECT MIN(popup_id) FROM popups));
SET @category_id = (SELECT category_id FROM popups WHERE popup_id = @popup_id);
SET @demo_img = (SELECT thumbnail_url FROM popups WHERE popup_id = @popup_id);

START TRANSACTION;

-- 교환글 3개 -------------------------------------------------------------
INSERT INTO exchange_posts (user_id, popup_id, category_id, have_item_name, want_item_name, preferred_location,
                            preferred_time, description, status, created_at, updated_at)
SELECT @u2, @popup_id, @category_id, '한정판 키링', '포토카드', '팝업 입구 앞', '평일 오후 3시 이후', @demo_desc, 'OPEN',
       DATE_SUB(@demo_now, INTERVAL 5 HOUR), DATE_SUB(@demo_now, INTERVAL 5 HOUR)
FROM DUAL
WHERE @demo_ready AND @popup_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM exchange_posts WHERE user_id = @u2 AND description = @demo_desc);
SET @p1 = (SELECT MAX(post_id) FROM exchange_posts WHERE user_id = @u2 AND description = @demo_desc);

INSERT INTO exchange_posts (user_id, popup_id, category_id, have_item_name, want_item_name, preferred_location,
                            preferred_time, description, status, created_at, updated_at)
SELECT @u3, @popup_id, @category_id, '아크릴 스탠드', '랜덤 뱃지', '근처 카페', '주말 오전', @demo_desc, 'OPEN',
       DATE_SUB(@demo_now, INTERVAL 4 HOUR), DATE_SUB(@demo_now, INTERVAL 4 HOUR)
FROM DUAL
WHERE @demo_ready AND @popup_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM exchange_posts WHERE user_id = @u3 AND description = @demo_desc);
SET @p2 = (SELECT MAX(post_id) FROM exchange_posts WHERE user_id = @u3 AND description = @demo_desc);

INSERT INTO exchange_posts (user_id, popup_id, category_id, have_item_name, want_item_name, preferred_location,
                            preferred_time, description, status, created_at, updated_at)
SELECT @u1, @popup_id, @category_id, '포토카드', '스티커 세트', '팝업 출구 앞', '오늘 저녁', @demo_desc, 'OPEN',
       DATE_SUB(@demo_now, INTERVAL 3 HOUR), DATE_SUB(@demo_now, INTERVAL 3 HOUR)
FROM DUAL
WHERE @demo_ready AND @popup_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM exchange_posts WHERE user_id = @u1 AND description = @demo_desc);
SET @p3 = (SELECT MAX(post_id) FROM exchange_posts WHERE user_id = @u1 AND description = @demo_desc);

-- 교환글 사진 (팝업 썸네일) ------------------------------------------------
INSERT INTO post_images (post_id, image_url, sort_order)
SELECT p.post_id, @demo_img, 0
FROM exchange_posts p
WHERE p.post_id IN (@p1, @p2, @p3) AND @demo_img IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM post_images i WHERE i.post_id = p.post_id);

-- 채팅방 3개 -------------------------------------------------------------
INSERT INTO chat_rooms (post_id, seller_id, buyer_id, created_at)
SELECT @p1, @u2, @u1, DATE_SUB(@demo_now, INTERVAL 150 MINUTE) FROM DUAL
WHERE @p1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM chat_rooms WHERE post_id = @p1 AND buyer_id = @u1);
SET @r1 = (SELECT room_id FROM chat_rooms WHERE post_id = @p1 AND buyer_id = @u1);

INSERT INTO chat_rooms (post_id, seller_id, buyer_id, created_at)
SELECT @p2, @u3, @u1, DATE_SUB(@demo_now, INTERVAL 120 MINUTE) FROM DUAL
WHERE @p2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM chat_rooms WHERE post_id = @p2 AND buyer_id = @u1);
SET @r2 = (SELECT room_id FROM chat_rooms WHERE post_id = @p2 AND buyer_id = @u1);

INSERT INTO chat_rooms (post_id, seller_id, buyer_id, created_at)
SELECT @p3, @u1, @u4, DATE_SUB(@demo_now, INTERVAL 90 MINUTE) FROM DUAL
WHERE @p3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM chat_rooms WHERE post_id = @p3 AND buyer_id = @u4);
SET @r3 = (SELECT room_id FROM chat_rooms WHERE post_id = @p3 AND buyer_id = @u4);

-- 메시지 (메시지가 하나도 없는 방에만 추가) ---------------------------------
SET @r1_empty = (@r1 IS NOT NULL AND (SELECT COUNT(*) FROM chat_messages WHERE room_id = @r1) = 0);
SET @r2_empty = (@r2 IS NOT NULL AND (SELECT COUNT(*) FROM chat_messages WHERE room_id = @r2) = 0);
SET @r3_empty = (@r3 IS NOT NULL AND (SELECT COUNT(*) FROM chat_messages WHERE room_id = @r3) = 0);

-- 방 1: pickittest1(구매자) ↔ pickittest2(판매자)
INSERT INTO chat_messages (room_id, sender_id, message_type, content, image_url, created_at)
SELECT @r1, m.sender_id, 'TEXT', m.content, NULL, DATE_SUB(@demo_now, INTERVAL m.minutes_ago MINUTE)
FROM (
    SELECT @u1 AS sender_id, '안녕하세요! 키링 아직 교환 가능할까요?' AS content, 148 AS minutes_ago
    UNION ALL SELECT @u2, '네 가능해요! 어떤 포토카드 있으세요?', 145
    UNION ALL SELECT @u1, '멤버 A 포토카드 있어요. 상태 좋아요.', 140
    UNION ALL SELECT @u2, '좋아요 그걸로 교환해요 :)', 135
    UNION ALL SELECT @u1, '오늘 오후 3시에 팝업 입구 앞 괜찮으세요?', 130
    UNION ALL SELECT @u2, '네 3시에 뵐게요!', 125
) m
WHERE @r1_empty;

-- 방 2: pickittest1(구매자) ↔ pickittest3(판매자), 사진 메시지 포함
INSERT INTO chat_messages (room_id, sender_id, message_type, content, image_url, created_at)
SELECT @r2, m.sender_id, m.message_type, m.content, m.image_url, DATE_SUB(@demo_now, INTERVAL m.minutes_ago MINUTE)
FROM (
    SELECT @u1 AS sender_id, 'TEXT' AS message_type, '아크릴 스탠드 실물 사진 볼 수 있을까요?' AS content, NULL AS image_url, 118 AS minutes_ago
    UNION ALL SELECT @u3, 'IMAGE', NULL, @demo_img, 115
    UNION ALL SELECT @u3, 'TEXT', '미개봉이에요!', NULL, 114
    UNION ALL SELECT @u1, 'TEXT', '제가 가진 랜덤 뱃지도 보내드릴게요', NULL, 110
    UNION ALL SELECT @u1, 'IMAGE', NULL, @demo_img, 109
    UNION ALL SELECT @u3, 'TEXT', '좋네요. 주말 오전에 근처 카페에서 교환해요', NULL, 100
) m
WHERE @r2_empty AND (m.message_type = 'TEXT' OR @demo_img IS NOT NULL);

-- 방 3: pickittest1(판매자) ↔ pickittest4(구매자)
INSERT INTO chat_messages (room_id, sender_id, message_type, content, image_url, created_at)
SELECT @r3, m.sender_id, 'TEXT', m.content, NULL, DATE_SUB(@demo_now, INTERVAL m.minutes_ago MINUTE)
FROM (
    SELECT @u4 AS sender_id, '포토카드 교환 원해요! 스티커 세트 있어요.' AS content, 88 AS minutes_ago
    UNION ALL SELECT @u1, '안녕하세요! 스티커 세트 몇 장 구성인가요?', 80
    UNION ALL SELECT @u4, '6장 한 세트예요', 75
    UNION ALL SELECT @u1, '좋아요, 오늘 저녁 출구 앞에서 가능하세요?', 60
) m
WHERE @r3_empty;

COMMIT;

-- 결과 확인
SELECT @demo_ready AS accounts_ready, @popup_id AS popup_id, @p1 AS post1, @p2 AS post2, @p3 AS post3,
       @r1 AS room1, @r2 AS room2, @r3 AS room3;
SELECT r.room_id, r.post_id, r.seller_id, r.buyer_id, COUNT(m.message_id) AS message_count
FROM chat_rooms r LEFT JOIN chat_messages m ON m.room_id = r.room_id
WHERE r.room_id IN (@r1, @r2, @r3)
GROUP BY r.room_id, r.post_id, r.seller_id, r.buyer_id
ORDER BY r.room_id;
