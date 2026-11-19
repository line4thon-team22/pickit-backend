-- src/main/resources/data/popups.json의 마지막 두 항목(4호선톤, PICKIT 시연 팝업)을 수동으로 추가합니다.
-- 2026-11-22 4호선톤 현장(서울창업허브 공덕) 시연용입니다. 두 팝업은 같은 좌표라 위치 인증 한 번에 함께 인증됩니다.
-- 좌표는 본관과 별관의 가운데입니다. 두 건물 어디서든 약 96m 안이라 200m 인증 반경에 여유가 있습니다.
-- 기존 데이터는 변경하지 않습니다. 이름·주소·시작일·종료일이 같은 팝업은 건너뛰므로 여러 번 실행해도 됩니다.
-- 썸네일(/images/popups/4thon.jpg, /images/popups/pickit.png)은 앱 이미지 폴더에 들어 있어 배포 후에 보입니다.
USE pickit;
SET NAMES utf8mb4;
SET @popup_import_today = DATE(DATE_ADD(UTC_TIMESTAMP(), INTERVAL 9 HOUR));
START TRANSACTION;
INSERT INTO categories (name) SELECT 'IT/전자제품' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('IT/전자제품'));

-- 2026 4호선톤
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '2026 4호선톤', '동국대 멋쟁이사자처럼이 주관하는 멋쟁이사자처럼 대학 14기 연합 해커톤. 11월 22일(일) 10:00~16:30 서울창업허브 공덕에서 4호선 8개 대학 팀의 서비스를 만나는 현장.', '홍대/신촌', '서울 마포구 백범로31길 21', 37.5470505, 126.9497895, '2026-11-22', '2026-11-22', CASE WHEN '2026-11-22' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/4thon.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('IT/전자제품'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '2026 4호선톤' AND p.address = '서울 마포구 백범로31길 21' AND p.start_date = '2026-11-22' AND p.end_date = '2026-11-22');

-- PICKIT 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'PICKIT 팝업', '위치 기반 팝업 굿즈 현장 교환 및 굿즈 교환 전용 서비스 팝업.', '홍대/신촌', '서울 마포구 백범로31길 21', 37.5470505, 126.9497895, '2026-11-22', '2026-11-22', CASE WHEN '2026-11-22' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/pickit.png'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('IT/전자제품'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'PICKIT 팝업' AND p.address = '서울 마포구 백범로31길 21' AND p.start_date = '2026-11-22' AND p.end_date = '2026-11-22');
COMMIT;
SELECT popup_id, name, district, latitude, longitude, start_date, end_date, thumbnail_url
FROM popups WHERE address = '서울 마포구 백범로31길 21' ORDER BY popup_id;
SELECT COUNT(*) AS popup_count FROM popups;
