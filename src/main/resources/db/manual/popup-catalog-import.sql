-- src/main/resources/data/popups.json의 127개 팝업을 수동으로 추가합니다.
-- 기존 데이터는 변경하지 않습니다. 이름·주소·시작일·종료일이 같은 팝업은 건너뜁니다.
-- DB의 popup_id는 자동 발급됩니다. 원본 JSON의 문자열 ID를 DB 기본키로 사용하지 않습니다.
USE pickit;
SET NAMES utf8mb4;
SET @popup_import_today = DATE(DATE_ADD(UTC_TIMESTAMP(), INTERVAL 9 HOUR));
START TRANSACTION;
INSERT INTO categories (name) SELECT '게임' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('게임'));
INSERT INTO categories (name) SELECT '라이프스타일' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('라이프스타일'));
INSERT INTO categories (name) SELECT '뷰티' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('뷰티'));
INSERT INTO categories (name) SELECT '스포츠' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('스포츠'));
INSERT INTO categories (name) SELECT '애니메이션' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('애니메이션'));
INSERT INTO categories (name) SELECT '연예인' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('연예인'));
INSERT INTO categories (name) SELECT '영화/드라마' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('영화/드라마'));
INSERT INTO categories (name) SELECT '웰니스' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('웰니스'));
INSERT INTO categories (name) SELECT '크리에이터' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('크리에이터'));
INSERT INTO categories (name) SELECT '키즈' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('키즈', '키즈/반려동물'));
INSERT INTO categories (name) SELECT '팬시/문구' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('팬시/문구'));
INSERT INTO categories (name) SELECT 'F&B' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('F&B'));
INSERT INTO categories (name) SELECT 'IT/전자제품' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('IT/전자제품'));
INSERT INTO categories (name) SELECT 'K-POP' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name IN ('K-POP', 'K_POP'));

-- 플레이인더박스 성수 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '플레이인더박스 성수 팝업', '포켓몬·원피스·하츠네 미쿠 등 해외 캐릭터 굿즈를 직접 보고 고르는 캐릭터 팝업.', '성수', '서울 성동구 연무장길 27', 37.54345247, 127.05255381, '2026-09-11', '2026-12-31', CASE WHEN '2026-09-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1002935427.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '플레이인더박스 성수 팝업' AND p.address = '서울 성동구 연무장길 27' AND p.start_date = '2026-09-11' AND p.end_date = '2026-12-31');

-- 주술회전 콜라보 카페
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '주술회전 콜라보 카페', '주술회전 시부야사변을 테마로 캐릭터 음료와 콜라보 굿즈를 만나는 카페.', '홍대/신촌', '서울 마포구 양화로 188', 37.55781124, 126.92638407, '2026-09-23', '2026-12-01', CASE WHEN '2026-09-23' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-01' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1026821480.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '주술회전 콜라보 카페' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-09-23' AND p.end_date = '2026-12-01');

-- 렌시스 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '렌시스 팝업', '컬러렌즈 브랜드 렌시스의 울트라·마리나 시리즈를 오프라인으로 만나는 팝업.', '성수', '서울 성동구 연무장길 44', 37.54271927, 127.05407182, '2026-09-29', '2026-11-29', CASE WHEN '2026-09-29' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1035958519.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '렌시스 팝업' AND p.address = '서울 성동구 연무장길 44' AND p.start_date = '2026-09-29' AND p.end_date = '2026-11-29');

-- 캐릭캐릭체인지 X 팝퍼블 콜라보 카페
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '캐릭캐릭체인지 X 팝퍼블 콜라보 카페', '캐릭캐릭체인지의 설레는 세계를 팝퍼블의 공간에서 만나는 애니메이션 콜라보 카페.', '용산', '서울 용산구 한강대로23길 55', 37.52874728, 126.96445587, '2026-08-14', '2026-11-29', CASE WHEN '2026-08-14' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1097293670.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '캐릭캐릭체인지 X 팝퍼블 콜라보 카페' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-08-14' AND p.end_date = '2026-11-29');

-- 한샘홈퍼니싱 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '한샘홈퍼니싱 팝업', '가구와 인테리어 소품을 둘러보며 한샘의 홈스타일링 아이디어를 만나는 리빙 팝업.', '강남/서초', '서울 강남구 도곡로 401', 37.49697932, 127.05325515, '2026-08-25', '2027-01-31', CASE WHEN '2026-08-25' > @popup_import_today THEN 'UPCOMING' WHEN '2027-01-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1119350272.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '한샘홈퍼니싱 팝업' AND p.address = '서울 강남구 도곡로 401' AND p.start_date = '2026-08-25' AND p.end_date = '2027-01-31');

-- 닥터루시마마 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '닥터루시마마 팝업', '아줄렌 앰플·로션·세럼 등 닥터루시마마의 스킨케어 제형을 직접 경험하는 팝업.', '홍대/신촌', '서울 서대문구 연세로 13', 37.55679901, 126.93654255, '2026-10-16', '2026-12-15', CASE WHEN '2026-10-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-15' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1138527995.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '닥터루시마마 팝업' AND p.address = '서울 서대문구 연세로 13' AND p.start_date = '2026-10-16' AND p.end_date = '2026-12-15');

-- 산리오캐릭터즈 X 롯데월드 아쿠아리움 - 머메이드 파티
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '산리오캐릭터즈 X 롯데월드 아쿠아리움 - 머메이드 파티', '마이멜로디·쿠로미·시나모롤 등 산리오 친구들과 아쿠아리움의 머메이드 파티를 만나는 공간.', '잠실', '서울 송파구 올림픽로 300', 37.51348615, 127.10406115, '2026-09-04', '2026-11-29', CASE WHEN '2026-09-04' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1200397763.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('키즈', '키즈/반려동물'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '산리오캐릭터즈 X 롯데월드 아쿠아리움 - 머메이드 파티' AND p.address = '서울 송파구 올림픽로 300' AND p.start_date = '2026-09-04' AND p.end_date = '2026-11-29');

-- 미러로이드 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '미러로이드 팝업', '스마트미러 이미지 컨설팅과 두피·피부 분석으로 나만의 뷰티를 알아보는 체험 팝업.', '홍대/신촌', '서울 서대문구 연세로 13', 37.55668188, 126.93654039, '2026-10-16', '2026-12-15', CASE WHEN '2026-10-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-15' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1237930744.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '미러로이드 팝업' AND p.address = '서울 서대문구 연세로 13' AND p.start_date = '2026-10-16' AND p.end_date = '2026-12-15');

-- 산리오 팝업 - 냥냥 클럽
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '산리오 팝업 - 냥냥 클럽', '챠미키티·초코캣 등 산리오 고양이 캐릭터들의 인형 키링과 문구를 만나는 팝업.', '용산', '서울 용산구 한강대로23길 55', 37.52889758, 126.96390484, '2026-10-30', '2026-11-22', CASE WHEN '2026-10-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1314874643.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '산리오 팝업 - 냥냥 클럽' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-10-30' AND p.end_date = '2026-11-22');

-- 짱구는 못말려 전시 - 놀자! 시공간 대모험
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '짱구는 못말려 전시 - 놀자! 시공간 대모험', '떡잎마을 방범대와 시공간 모험을 떠나는 짱구는 못말려 인터랙티브 체험 전시.', '성수', '서울 성동구 연무장5길 9-29', 37.54442667, 127.05205095, '2026-10-17', '2027-01-24', CASE WHEN '2026-10-17' > @popup_import_today THEN 'UPCOMING' WHEN '2027-01-24' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1315653876.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('키즈', '키즈/반려동물'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '짱구는 못말려 전시 - 놀자! 시공간 대모험' AND p.address = '서울 성동구 연무장5길 9-29' AND p.start_date = '2026-10-17' AND p.end_date = '2027-01-24');

-- 디자인하우스 전시 - 막, 크리에이티브!
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '디자인하우스 전시 - 막, 크리에이티브!', '디자인하우스의 50년을 기념해 크리에이터들이 공간과 음식으로 풀어낸 창작 전시.', '종로/광화문', '서울 중구 동호로 272', 37.5592661759, 127.0036267122, '2026-10-27', '2026-11-25', CASE WHEN '2026-10-27' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1335149124.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('크리에이터'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '디자인하우스 전시 - 막, 크리에이티브!' AND p.address = '서울 중구 동호로 272' AND p.start_date = '2026-10-27' AND p.end_date = '2026-11-25');

-- 후쿠요시 카페 X 사오리 몽블랑 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '후쿠요시 카페 X 사오리 몽블랑 팝업', '홋카이도 토키와야키와 교토 사오리 몽블랑 등 일본 현지 디저트를 만나는 팝업.', '강남/서초', '서울 서초구 신반포로 176', 37.50492551, 127.00414274, '2026-10-20', '2026-11-25', CASE WHEN '2026-10-20' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1363118463.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '후쿠요시 카페 X 사오리 몽블랑 팝업' AND p.address = '서울 서초구 신반포로 176' AND p.start_date = '2026-10-20' AND p.end_date = '2026-11-25');

-- 버터샵 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '버터샵 팝업', '버터샵의 크리스마스 소품과 세서미·엔젤 시리즈를 만나는 캐릭터 라이프스타일 팝업.', '성수', '서울 성동구 연무장길 57', 37.54255809, 127.05580183, '2026-10-30', '2026-11-26', CASE WHEN '2026-10-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-26' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1366840429.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '버터샵 팝업' AND p.address = '서울 성동구 연무장길 57' AND p.start_date = '2026-10-30' AND p.end_date = '2026-11-26');

-- 원피스 무기와라스토어 한국 출장점
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '원피스 무기와라스토어 한국 출장점', '원피스 무기와라스토어의 일본·한국 한정 상품과 특별한 캐릭터 굿즈를 만나는 공간.', '잠실', '서울 송파구 올림픽로 240', 37.51114406, 127.09818768, '2026-07-30', '2026-12-31', CASE WHEN '2026-07-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/136955711.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '원피스 무기와라스토어 한국 출장점' AND p.address = '서울 송파구 올림픽로 240' AND p.start_date = '2026-07-30' AND p.end_date = '2026-12-31');

-- 원오브뎀 팝업 - ONE OF THEM
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '원오브뎀 팝업 - ONE OF THEM', '남성 뷰티 브랜드 원오브뎀의 다양한 제품을 직접 살펴보는 그루밍 팝업.', '홍대/신촌', '서울 서대문구 연세로 13', 37.55673775, 126.93655958, '2026-10-16', '2026-12-15', CASE WHEN '2026-10-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-15' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1399780341.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '원오브뎀 팝업 - ONE OF THEM' AND p.address = '서울 서대문구 연세로 13' AND p.start_date = '2026-10-16' AND p.end_date = '2026-12-15');

-- 주술회전 PLAZA 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '주술회전 PLAZA 팝업', '인기 애니메이션 주술회전의 5주년을 기념하는 PLAZA 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55780673, 126.92637276, '2026-10-23', '2026-11-30', CASE WHEN '2026-10-23' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1405270012.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '주술회전 PLAZA 팝업' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-10-23' AND p.end_date = '2026-11-30');

-- 온그리디언츠 팝업 - 이너글로우 VIP 라운지
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '온그리디언츠 팝업 - 이너글로우 VIP 라운지', '온그리디언츠의 로션과 빛나는 피부를 위한 속광 루틴을 경험하는 뷰티 라운지.', '성수', '서울 성동구 연무장17길 4', 37.5410039, 127.06119347, '2026-06-05', '2027-05-31', CASE WHEN '2026-06-05' > @popup_import_today THEN 'UPCOMING' WHEN '2027-05-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1412338471.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '온그리디언츠 팝업 - 이너글로우 VIP 라운지' AND p.address = '서울 성동구 연무장17길 4' AND p.start_date = '2026-06-05' AND p.end_date = '2027-05-31');

-- 오우라 팝업 롯데백화점잠실점
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '오우라 팝업 롯데백화점잠실점', '수면·활동·스트레스 등 일상의 건강 지표를 살펴보는 오우라 스마트 링을 만나는 팝업.', '잠실', '서울 송파구 올림픽로 240', 37.51210693, 127.09962202, '2026-09-08', '2026-12-31', CASE WHEN '2026-09-08' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/14130080.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('IT/전자제품'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '오우라 팝업 롯데백화점잠실점' AND p.address = '서울 송파구 올림픽로 240' AND p.start_date = '2026-09-08' AND p.end_date = '2026-12-31');

-- 어메이징 디지털 서커스 콜라보 카페
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '어메이징 디지털 서커스 콜라보 카페', '다크 코미디 애니메이션 어메이징 디지털 서커스의 세계를 만나는 콜라보 카페.', '잠실', '서울 송파구 송파대로 521', 37.51165091, 127.10082374, '2026-11-13', '2026-12-08', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-08' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1424669911.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '어메이징 디지털 서커스 콜라보 카페' AND p.address = '서울 송파구 송파대로 521' AND p.start_date = '2026-11-13' AND p.end_date = '2026-12-08');

-- 진격의 거인 팝업 @홍대
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '진격의 거인 팝업 @홍대', '진격의 거인 The Final Season의 세계를 만나는 애니메이션 팝업스토어.', '홍대/신촌', '서울 마포구 양화로 188', 37.55783377, 126.92639536, '2026-11-12', '2026-12-03', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-03' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1431486605.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '진격의 거인 팝업 @홍대' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-12' AND p.end_date = '2026-12-03');

-- 꿈나라 별나라 마루 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '꿈나라 별나라 마루 팝업스토어', '야광별 가득한 꿈나라에서 마루와 토순이의 여정을 만나는 마루는 강쥐 팝업.', '종로/광화문', '서울 종로구 종로54길 26-3', 37.57120505, 127.01390967, '2026-10-30', '2026-11-29', CASE WHEN '2026-10-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1463078478.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '꿈나라 별나라 마루 팝업스토어' AND p.address = '서울 종로구 종로54길 26-3' AND p.start_date = '2026-10-30' AND p.end_date = '2026-11-29');

-- 악마에 입문했습니다! 이루마 군 페어 @잠실
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '악마에 입문했습니다! 이루마 군 페어 @잠실', '이루마 군 마피아 스핀오프의 신규 일러스트를 담은 클럽 데빌 오리지널 굿즈 페어.', '잠실', '서울 송파구 올림픽로 240', 37.51075258, 127.09763861, '2026-11-21', '2026-12-06', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-06' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1499167082.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '악마에 입문했습니다! 이루마 군 페어 @잠실' AND p.address = '서울 송파구 올림픽로 240' AND p.start_date = '2026-11-21' AND p.end_date = '2026-12-06');

-- 프로세카 X 애니플러스 콜라보 카페 @합정
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '프로세카 X 애니플러스 콜라보 카페 @합정', '프로젝트 세카이 유닛별 음료·디저트와 캐릭터 굿즈를 만나는 애니플러스 콜라보 카페.', '홍대/신촌', '서울 마포구 월드컵로3길 14', 37.55101211, 126.91163949, '2026-10-08', '2026-11-22', CASE WHEN '2026-10-08' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1507041969.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '프로세카 X 애니플러스 콜라보 카페 @합정' AND p.address = '서울 마포구 월드컵로3길 14' AND p.start_date = '2026-10-08' AND p.end_date = '2026-11-22');

-- 더정 우롱티프로젝트 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '더정 우롱티프로젝트 팝업', '우롱티 전문 브랜드 더정의 향긋하고 다양한 차 음료를 만나는 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55774554, 126.92649844, '2026-09-21', '2026-12-17', CASE WHEN '2026-09-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-17' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1514311967.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '더정 우롱티프로젝트 팝업' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-09-21' AND p.end_date = '2026-12-17');

-- 페이커 기념우표 기획전시관
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '페이커 기념우표 기획전시관', '페이커의 기념우표와 유니폼·소장품을 보고 영광의 순간과 게임 체험을 즐기는 전시.', '종로/광화문', '서울 중구 소공로 70', 37.56159901, 126.9821692, '2026-10-10', '2027-01-31', CASE WHEN '2026-10-10' > @popup_import_today THEN 'UPCOMING' WHEN '2027-01-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/151572907.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '페이커 기념우표 기획전시관' AND p.address = '서울 중구 소공로 70' AND p.start_date = '2026-10-10' AND p.end_date = '2027-01-31');

-- 토리든 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '토리든 팝업', '흡수 폭포 포토존·체험 게임·피부 진단으로 토리든의 뷰티 세계를 경험하는 팝업.', '성수', '서울 성동구 성수이로7가길 17', 37.54182311, 127.05528757, '2026-07-15', '2026-12-31', CASE WHEN '2026-07-15' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1519636400.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '토리든 팝업' AND p.address = '서울 성동구 성수이로7가길 17' AND p.start_date = '2026-07-15' AND p.end_date = '2026-12-31');

-- 프라포투투 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '프라포투투 팝업', '자연을 존중하는 방식으로 완성한 프라포투투의 파인 프래그런스를 경험하는 향 팝업.', '성수', '서울 성동구 성수이로 77', 37.54199397, 127.05599603, '2026-03-06', '2026-11-30', CASE WHEN '2026-03-06' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1536335294.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '프라포투투 팝업' AND p.address = '서울 성동구 성수이로 77' AND p.start_date = '2026-03-06' AND p.end_date = '2026-11-30');

-- 퀴진케이 팝업 - 빌롱(Belong)
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '퀴진케이 팝업 - 빌롱(Belong)', '제철 식재료와 티룸 감성을 바탕으로 한식 코스 요리를 선보이는 퀴진케이 팝업 레스토랑.', '강남/서초', '서울 강남구 삼성로85길 34', 37.50469226, 127.05452279, '2026-10-07', '2027-04-06', CASE WHEN '2026-10-07' > @popup_import_today THEN 'UPCOMING' WHEN '2027-04-06' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/154472833.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '퀴진케이 팝업 - 빌롱(Belong)' AND p.address = '서울 강남구 삼성로85길 34' AND p.start_date = '2026-10-07' AND p.end_date = '2027-04-06');

-- 산나비 X 애니플러스 콜라보 카페 @합정
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '산나비 X 애니플러스 콜라보 카페 @합정', '산나비의 3주년을 기념해 게임의 세계를 함께 즐기는 애니플러스 콜라보 카페.', '홍대/신촌', '서울 마포구 월드컵로3길 14', 37.55100761, 126.91164855, '2026-11-05', '2026-12-20', CASE WHEN '2026-11-05' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-20' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1545501318.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '산나비 X 애니플러스 콜라보 카페 @합정' AND p.address = '서울 마포구 월드컵로3길 14' AND p.start_date = '2026-11-05' AND p.end_date = '2026-12-20');

-- 타네무라 아리나 30주년 기념전
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '타네무라 아리나 30주년 기념전', '달빛천사와 신풍괴도 잔느의 작가 타네무라 아리나의 작품과 신규 일러스트를 만나는 기념전.', '홍대/신촌', '서울 마포구 양화로 188', 37.55780223, 126.92638974, '2026-10-03', '2026-12-06', CASE WHEN '2026-10-03' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-06' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1558662438.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('크리에이터'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '타네무라 아리나 30주년 기념전' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-10-03' AND p.end_date = '2026-12-06');

-- PEACH-PIT 25주년 기념 특별전
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'PEACH-PIT 25주년 기념 특별전', '로젠 메이든·캐릭캐릭체인지 등 PEACH-PIT의 작품과 캐릭터 원화를 만나는 기념전.', '용산', '서울 용산구 한강대로23길 55', 37.52893819, 126.9641424, '2026-10-24', '2026-11-29', CASE WHEN '2026-10-24' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1561681004.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('크리에이터'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'PEACH-PIT 25주년 기념 특별전' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-10-24' AND p.end_date = '2026-11-29');

-- 롱테이크 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '롱테이크 팝업', '향마다 다르게 꾸민 공간에서 롱테이크의 감각적인 향을 직접 경험하는 팝업.', '성수', '서울 성동구 아차산로 103', 37.54504844, 127.055815, '2026-10-01', '2026-12-31', CASE WHEN '2026-10-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1566223773.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '롱테이크 팝업' AND p.address = '서울 성동구 아차산로 103' AND p.start_date = '2026-10-01' AND p.end_date = '2026-12-31');

-- 시오팡야 영등포 타임스퀘어점
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '시오팡야 영등포 타임스퀘어점', '다양한 맛의 소금빵을 골라 포장해 즐기는 시오팡야 베이커리.', '여의도', '서울 영등포구 영중로 15', 37.51714621, 126.90359733, '2026-10-01', '2026-11-30', CASE WHEN '2026-10-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/157080280.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '시오팡야 영등포 타임스퀘어점' AND p.address = '서울 영등포구 영중로 15' AND p.start_date = '2026-10-01' AND p.end_date = '2026-11-30');

-- 52TOYS 팝업 <용산아이파크>
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '52TOYS 팝업 <용산아이파크>', '52TOYS와 디즈니·짱구 등 인기 캐릭터의 굿즈와 블라인드박스를 만나는 팝업.', '용산', '서울 용산구 한강대로23길 55', 37.52887963, 126.96414809, '2026-10-09', '2026-12-31', CASE WHEN '2026-10-09' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1630594585.png'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '52TOYS 팝업 <용산아이파크>' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-10-09' AND p.end_date = '2026-12-31');

-- <재혼황후 : 황궁으로의 초대> 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '<재혼황후 : 황궁으로의 초대> 팝업', '재혼황후의 나비에와 하인레이가 기다리는 황궁 세계로 초대하는 웹툰 팝업.', '서울 전체', '서울 금천구 디지털로 185', 37.47818746, 126.88715469, '2026-10-30', '2026-12-24', CASE WHEN '2026-10-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-24' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1633144172.png'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '<재혼황후 : 황궁으로의 초대> 팝업' AND p.address = '서울 금천구 디지털로 185' AND p.start_date = '2026-10-30' AND p.end_date = '2026-12-24');

-- 타네무라 아리나 30주년 X 애니메이트 카페 @홍대
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '타네무라 아리나 30주년 X 애니메이트 카페 @홍대', '타네무라 아리나의 데뷔 30주년 일러스트와 콜라보 메뉴·굿즈를 즐기는 카페.', '홍대/신촌', '서울 마포구 양화로 188', 37.55780223, 126.9263954, '2026-11-20', '2026-12-15', CASE WHEN '2026-11-20' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-15' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1718330688.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('크리에이터'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '타네무라 아리나 30주년 X 애니메이트 카페 @홍대' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-20' AND p.end_date = '2026-12-15');

-- 메르세데스 벤츠 전시
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '메르세데스 벤츠 전시', '메르세데스 벤츠의 첫 공장에서 영감을 받은 공간에서 브랜드의 과거·현재·미래를 만나는 전시.', '성수', '서울 성동구 연무장길 73', 37.5420977, 127.05763683, '2026-05-19', '2027-02-14', CASE WHEN '2026-05-19' > @popup_import_today THEN 'UPCOMING' WHEN '2027-02-14' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1726047978.png'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '메르세데스 벤츠 전시' AND p.address = '서울 성동구 연무장길 73' AND p.start_date = '2026-05-19' AND p.end_date = '2027-02-14');

-- 룰루레몬 신세계백화점 강남 맨즈 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '룰루레몬 신세계백화점 강남 맨즈 팝업스토어', '룰루레몬의 남성 퍼포먼스 웨어와 일상용 라이프스타일 컬렉션을 만나는 맨즈 팝업.', '강남/서초', '서울 서초구 신반포로 176', 37.503765, 127.00467084, '2026-09-01', '2026-11-30', CASE WHEN '2026-09-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1749786445.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('스포츠'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '룰루레몬 신세계백화점 강남 맨즈 팝업스토어' AND p.address = '서울 서초구 신반포로 176' AND p.start_date = '2026-09-01' AND p.end_date = '2026-11-30');

-- 한주다이어리 X 둘리와 친구들 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '한주다이어리 X 둘리와 친구들 팝업', '한주다이어리의 감성으로 재해석한 둘리와 친구들을 일상 문구 굿즈로 만나는 팝업.', '종로/광화문', '서울 중구 충무로4길 2', 37.56493654, 126.99298802, '2026-09-23', '2026-12-31', CASE WHEN '2026-09-23' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/183760628.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '한주다이어리 X 둘리와 친구들 팝업' AND p.address = '서울 중구 충무로4길 2' AND p.start_date = '2026-09-23' AND p.end_date = '2026-12-31');

-- T1 X 서울스카이 전시
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'T1 X 서울스카이 전시', 'e스포츠 팀 T1을 테마로 한 전시와 서울스카이 한정 굿즈를 만나는 특별 전시.', '잠실', '서울 송파구 올림픽로 300', 37.51365294, 127.10393697, '2026-09-11', '2026-11-29', CASE WHEN '2026-09-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1868812672.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'T1 X 서울스카이 전시' AND p.address = '서울 송파구 올림픽로 300' AND p.start_date = '2026-09-11' AND p.end_date = '2026-11-29');

-- 씨비스 팝업 - The Land
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '씨비스 팝업 - The Land', '씨비스의 The Land 컬렉션과 아카이브 라인을 함께 만나는 브랜드 첫 팝업.', '성수', '서울 성동구 연무장길 73', 37.54202378, 127.05770467, '2026-11-05', '2026-11-27', CASE WHEN '2026-11-05' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-27' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1903511086.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '씨비스 팝업 - The Land' AND p.address = '서울 성동구 연무장길 73' AND p.start_date = '2026-11-05' AND p.end_date = '2026-11-27');

-- 랩투마스 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '랩투마스 팝업', '헤어부터 스킨케어까지 랩투마스의 향과 텍스처를 오감으로 경험하는 뷰티·웰니스 팝업.', '잠실', '서울 송파구 올림픽로 300', 37.51382345, 127.10471201, '2026-10-04', '2026-12-17', CASE WHEN '2026-10-04' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-17' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1920657343.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '랩투마스 팝업' AND p.address = '서울 송파구 올림픽로 300' AND p.start_date = '2026-10-04' AND p.end_date = '2026-12-17');

-- 섭듀드 한남 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '섭듀드 한남 팝업스토어', '후드와 니트·데님 등 섭듀드의 캐주얼 의류와 액세서리를 직접 만나는 팝업.', '용산', '서울 용산구 이태원로 232', 37.53561465, 126.99947275, '2026-10-03', '2026-12-31', CASE WHEN '2026-10-03' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1933620071.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '섭듀드 한남 팝업스토어' AND p.address = '서울 용산구 이태원로 232' AND p.start_date = '2026-10-03' AND p.end_date = '2026-12-31');

-- 신라면 분식 더 팩토리
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '신라면 분식 더 팩토리', '신라면 스페셜 메뉴와 해외 라면·굿즈를 즐기며 새로운 라면을 경험하는 브랜드 체험 공간.', '성수', '서울 성동구 성수일로4길 52', 37.54249211, 127.05430589, '2026-06-16', '2026-11-30', CASE WHEN '2026-06-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/193909986.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '신라면 분식 더 팩토리' AND p.address = '서울 성동구 성수일로4길 52' AND p.start_date = '2026-06-16' AND p.end_date = '2026-11-30');

-- 연도문구 팝업 - 우주정거장점
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '연도문구 팝업 - 우주정거장점', '우주정거장 테마 속에서 치이카와·담곰이·다마고치 등 캐릭터 문구와 소품을 탐험하는 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.5578518, 126.92640666, '2026-09-11', '2026-12-06', CASE WHEN '2026-09-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-06' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1948681209.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '연도문구 팝업 - 우주정거장점' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-09-11' AND p.end_date = '2026-12-06');

-- 플라스틱프로덕트 MPa BOOKSHOP
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '플라스틱프로덕트 MPa BOOKSHOP', '플라스틱프로덕트가 수집해 온 국내외 아트북과 브랜드에 영감을 준 서적을 소개하는 임시 서점.', '강남/서초', '서울 강남구 압구정로80길 40', 37.52469017, 127.04332986, '2026-10-08', '2026-12-10', CASE WHEN '2026-10-08' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-10' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/1967191119.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '플라스틱프로덕트 MPa BOOKSHOP' AND p.address = '서울 강남구 압구정로80길 40' AND p.start_date = '2026-10-08' AND p.end_date = '2026-12-10');

-- 모던하우스 팝업 - 매지컬 크리스마스
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '모던하우스 팝업 - 매지컬 크리스마스', '마법 같은 크리스마스 분위기 속에서 비밀 미션과 시크릿 드로우를 즐기는 모던하우스 팝업.', '여의도', '서울 영등포구 국제금융로 10', 37.52500555, 126.9259129, '2026-11-04', '2026-12-25', CASE WHEN '2026-11-04' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/2096339681.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '모던하우스 팝업 - 매지컬 크리스마스' AND p.address = '서울 영등포구 국제금융로 10' AND p.start_date = '2026-11-04' AND p.end_date = '2026-12-25');

-- LG전자 브랜드 전시 - THE FIRST : Origins
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'LG전자 브랜드 전시 - THE FIRST : Origins', 'LG전자의 시작과 브랜드 헤리티지를 돌아보는 플래그십 D5 전시.', '강남/서초', '서울 강남구 도산대로 403', 37.52367513, 127.03964478, '2026-04-02', '2026-12-31', CASE WHEN '2026-04-02' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/2135987447.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('IT/전자제품'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'LG전자 브랜드 전시 - THE FIRST : Origins' AND p.address = '서울 강남구 도산대로 403' AND p.start_date = '2026-04-02' AND p.end_date = '2026-12-31');

-- 도쿄 리벤저스 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '도쿄 리벤저스 팝업', '도쿄 리벤저스의 세계관과 신규·서울 한정 굿즈를 만나는 아시아 투어 팝업.', '홍대/신촌', '서울 마포구 신촌로6길 22', 37.55640777, 126.92981465, '2026-11-13', '2026-11-22', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/226226347.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '도쿄 리벤저스 팝업' AND p.address = '서울 마포구 신촌로6길 22' AND p.start_date = '2026-11-13' AND p.end_date = '2026-11-22');

-- 더현대 서울 크리스마스 빌리지 - Christmas in New York City
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '더현대 서울 크리스마스 빌리지 - Christmas in New York City', '뉴욕의 브라운스톤 거리와 마켓을 재현해 크리스마스 겨울 풍경을 즐기는 빌리지.', '여의도', '서울 영등포구 여의대로 108', 37.52582707, 126.92852536, '2026-11-10', '2026-12-31', CASE WHEN '2026-11-10' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/272240355.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '더현대 서울 크리스마스 빌리지 - Christmas in New York City' AND p.address = '서울 영등포구 여의대로 108' AND p.start_date = '2026-11-10' AND p.end_date = '2026-12-31');

-- 우주이스케이프
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '우주이스케이프', '방탈출과 돌발 미니게임·코인마켓을 함께 즐기는 파티형 방탈출 체험 공간.', '서울 전체', '서울 광진구 아차산로51길 74-1', 37.5403097, 127.08514686, '2026-09-26', '2027-03-25', CASE WHEN '2026-09-26' > @popup_import_today THEN 'UPCOMING' WHEN '2027-03-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/276835515.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '우주이스케이프' AND p.address = '서울 광진구 아차산로51길 74-1' AND p.start_date = '2026-09-26' AND p.end_date = '2027-03-25');

-- 탄산마그네슘 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '탄산마그네슘 팝업', '그래픽 롱슬리브·아우터·팬츠 등 탄산마그네슘의 스트리트 아이템을 직접 입어보는 팝업.', '여의도', '서울 영등포구 영중로 15', 37.51706969, 126.90367661, '2026-09-17', '2026-11-25', CASE WHEN '2026-09-17' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/359164717.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '탄산마그네슘 팝업' AND p.address = '서울 영등포구 영중로 15' AND p.start_date = '2026-09-17' AND p.end_date = '2026-11-25');

-- 자아 팝업 한남점
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '자아 팝업 한남점', '도시 여성의 하루를 위한 자아의 캐주얼 웨어를 자유롭게 레이어링하고 스타일링하는 팝업.', '용산', '서울 용산구 이태원로 234-1', 37.53580476, 126.99967188, '2026-09-04', '2026-11-30', CASE WHEN '2026-09-04' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/36996743.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '자아 팝업 한남점' AND p.address = '서울 용산구 이태원로 234-1' AND p.start_date = '2026-09-04' AND p.end_date = '2026-11-30');

-- 악마에 입문했습니다! 이루마 군 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '악마에 입문했습니다! 이루마 군 팝업', '이루마 군 마피아 스핀오프의 신규 일러스트 굿즈를 만나는 클럽 데빌 홍대 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55779775, 126.92643501, '2026-11-21', '2026-12-06', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-06' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/379124017.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '악마에 입문했습니다! 이루마 군 팝업' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-21' AND p.end_date = '2026-12-06');

-- 비에스래빗 언주 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '비에스래빗 언주 팝업', '재킷·팬츠·장갑 등 비에스래빗의 겨울 보드 스타일을 직접 살펴보는 팝업.', '강남/서초', '서울 강남구 논현로 637', 37.51038581, 127.03212529, '2026-08-20', '2027-02-28', CASE WHEN '2026-08-20' > @popup_import_today THEN 'UPCOMING' WHEN '2027-02-28' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/449207873.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('스포츠'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '비에스래빗 언주 팝업' AND p.address = '서울 강남구 논현로 637' AND p.start_date = '2026-08-20' AND p.end_date = '2027-02-28');

-- 카드캡터 사쿠라 콜라보 카페
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '카드캡터 사쿠라 콜라보 카페', '사쿠라와 토모요의 티파티를 테마로 캐릭터 음료와 일본 오리지널 굿즈를 만나는 콜라보 카페.', '성수', '서울 성동구 서울숲2길 43', 37.54647992, 127.04346581, '2026-11-11', '2026-11-29', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/474286873.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '카드캡터 사쿠라 콜라보 카페' AND p.address = '서울 성동구 서울숲2길 43' AND p.start_date = '2026-11-11' AND p.end_date = '2026-11-29');

-- 데카트론 강남 윈터 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '데카트론 강남 윈터 팝업', '스키·스노보드 의류와 장비를 만나고 스키 에르그 챌린지를 즐기는 데카트론 윈터 팝업.', '강남/서초', '서울 서초구 강남대로 455', 37.50285973, 127.02454365, '2026-10-30', '2026-11-30', CASE WHEN '2026-10-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/4866702.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('스포츠'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '데카트론 강남 윈터 팝업' AND p.address = '서울 서초구 강남대로 455' AND p.start_date = '2026-10-30' AND p.end_date = '2026-11-30');

-- 프롬퓨처 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '프롬퓨처 팝업', '다채로운 컬러와 여유로운 감성을 담은 프롬퓨처의 가을·겨울 컬렉션을 만나는 팝업.', '강남/서초', '서울 강남구 압구정로 407', 37.52805069, 127.04142326, '2026-09-15', '2026-12-31', CASE WHEN '2026-09-15' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/490055217.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '프롬퓨처 팝업' AND p.address = '서울 강남구 압구정로 407' AND p.start_date = '2026-09-15' AND p.end_date = '2026-12-31');

-- 발루토커피 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '발루토커피 팝업', '발루토커피의 부드럽고 진한 커피 한 잔을 일상 속에서 즐기는 커피 팝업.', '강남/서초', '서울 강남구 언주로174길 17', 37.52845359, 127.03542181, '2025-12-19', '2026-12-18', CASE WHEN '2025-12-19' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-18' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/550414826.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '발루토커피 팝업' AND p.address = '서울 강남구 언주로174길 17' AND p.start_date = '2025-12-19' AND p.end_date = '2026-12-18');

-- 가치서울 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '가치서울 팝업', '한국의 전통적인 맛과 가치를 함께 나누는 디저트 브랜드 가치서울을 만나는 팝업.', '성수', '서울 성동구 뚝섬로9길 16', 37.53963404, 127.05645252, '2025-10-14', '2026-12-31', CASE WHEN '2025-10-14' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/552500788.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '가치서울 팝업' AND p.address = '서울 성동구 뚝섬로9길 16' AND p.start_date = '2025-10-14' AND p.end_date = '2026-12-31');

-- 픽스피자 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '픽스피자 팝업스토어', '취향에 따라 여러 맛을 골라 갓 구운 한 손 피자로 즐기는 픽스피자 팝업.', '성수', '서울 성동구 연무장길 5-9', 37.54424901, 127.05036479, '2026-10-22', '2026-11-22', CASE WHEN '2026-10-22' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/572304196.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '픽스피자 팝업스토어' AND p.address = '서울 성동구 연무장길 5-9' AND p.start_date = '2026-10-22' AND p.end_date = '2026-11-22');

-- 화산귀환 팝업 - 피어나자, 화산에서
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '화산귀환 팝업 - 피어나자, 화산에서', '화산귀환의 새 키링·스노우볼·키캡과 한정 굿즈를 함께 만나는 웹툰 팝업.', '용산', '서울 용산구 한강대로23길 55', 37.52895624, 126.9642329, '2026-11-12', '2026-12-09', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-09' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/588567475.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '화산귀환 팝업 - 피어나자, 화산에서' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-11-12' AND p.end_date = '2026-12-09');

-- 앤커 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '앤커 팝업', '앤커의 고속 충전 기기부터 오디오·스마트홈 제품까지 직접 경험하는 테크 팝업.', '서울 전체', '서울 광진구 능동로 92', 37.53913758, 127.07144322, '2026-09-18', '2026-12-17', CASE WHEN '2026-09-18' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-17' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/597146541.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('IT/전자제품'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '앤커 팝업' AND p.address = '서울 광진구 능동로 92' AND p.start_date = '2026-09-18' AND p.end_date = '2026-12-17');

-- 안전가옥 팝업 - 아무렇게나 읽는 게 어때서!
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '안전가옥 팝업 - 아무렇게나 읽는 게 어때서!', '나만의 방식으로 책 읽기를 즐기며 안전가옥의 신간과 새로운 굿즈를 만나는 팝업.', '용산', '서울 용산구 한강대로23길 55', 37.5288481, 126.96415942, '2026-11-12', '2026-11-25', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/651359688.png'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '안전가옥 팝업 - 아무렇게나 읽는 게 어때서!' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-11-12' AND p.end_date = '2026-11-25');

-- 오란고교 사교클럽 콜라보 카페
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '오란고교 사교클럽 콜라보 카페', '오란고교 사교클럽의 캐릭터와 작품 분위기를 카페에서 만나는 애니메이션 콜라보.', '홍대/신촌', '서울 서대문구 연세로 13', 37.55676293, 126.93646902, '2026-10-02', '2026-11-30', CASE WHEN '2026-10-02' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/69258808.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '오란고교 사교클럽 콜라보 카페' AND p.address = '서울 서대문구 연세로 13' AND p.start_date = '2026-10-02' AND p.end_date = '2026-11-30');

-- 디페스타 더 오리진 전시
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '디페스타 더 오리진 전시', 'K팝 아티스트들의 사진·영상·그림·손편지로 꿈과 열정을 만나는 디페스타 전시.', '여의도', '서울 영등포구 국제금융로 10', 37.52517614, 126.92496812, '2026-10-23', '2026-11-30', CASE WHEN '2026-10-23' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/74388761.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('K-POP', 'K_POP'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '디페스타 더 오리진 전시' AND p.address = '서울 영등포구 국제금융로 10' AND p.start_date = '2026-10-23' AND p.end_date = '2026-11-30');

-- 버켄스탁 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '버켄스탁 팝업', '버켄스탁의 다양한 풋웨어 디자인과 착화감을 직접 살펴보는 브랜드 팝업.', '용산', '서울 용산구 이태원로54길 32', 37.53701569, 127.00172886, '2026-03-06', '2026-11-30', CASE WHEN '2026-03-06' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/759710373.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '버켄스탁 팝업' AND p.address = '서울 용산구 이태원로54길 32' AND p.start_date = '2026-03-06' AND p.end_date = '2026-11-30');

-- ONGBROS 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'ONGBROS 팝업', '한국 전통 장류의 깊은 맛을 현대적인 스낵으로 풀어낸 옹브로스의 K-스낵 팝업.', '종로/광화문', '서울 종로구 종로32길 21', 37.57009133, 127.00131307, '2026-11-01', '2026-11-21', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-21' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/834512650.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('F&B'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'ONGBROS 팝업' AND p.address = '서울 종로구 종로32길 21' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-21');

-- 렌즈미 압구정도산점 팝업 이벤트
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '렌즈미 압구정도산점 팝업 이벤트', '렌즈미 제품의 착용감을 확인하고 나에게 맞는 렌즈를 찾는 매장 팝업 이벤트.', '강남/서초', '서울 강남구 선릉로155길 29', 37.52567707, 127.03714683, '2026-10-24', '2026-11-30', CASE WHEN '2026-10-24' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/866769859.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '렌즈미 압구정도산점 팝업 이벤트' AND p.address = '서울 강남구 선릉로155길 29' AND p.start_date = '2026-10-24' AND p.end_date = '2026-11-30');

-- 메디큐브 X 포에버체리 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '메디큐브 X 포에버체리 팝업', '포에버체리의 사랑스러운 핑크빛 감성을 담은 메디큐브 제품과 공간을 만나는 팝업.', '종로/광화문', '서울 중구 퇴계로 77', 37.56050404, 126.9805555, '2026-07-24', '2026-11-30', CASE WHEN '2026-07-24' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/896055957.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '메디큐브 X 포에버체리 팝업' AND p.address = '서울 중구 퇴계로 77' AND p.start_date = '2026-07-24' AND p.end_date = '2026-11-30');

-- 두잉왓 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '두잉왓 팝업', '피부 톤 케어와 헤어 스타일링 제품을 테스트하며 두잉왓의 남성 그루밍을 경험하는 팝업.', '홍대/신촌', '서울 서대문구 연세로 13', 37.55677197, 126.93653692, '2026-10-16', '2026-12-15', CASE WHEN '2026-10-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-15' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/931204281.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '두잉왓 팝업' AND p.address = '서울 서대문구 연세로 13' AND p.start_date = '2026-10-16' AND p.end_date = '2026-12-15');

-- 덴티스테 단독 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '덴티스테 단독 팝업', '덴티스테 미니무비를 테마로 상쾌한 자신감을 경험하고 이벤트를 즐기는 팝업.', '홍대/신촌', '서울 마포구 독막로15길 12', 37.54836617, 126.92230366, '2026-11-06', '2026-11-29', CASE WHEN '2026-11-06' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/951995888.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('웰니스'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '덴티스테 단독 팝업' AND p.address = '서울 마포구 독막로15길 12' AND p.start_date = '2026-11-06' AND p.end_date = '2026-11-29');

-- 불가리 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '불가리 팝업', '세르펜티·디바스 드림·비제로원과 워치 등 불가리의 대표 컬렉션을 만나는 주얼리 팝업.', '강남/서초', '서울 강남구 테헤란로 517', 37.5086154, 127.05976455, '2026-03-22', '2026-12-31', CASE WHEN '2026-03-22' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/961654704.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '불가리 팝업' AND p.address = '서울 강남구 테헤란로 517' AND p.start_date = '2026-03-22' AND p.end_date = '2026-12-31');

-- 제이닷트리 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '제이닷트리 팝업', '크리스마스 트리와 특별한 오너먼트가 꾸민 영화 같은 공간에서 사진을 남기는 팝업.', '종로/광화문', '서울 종로구 사직로2길 12', 37.57286906, 126.96412361, '2026-10-24', '2026-12-31', CASE WHEN '2026-10-24' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/971055821.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '제이닷트리 팝업' AND p.address = '서울 종로구 사직로2길 12' AND p.start_date = '2026-10-24' AND p.end_date = '2026-12-31');

-- <듄: 파트 3> 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '<듄: 파트 3> 팝업', '몰입형 LED와 모래벌레 라이딩 포토존으로 듄의 세계를 미리 경험하는 영화 팝업.', '강남/서초', '서울 강남구 테헤란로 지하 538', 37.50882274, 127.06302321, '2026-10-30', '2026-11-22', CASE WHEN '2026-10-30' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/972065341.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('영화/드라마'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '<듄: 파트 3> 팝업' AND p.address = '서울 강남구 테헤란로 지하 538' AND p.start_date = '2026-10-30' AND p.end_date = '2026-11-22');

-- 삐아 성수 플래그십 - 11월 프로모션
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '삐아 성수 플래그십 - 11월 프로모션', '삐아의 메이크업 제품과 시즌별 이벤트를 만나는 성수 플래그십 프로모션.', '성수', '서울 성동구 연무장3길 17', 37.5451875, 127.0516875, '2026-11-10', '2026-11-30', CASE WHEN '2026-11-10' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17792.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '삐아 성수 플래그십 - 11월 프로모션' AND p.address = '서울 성동구 연무장3길 17' AND p.start_date = '2026-11-10' AND p.end_date = '2026-11-30');

-- 론론 블랙 프라이데이 세일 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '론론 블랙 프라이데이 세일 팝업', '론론과 무드베터·88아카이브 등 여러 패션 브랜드의 시즌 상품을 만나는 세일 팝업.', '성수', '서울 성동구 아차산로5길 24-18', 37.5473125, 127.0533125, '2026-11-05', '2026-12-04', CASE WHEN '2026-11-05' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-04' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17755.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '론론 블랙 프라이데이 세일 팝업' AND p.address = '서울 성동구 아차산로5길 24-18' AND p.start_date = '2026-11-05' AND p.end_date = '2026-12-04');

-- 대원미디어 팝콘D스퀘어 성수 오픈 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '대원미디어 팝콘D스퀘어 성수 오픈 팝업', '이치방쿠지와 산엑스 캐릭터 굿즈·체험 이벤트를 즐기는 팝콘D스퀘어 오픈 팝업.', '성수', '서울 성동구 연무장길 37-29', 37.5436875, 127.0538125, '2026-11-12', '2026-12-20', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-20' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17717.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '대원미디어 팝콘D스퀘어 성수 오픈 팝업' AND p.address = '서울 성동구 연무장길 37-29' AND p.start_date = '2026-11-12' AND p.end_date = '2026-12-20');

-- KUKU Illustration Fair
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'KUKU Illustration Fair', '일곱 작가의 일러스트 굿즈와 라이브 드로잉·타블렛 체험을 만나는 참여형 페어.', '홍대/신촌', '서울 마포구 양화로 188', 37.55781124, 126.92638407, '2026-11-16', '2026-11-22', CASE WHEN '2026-11-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17713.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('크리에이터'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'KUKU Illustration Fair' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-16' AND p.end_date = '2026-11-22');

-- TCL 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'TCL 팝업', 'TCL의 최신 기술을 게임 챌린지와 커스텀 굿즈·체험 프로그램으로 만나는 팝업.', '성수', '서울 성동구 연무장17길 3', 37.5410625, 127.0608125, '2026-11-20', '2026-11-22', CASE WHEN '2026-11-20' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17709.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('IT/전자제품'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'TCL 팝업' AND p.address = '서울 성동구 연무장17길 3' AND p.start_date = '2026-11-20' AND p.end_date = '2026-11-22');

-- 리그 오브 레전드: 와일드 리프트 팝업 - 2026 Wild Rounds: SMASH
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '리그 오브 레전드: 와일드 리프트 팝업 - 2026 Wild Rounds: SMASH', 'K/DA의 무대 준비 여정을 스타일링·게임·퍼포먼스와 스탬프 투어로 즐기는 와일드 리프트 팝업.', '성수', '서울 성동구 연무장15길 11', 37.54282238, 127.05890585, '2026-11-21', '2026-11-22', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17708.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '리그 오브 레전드: 와일드 리프트 팝업 - 2026 Wild Rounds: SMASH' AND p.address = '서울 성동구 연무장15길 11' AND p.start_date = '2026-11-21' AND p.end_date = '2026-11-22');

-- 어그 팝업 - UGG in BUKCHON
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '어그 팝업 - UGG in BUKCHON', '북촌의 한옥·양옥 공간에서 어그 카페와 아티스트 협업·커스터마이징을 즐기는 겨울 팝업.', '종로/광화문', '서울 종로구 북촌로 53', 37.5818125, 126.9846875, '2026-11-13', '2026-11-22', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17704.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '어그 팝업 - UGG in BUKCHON' AND p.address = '서울 종로구 북촌로 53' AND p.start_date = '2026-11-13' AND p.end_date = '2026-11-22');

-- 째피 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '째피 팝업', '째피의 바디워시와 바디로션을 직접 써보며 나에게 맞는 바디케어를 찾는 팝업.', '성수', '서울 성동구 연무장3길 9', 37.54466575, 127.05133483, '2026-11-11', '2026-12-08', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-08' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17706.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '째피 팝업' AND p.address = '서울 성동구 연무장3길 9' AND p.start_date = '2026-11-11' AND p.end_date = '2026-12-08');

-- 온그리디언츠 팝업 - ddalgi HOUSE
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '온그리디언츠 팝업 - ddalgi HOUSE', '온그리디언츠가 꾸민 핑크빛 딸기 세상과 달콤한 상상 속 공간을 만나는 팝업.', '성수', '서울 성동구 연무장길 65', 37.5423125, 127.0568125, '2026-11-13', '2027-02-10', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2027-02-10' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17582.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '온그리디언츠 팝업 - ddalgi HOUSE' AND p.address = '서울 성동구 연무장길 65' AND p.start_date = '2026-11-13' AND p.end_date = '2027-02-10');

-- 충청유람 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '충청유람 팝업', '선비의 멋과 충청 여행을 전통 놀이·자개 거울 꾸미기·유생복 체험으로 즐기는 팝업.', '성수', '서울 성동구 아차산로 120', 37.54391071, 127.05722294, '2026-11-21', '2026-11-22', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17436.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '충청유람 팝업' AND p.address = '서울 성동구 아차산로 120' AND p.start_date = '2026-11-21' AND p.end_date = '2026-11-22');

-- 다마고치 30주년 팝업 - 생일 파티로 놀러 와!
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '다마고치 30주년 팝업 - 생일 파티로 놀러 와!', '다마고치 30번째 생일을 기념해 신제품 체험·미니게임과 특별한 캐릭터 굿즈를 만나는 팝업.', '여의도', '서울 영등포구 여의대로 108', 37.52566471, 126.92822572, '2026-11-20', '2026-12-03', CASE WHEN '2026-11-20' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-03' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17577.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '다마고치 30주년 팝업 - 생일 파티로 놀러 와!' AND p.address = '서울 영등포구 여의대로 108' AND p.start_date = '2026-11-20' AND p.end_date = '2026-12-03');

-- 투니크축제 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '투니크축제 팝업', '투니크의 스페셜 캐릭터 상품과 축제 한정 혜택을 다시 만나는 팝업.', '홍대/신촌', '서울 마포구 양화로 78-17', 37.55129411, 126.91786108, '2026-11-12', '2026-11-30', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17427.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '투니크축제 팝업' AND p.address = '서울 마포구 양화로 78-17' AND p.start_date = '2026-11-12' AND p.end_date = '2026-11-30');

-- FuRyu 누들 스토퍼 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'FuRyu 누들 스토퍼 팝업', 'FuRyu의 누들 스토퍼와 Trio-Try-iT·Bicute 등 다양한 캐릭터 피규어를 만나는 팝업.', '용산', '서울 용산구 한강대로23길 55', 37.52874728, 126.96445587, '2026-11-11', '2026-11-24', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-24' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17264.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'FuRyu 누들 스토퍼 팝업' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-11-11' AND p.end_date = '2026-11-24');

-- 윈디위티 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '윈디위티 팝업', '윈디위티가 그려온 무드와 겨울 컬렉션을 직접 경험하는 첫 오프라인 팝업.', '성수', '서울 성동구 성수이로18길 8', 37.5423125, 127.0578125, '2026-11-20', '2026-11-22', CASE WHEN '2026-11-20' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17573.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '윈디위티 팝업' AND p.address = '서울 성동구 성수이로18길 8' AND p.start_date = '2026-11-20' AND p.end_date = '2026-11-22');

-- 몽돌 더현대 아카이브 세일
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '몽돌 더현대 아카이브 세일', '여러 시즌에 걸친 몽돌의 아카이브 의류를 한자리에서 만나는 특별 세일 팝업.', '여의도', '서울 영등포구 여의대로 108', 37.52566471, 126.92822572, '2026-11-19', '2026-11-25', CASE WHEN '2026-11-19' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15423.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '몽돌 더현대 아카이브 세일' AND p.address = '서울 영등포구 여의대로 108' AND p.start_date = '2026-11-19' AND p.end_date = '2026-11-25');

-- 에이티즈 팝업 - GOLDEN HOUR : Part.6
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '에이티즈 팝업 - GOLDEN HOUR : Part.6', '에이티즈 GOLDEN HOUR : Part.6의 공식 MD와 포토존으로 골든아워를 기록하는 팝업.', '강남/서초', '서울 서초구 신반포로 176', 37.50492551, 127.00414274, '2026-11-13', '2026-11-22', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17494.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('K-POP', 'K_POP'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '에이티즈 팝업 - GOLDEN HOUR : Part.6' AND p.address = '서울 서초구 신반포로 176' AND p.start_date = '2026-11-13' AND p.end_date = '2026-11-22');

-- 브레이 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '브레이 팝업', '씬 글로우 틴트를 비롯한 브레이의 메이크업 제품과 감각적인 뷰티 무드를 경험하는 팝업.', '성수', '서울 성동구 성수이로 88', 37.5426875, 127.0566875, '2026-11-12', '2026-11-25', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17451.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '브레이 팝업' AND p.address = '서울 성동구 성수이로 88' AND p.start_date = '2026-11-12' AND p.end_date = '2026-11-25');

-- 가누다&까르마&호텔인홈 롯데백화점 노원점 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '가누다&까르마&호텔인홈 롯데백화점 노원점 팝업스토어', '가누다 베개·까르마 매트리스와 호텔인홈의 생활용품을 체험하며 수면 취향을 찾는 팝업.', '서울 전체', '서울 노원구 동일로 1414', 37.6551875, 127.0610625, '2026-11-13', '2026-11-26', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-26' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17434.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('웰니스'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '가누다&까르마&호텔인홈 롯데백화점 노원점 팝업스토어' AND p.address = '서울 노원구 동일로 1414' AND p.start_date = '2026-11-13' AND p.end_date = '2026-11-26');

-- 산산기어 아카이브 세일 - 불완전한 피난처 SHELTER
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '산산기어 아카이브 세일 - 불완전한 피난처 SHELTER', '산산기어의 지난 시즌 아카이브 제품과 브랜드의 스타일을 만나는 세일 팝업.', '성수', '서울 성동구 연무장길 35', 37.54310877, 127.05348144, '2026-11-14', '2026-11-22', CASE WHEN '2026-11-14' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17426.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '산산기어 아카이브 세일 - 불완전한 피난처 SHELTER' AND p.address = '서울 성동구 연무장길 35' AND p.start_date = '2026-11-14' AND p.end_date = '2026-11-22');

-- 투에이엔 모브 크러쉬 컬렉션 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '투에이엔 모브 크러쉬 컬렉션 팝업', '투에이엔 모브 크러쉬 컬렉션의 색감과 메이크업 무드를 직접 만나는 뷰티 팝업.', '성수', '서울 성동구 연무장길 87', 37.5416875, 127.0589375, '2026-11-11', '2026-12-07', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-07' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17425.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '투에이엔 모브 크러쉬 컬렉션 팝업' AND p.address = '서울 성동구 연무장길 87' AND p.start_date = '2026-11-11' AND p.end_date = '2026-12-07');

-- 라이프앤피시스 Give a Record 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '라이프앤피시스 Give a Record 팝업', '문구류로 오늘의 기록을 쓰고 꾸미며 방문객들의 이야기를 함께 완성하는 팝업.', '성수', '서울 성동구 연무장17길 10', 37.54156686, 127.06150963, '2026-11-10', '2026-11-30', CASE WHEN '2026-11-10' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17267.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '라이프앤피시스 Give a Record 팝업' AND p.address = '서울 성동구 연무장17길 10' AND p.start_date = '2026-11-10' AND p.end_date = '2026-11-30');

-- 투모로우바이투게더 PERFECT STORM 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '투모로우바이투게더 PERFECT STORM 팝업', '투모로우바이투게더 PERFECT STORM의 배지·키링·의류 등 앨범 머치를 만나는 팝업.', '용산', '서울 용산구 한강대로 42', 37.5245625, 126.9641875, '2026-11-17', '2026-11-22', CASE WHEN '2026-11-17' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17430.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('K-POP', 'K_POP'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '투모로우바이투게더 PERFECT STORM 팝업' AND p.address = '서울 용산구 한강대로 42' AND p.start_date = '2026-11-17' AND p.end_date = '2026-11-22');

-- 비밀의 아이프리 X 산리오캐릭터즈 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '비밀의 아이프리 X 산리오캐릭터즈 팝업', '비밀의 아이프리와 산리오캐릭터즈의 콜라보 한정 상품·게임존을 즐기는 팝업.', '용산', '서울 용산구 한강대로23길 55', 37.52874728, 126.96445587, '2026-11-20', '2026-11-26', CASE WHEN '2026-11-20' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-26' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17431.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '비밀의 아이프리 X 산리오캐릭터즈 팝업' AND p.address = '서울 용산구 한강대로23길 55' AND p.start_date = '2026-11-20' AND p.end_date = '2026-11-26');

-- VDL X 밀로우먼 팝업 - SKIN SILHOUETTE EDITION
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'VDL X 밀로우먼 팝업 - SKIN SILHOUETTE EDITION', '레이스의 섬세한 감성을 메이크업으로 풀어낸 VDL과 밀로우먼의 협업 컬렉션 팝업.', '강남/서초', '서울 서초구 강남대로 429', 37.5008125, 127.0256875, '2026-11-01', '2026-11-28', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-28' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17261.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'VDL X 밀로우먼 팝업 - SKIN SILHOUETTE EDITION' AND p.address = '서울 서초구 강남대로 429' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-28');

-- 에싸 X 홍진경 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '에싸 X 홍진경 팝업', '미러 스틸과 가죽의 대비로 가구와 오브제의 경계를 넘나드는 에싸와 홍진경의 미러 소파 팝업.', '잠실', '서울 송파구 올림픽로 240', 37.51123913, 127.09762227, '2026-11-01', '2026-11-22', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16977.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('연예인'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '에싸 X 홍진경 팝업' AND p.address = '서울 송파구 올림픽로 240' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-22');

-- 앳모어 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '앳모어 팝업', '정제된 아름다움과 시간을 담은 앳모어의 컬렉션을 직접 입어보고 만나는 팝업.', '여의도', '서울 영등포구 여의대로 108', 37.52566471, 126.92822572, '2026-11-19', '2026-11-25', CASE WHEN '2026-11-19' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17247.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '앳모어 팝업' AND p.address = '서울 영등포구 여의대로 108' AND p.start_date = '2026-11-19' AND p.end_date = '2026-11-25');

-- 바우로 코스메틱 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '바우로 코스메틱 팝업', '남성의 일상을 위한 바우로 코스메틱의 그루밍 제품을 직접 살펴보는 뷰티 팝업.', '홍대/신촌', '서울 서대문구 연세로 13', 37.55668188, 126.93654039, '2026-10-16', '2026-12-16', CASE WHEN '2026-10-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-16' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-17246.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '바우로 코스메틱 팝업' AND p.address = '서울 서대문구 연세로 13' AND p.start_date = '2026-10-16' AND p.end_date = '2026-12-16');

-- 주바의 포춘 다락방 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '주바의 포춘 다락방 팝업', '포춘쿠키와 행운의 메시지로 꾸민 공간에서 주바의 신상품·메이드바이 협업 굿즈를 만나는 팝업.', '홍대/신촌', '서울 마포구 동교로 262-6', 37.5628125, 126.9254375, '2026-11-04', '2027-01-03', CASE WHEN '2026-11-04' > @popup_import_today THEN 'UPCOMING' WHEN '2027-01-03' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16776.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '주바의 포춘 다락방 팝업' AND p.address = '서울 마포구 동교로 262-6' AND p.start_date = '2026-11-04' AND p.end_date = '2027-01-03');

-- UGG 성수 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'UGG 성수 팝업', '어그의 슈즈와 따뜻한 홀리데이 무드를 성수에서 직접 경험하는 시즌 팝업.', '성수', '서울 성동구 연무장7길 16', 37.54394866, 127.05509904, '2026-11-05', '2026-12-30', CASE WHEN '2026-11-05' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16610.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'UGG 성수 팝업' AND p.address = '서울 성동구 연무장7길 16' AND p.start_date = '2026-11-05' AND p.end_date = '2026-12-30');

-- 닥터리쥬올 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '닥터리쥬올 팝업', '피부 장벽 체크와 약사 상담으로 닥터리쥬올의 베리어 스킨케어를 경험하는 팝업.', '종로/광화문', '서울 중구 명동10길 36', 37.5623125, 126.9855625, '2026-11-09', '2026-12-31', CASE WHEN '2026-11-09' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16659.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '닥터리쥬올 팝업' AND p.address = '서울 중구 명동10길 36' AND p.start_date = '2026-11-09' AND p.end_date = '2026-12-31');

-- 휘란 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '휘란 팝업', '한국적인 감성을 색으로 풀어낸 휘란의 메이크업 제품과 모던한 아름다움을 만나는 팝업.', '홍대/신촌', '서울 서대문구 신촌로 83', 37.55608324, 126.93584488, '2026-10-16', '2026-12-15', CASE WHEN '2026-10-16' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-15' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16602.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '휘란 팝업' AND p.address = '서울 서대문구 신촌로 83' AND p.start_date = '2026-10-16' AND p.end_date = '2026-12-15');

-- 안녕자두야 자두와 꼬꼬 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '안녕자두야 자두와 꼬꼬 팝업', '자두와 꼬꼬의 인형·커플 키링 등 귀여운 신상 캐릭터 굿즈를 만나는 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55781124, 126.92638407, '2026-11-10', '2026-11-23', CASE WHEN '2026-11-10' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-23' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16597.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '안녕자두야 자두와 꼬꼬 팝업' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-10' AND p.end_date = '2026-11-23');

-- 비디비치 팝업 - LIGHT IN BLACK
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '비디비치 팝업 - LIGHT IN BLACK', '비디비치 블랙 쿠션과 메이크업 헤리티지를 체험하며 나에게 맞는 베이스를 찾는 팝업.', '강남/서초', '서울 강남구 도산대로 442', 37.5234375, 127.0436875, '2026-11-04', '2026-11-27', CASE WHEN '2026-11-04' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-27' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16474.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '비디비치 팝업 - LIGHT IN BLACK' AND p.address = '서울 강남구 도산대로 442' AND p.start_date = '2026-11-04' AND p.end_date = '2026-11-27');

-- Myosi / myosee 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, 'Myosi / myosee 팝업스토어', '고양이의 시선으로 본 도시 일상을 위트 있는 그래픽과 패션 아이템으로 풀어낸 묘시묘시 팝업.', '서울 전체', '서울 성북구 삼양로 41', 37.6068125, 127.0235625, '2026-11-05', '2026-11-29', CASE WHEN '2026-11-05' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16606.png'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = 'Myosi / myosee 팝업스토어' AND p.address = '서울 성북구 삼양로 41' AND p.start_date = '2026-11-05' AND p.end_date = '2026-11-29');

-- 2026 한지도서 특별전
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '2026 한지도서 특별전', '다섯 출판사의 한지도서와 활판 체험으로 전통 종이의 질감·아름다움을 발견하는 특별전.', '종로/광화문', '서울 종로구 종로 1', 37.5709375, 126.97779688, '2026-11-01', '2026-11-30', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16429.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '2026 한지도서 특별전' AND p.address = '서울 종로구 종로 1' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-30');

-- 야마다 군과 Lv999의 사랑을 하다 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '야마다 군과 Lv999의 사랑을 하다 팝업', '야마다와 아카네의 일본 공식 오리지널 굿즈를 만나는 작품의 한국 단독 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55781124, 126.92638407, '2026-11-19', '2026-12-02', CASE WHEN '2026-11-19' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-02' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16257.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '야마다 군과 Lv999의 사랑을 하다 팝업' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-19' AND p.end_date = '2026-12-02');

-- 무민 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '무민 팝업', '보기만 해도 따뜻해지는 무민 골짜기 친구들의 매력을 만나는 캐릭터 팝업.', '잠실', '서울 송파구 올림픽로 300', 37.51348615, 127.10406115, '2026-11-11', '2026-11-24', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-24' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16256.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '무민 팝업' AND p.address = '서울 송파구 올림픽로 300' AND p.start_date = '2026-11-11' AND p.end_date = '2026-11-24');

-- 타이니빌 팝업 - 웰컴 투 타이니빌 슈퍼마켓
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '타이니빌 팝업 - 웰컴 투 타이니빌 슈퍼마켓', '슈퍼마켓 테마 속에서 타이니빌의 햄버거 클리커·키캡·피규어와 뽑기를 즐기는 팝업.', '서울 전체', '서울 강서구 하늘길 38', 37.56331777, 126.80329317, '2026-11-04', '2026-11-30', CASE WHEN '2026-11-04' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16264.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '타이니빌 팝업 - 웰컴 투 타이니빌 슈퍼마켓' AND p.address = '서울 강서구 하늘길 38' AND p.start_date = '2026-11-04' AND p.end_date = '2026-11-30');

-- 아스토믹 팝업 - UNBOXING:최애 집합소
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '아스토믹 팝업 - UNBOXING:최애 집합소', '가챠와 피규어·산리오 굿즈 등 취향에 맞는 캐릭터 컬렉션을 발견하는 아스토믹 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55781124, 126.92638407, '2026-11-02', '2026-11-22', CASE WHEN '2026-11-02' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-22' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16115.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '아스토믹 팝업 - UNBOXING:최애 집합소' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-02' AND p.end_date = '2026-11-22');

-- 상수리나무 아래 X 잊혀진 들판 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '상수리나무 아래 X 잊혀진 들판 팝업', '김수지 작가의 두 로맨스 소설 속 세계관을 전시와 스토어로 만나는 팝업.', '여의도', '서울 영등포구 여의대로 108', 37.52566471, 126.92822572, '2026-11-19', '2026-12-03', CASE WHEN '2026-11-19' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-03' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16210.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '상수리나무 아래 X 잊혀진 들판 팝업' AND p.address = '서울 영등포구 여의대로 108' AND p.start_date = '2026-11-19' AND p.end_date = '2026-12-03');

-- 로봇 태권브이 50주년 팝업 @서울
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '로봇 태권브이 50주년 팝업 @서울', '로봇 태권브이 50주년 피규어·달력과 포토월로 추억을 기념하는 캐릭터 팝업.', '종로/광화문', '서울 중구 한강대로 405', 37.5558125, 126.9704375, '2026-11-21', '2026-11-27', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-27' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15924.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '로봇 태권브이 50주년 팝업 @서울' AND p.address = '서울 중구 한강대로 405' AND p.start_date = '2026-11-21' AND p.end_date = '2026-11-27');

-- 카비시 X 달빛천사 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '카비시 X 달빛천사 팝업', '달빛천사의 추억을 카비시 콜라보 아이템과 특별한 캐릭터 상품으로 만나는 팝업.', '홍대/신촌', '서울 마포구 양화로 188', 37.55781124, 126.92638407, '2026-11-13', '2026-11-26', CASE WHEN '2026-11-13' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-26' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15920.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '카비시 X 달빛천사 팝업' AND p.address = '서울 마포구 양화로 188' AND p.start_date = '2026-11-13' AND p.end_date = '2026-11-26');

-- 메이드바이 X 완소클럽 팝업 - 놀러와! 완소 디저트 타운
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '메이드바이 X 완소클럽 팝업 - 놀러와! 완소 디저트 타운', '디저트를 닮은 완소클럽 친구들과 신상·메이드바이 협업 굿즈를 만나는 귀여운 팝업.', '성수', '서울 성동구 서울숲길 38', 37.54789153, 127.04167754, '2026-11-05', '2027-01-04', CASE WHEN '2026-11-05' > @popup_import_today THEN 'UPCOMING' WHEN '2027-01-04' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15918.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('팬시/문구'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '메이드바이 X 완소클럽 팝업 - 놀러와! 완소 디저트 타운' AND p.address = '서울 성동구 서울숲길 38' AND p.start_date = '2026-11-05' AND p.end_date = '2027-01-04');

-- 닥터엘시아 팝업 - 345 시크릿 프로젝트
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '닥터엘시아 팝업 - 345 시크릿 프로젝트', '비밀스러운 박사의 연구실을 테마로 미션과 이벤트를 즐기는 닥터엘시아 뷰티 팝업.', '성수', '서울 성동구 성수이로 108', 37.5445625, 127.0573125, '2026-11-01', '2026-11-28', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-28' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15915.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '닥터엘시아 팝업 - 345 시크릿 프로젝트' AND p.address = '서울 성동구 성수이로 108' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-28');

-- 블리치 천년혈전편 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '블리치 천년혈전편 팝업', '애니메이션 블리치 천년혈전편의 세계를 브이스퀘어에서 만나는 콜라보 팝업.', '서울 전체', '서울 광진구 아차산로 272', 37.53863855, 127.07271452, '2026-11-11', '2026-11-29', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15908.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('애니메이션'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '블리치 천년혈전편 팝업' AND p.address = '서울 광진구 아차산로 272' AND p.start_date = '2026-11-11' AND p.end_date = '2026-11-29');

-- 에트오소메 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '에트오소메 팝업', '섬세한 디테일과 여리한 실루엣을 담은 에트오소메의 가을·겨울 컬렉션을 만나는 팝업.', '성수', '서울 성동구 성수이로 74', 37.54153341, 127.05630233, '2026-11-01', '2026-11-30', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-30' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15907.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '에트오소메 팝업' AND p.address = '서울 성동구 성수이로 74' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-30');

-- 더마비 X 버터베어 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '더마비 X 버터베어 팝업', '버터베어와 함께 더마비 세라마이드 바디로션의 보습·흡수 체험과 포토존을 즐기는 팝업.', '홍대/신촌', '서울 마포구 홍익로6길 57', 37.5565625, 126.9243125, '2026-11-01', '2026-11-28', CASE WHEN '2026-11-01' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-28' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-15905.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '더마비 X 버터베어 팝업' AND p.address = '서울 마포구 홍익로6길 57' AND p.start_date = '2026-11-01' AND p.end_date = '2026-11-28');

-- 팝콘D스퀘어 TCG 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '팝콘D스퀘어 TCG 팝업', '포켓몬·원피스·유희왕 등 다양한 TCG 카드 상품을 한자리에서 만나는 팝콘D스퀘어 팝업.', '성수', '서울 성동구 연무장길 37-29', 37.5436875, 127.0538125, '2026-11-12', '2026-12-12', CASE WHEN '2026-11-12' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-12' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/popga-16615.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('게임'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '팝콘D스퀘어 TCG 팝업' AND p.address = '서울 성동구 연무장길 37-29' AND p.start_date = '2026-11-12' AND p.end_date = '2026-12-12');

-- 샤넬 홀리데이 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '샤넬 홀리데이 팝업', '알프스 생모리츠의 설산과 산장을 재현한 공간에서 샤넬 향수와 홀리데이 한정 제품을 만나는 팝업.', '성수', '서울 성동구 성수이로7가길 9', 37.5410625, 127.0549375, '2026-11-21', '2026-12-31', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-12-31' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/elle-1912415.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('뷰티'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '샤넬 홀리데이 팝업' AND p.address = '서울 성동구 성수이로7가길 9' AND p.start_date = '2026-11-21' AND p.end_date = '2026-12-31');

-- 성수동 숭숭사 팝업
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '성수동 숭숭사 팝업', '깨달음을 얻은 도숭이와 유혹에 흔들리는 똥숭이를 만나는 불교 콘셉트 캐릭터 전시 팝업.', '성수', '서울 성동구 뚝섬로9길 11-1', 37.5393125, 127.0565625, '2026-11-19', '2026-11-21', CASE WHEN '2026-11-19' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-21' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/wkly-175510079.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('크리에이터'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '성수동 숭숭사 팝업' AND p.address = '서울 성동구 뚝섬로9길 11-1' AND p.start_date = '2026-11-19' AND p.end_date = '2026-11-21');

-- 취향이 머무는 집, K-크래프트 하우스
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '취향이 머무는 집, K-크래프트 하우스', '전통기술과 현대 디자인이 만난 전승공예품을 생활공간 테마로 보고 구매하는 K-크래프트 팝업.', '여의도', '서울 영등포구 여의대로 108', 37.52566471, 126.92822572, '2026-11-21', '2026-11-29', CASE WHEN '2026-11-21' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-29' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/kha-120022.webp'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '취향이 머무는 집, K-크래프트 하우스' AND p.address = '서울 영등포구 여의대로 108' AND p.start_date = '2026-11-21' AND p.end_date = '2026-11-29');

-- 타트라스 팝업스토어
INSERT INTO popups (category_id, name, description, district, address, latitude, longitude, start_date, end_date, status, thumbnail_url)
SELECT c.category_id, '타트라스 팝업스토어', '아트 오브 하모니를 주제로 한 타트라스의 프리미엄 다운재킷과 가을·겨울 컬렉션을 만나는 팝업.', '강남/서초', '서울 서초구 신반포로 176', 37.50492551, 127.00414274, '2026-11-11', '2026-11-25', CASE WHEN '2026-11-11' > @popup_import_today THEN 'UPCOMING' WHEN '2026-11-25' < @popup_import_today THEN 'ENDED' ELSE 'ONGOING' END, '/images/popups/mediadale-295899.jpg'
FROM categories c WHERE c.category_id = (SELECT MIN(category_id) FROM categories WHERE name IN ('라이프스타일'))
AND NOT EXISTS (SELECT 1 FROM popups p WHERE p.name = '타트라스 팝업스토어' AND p.address = '서울 서초구 신반포로 176' AND p.start_date = '2026-11-11' AND p.end_date = '2026-11-25');
COMMIT;
SELECT category_id, name FROM categories ORDER BY category_id;
SELECT COUNT(*) AS popup_count FROM popups;
