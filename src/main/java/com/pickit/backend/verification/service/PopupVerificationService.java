package com.pickit.backend.verification.service;

import com.pickit.backend.popup.dto.response.NearbyPopupResponse;
import com.pickit.backend.popup.entity.Popup;
import com.pickit.backend.popup.entity.PopupStatus;
import com.pickit.backend.popup.repository.NearbyPopupProjection;
import com.pickit.backend.popup.repository.PopupRepository;
import com.pickit.backend.popup.service.PopupLikeService;
import com.pickit.backend.popup.service.PopupService;
import com.pickit.backend.verification.dto.request.LocationRequest;
import com.pickit.backend.verification.dto.response.LocationStatusResponse;
import com.pickit.backend.verification.dto.response.PopupVerificationItemResponse;
import com.pickit.backend.verification.dto.response.VerificationResponse;
import com.pickit.backend.verification.entity.PopupVerification;
import com.pickit.backend.verification.entity.PresenceStatus;
import com.pickit.backend.verification.entity.UserLocationVerification;
import com.pickit.backend.verification.repository.PopupVerificationRepository;
import com.pickit.backend.verification.repository.UserLocationVerificationRepository;
import com.pickit.backend.verification.repository.VerificationUserStore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PopupVerificationService {

    private final PopupVerificationRepository verificationRepository;
    private final VerificationUserStore userStore;
    private final PopupRepository popupRepository;
    private final PopupService popupService;
    private final PopupLikeService popupLikeService;
    private final UserLocationVerificationRepository locationVerificationRepository;
    private final Clock clock;

    public PopupVerificationService(PopupVerificationRepository verificationRepository,
            VerificationUserStore userStore, PopupRepository popupRepository, PopupService popupService,
            PopupLikeService popupLikeService, UserLocationVerificationRepository locationVerificationRepository,
            @Qualifier("verificationClock") Clock clock) {
        this.verificationRepository = verificationRepository;
        this.userStore = userStore;
        this.popupRepository = popupRepository;
        this.popupService = popupService;
        this.popupLikeService = popupLikeService;
        this.locationVerificationRepository = locationVerificationRepository;
        this.clock = clock;
    }

    public NearbyPopupResponse preview(Long userId, LocationRequest request) {
        userStore.requireUser(userId, false);
        LocationSample sample = validateLocation(request, clock.instant());
        return popupService.getNearbyPopups(sample.lat(), sample.lng(), VerificationPolicy.RADIUS_METERS);
    }

    @Transactional
    public VerificationResponse verify(Long userId, LocationRequest request) {
        // 사용자별 요청을 직렬화한 뒤 현재 시각을 잡아 잠금 대기 중 오래된 좌표도 거른다.
        userStore.requireUser(userId, true);
        Instant instant = clock.instant();
        LocationSample sample = validateLocation(request, instant);
        LocalDateTime now = databaseTime(instant);
        List<PopupVerification> current = verificationRepository.findLatestByUserId(userId);
        rejectOutOfOrder(current, sample);

        // 위치 측정이 정상이면 반경 안에 팝업이 없어도 앱 전체 위치 인증 완료로 기록한다.
        // 이때는 새 팝업 인증 없이 200으로 응답하며 verifiedCount가 0일 수 있다.
        recordLocationVerification(userId, now);
        List<Long> popupIds = popupRepository.findNearbyPopups(
                sample.lat(), sample.lng(), VerificationPolicy.RADIUS_METERS
        ).stream().map(NearbyPopupProjection::getPopupId).toList();

        applyLocation(current, sample, now);
        Map<Long, PopupVerification> previous = current.stream().collect(Collectors.toMap(
                verification -> verification.getPopup().getPopupId(), Function.identity()));
        for (Popup popup : popupRepository.findAllById(popupIds)) {
            PopupVerification existing = previous.get(popup.getPopupId());
            if (existing == null || !existing.isVerifiedAt(now)) {
                // 재인증은 새 기록으로 남겨 기존 인증 및 이탈 이력을 보존한다.
                verificationRepository.save(new PopupVerification(userId, popup, sample.lat(), sample.lng(),
                        now, sample.measuredAt(), sample.measuredAt().plus(VerificationPolicy.VALIDITY)));
            }
        }
        verificationRepository.flush();
        return snapshot(userId, verificationRepository.findLatestByUserId(userId), now, true);
    }

    @Transactional
    public VerificationResponse updateLocation(Long userId, LocationRequest request) {
        userStore.requireUser(userId, true);
        Instant instant = clock.instant();
        LocationSample sample = validateLocation(request, instant);
        LocalDateTime now = databaseTime(instant);
        List<PopupVerification> current = verificationRepository.findLatestByUserId(userId);
        rejectOutOfOrder(current, sample);
        applyLocation(current, sample, now);
        return snapshot(userId, current, now, true);
    }

    public VerificationResponse getCurrent(Long userId, boolean includeInactive) {
        userStore.requireUser(userId, false);
        return snapshot(userId, verificationRepository.findLatestByUserId(userId), databaseTime(clock.instant()),
                includeInactive);
    }

    /** 한 번이라도 위치 인증(확인 버튼)을 완료했는지. 팝업별 유효기간과 달리 만료되지 않는다. */
    public LocationStatusResponse getLocationStatus(Long userId) {
        userStore.requireUser(userId, false);
        return locationVerificationRepository.findByUserId(userId)
                .map(record -> new LocationStatusResponse(true,
                        offset(record.getFirstVerifiedAt()), offset(record.getLastVerifiedAt())))
                .orElseGet(() -> new LocationStatusResponse(false, null, null));
    }

    private void recordLocationVerification(Long userId, LocalDateTime now) {
        // verify가 회원 행을 잠근 뒤 호출하므로 같은 회원의 동시 요청이 행을 두 번 만들지 않는다.
        locationVerificationRepository.findByUserId(userId).ifPresentOrElse(
                record -> record.verifyAgainAt(now),
                () -> locationVerificationRepository.save(new UserLocationVerification(userId, now)));
    }

    /** 다른 모듈이 서버에서 사용자·팝업별 현재 인증 상태를 확인하는 연동 지점. */
    public PopupVerificationItemResponse getPresence(Long userId, Long popupId) {
        LocalDateTime now = databaseTime(clock.instant());
        return verificationRepository.findFirstByUserIdAndPopup_PopupIdOrderByVerificationIdDesc(userId, popupId)
                .map(verification -> toItem(verification, now, popupLikeService.countLikes(List.of(popupId)),
                        popupLikeService.isLiked(userId, popupId) ? Set.of(popupId) : Set.of()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "인증 기록이 없습니다."));
    }

    private LocationSample validateLocation(LocationRequest request, Instant now) {
        if (request.accuracyMeters().compareTo(BigDecimal.valueOf(VerificationPolicy.MAX_ACCURACY_METERS)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "위치 정확도가 낮습니다. 다시 측정해주세요.");
        }
        if (request.measuredAt().isAfter(now.plus(VerificationPolicy.MAX_CLOCK_SKEW))
                || request.measuredAt().isBefore(now.minus(VerificationPolicy.MAX_LOCATION_AGE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "1분 이내에 측정한 최신 위치가 필요합니다.");
        }
        return new LocationSample(request.lat().setScale(7, RoundingMode.HALF_UP),
                request.lng().setScale(7, RoundingMode.HALF_UP), databaseTime(request.measuredAt()));
    }

    private void rejectOutOfOrder(List<PopupVerification> current, LocationSample sample) {
        for (PopupVerification verification : current) {
            LocalDateTime last = verification.getLastLocationAt();
            if (last == null) {
                last = verification.getVerifiedAt();
            }
            boolean older = sample.measuredAt().isBefore(last);
            boolean conflicting = sample.measuredAt().isEqual(last)
                    && verification.getLastLat() != null && verification.getLastLng() != null
                    && (sample.lat().compareTo(verification.getLastLat()) != 0
                        || sample.lng().compareTo(verification.getLastLng()) != 0);
            if (older || conflicting) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 처리한 위치보다 최신 위치를 보내주세요.");
            }
        }
    }

    private void applyLocation(List<PopupVerification> current, LocationSample sample, LocalDateTime now) {
        for (PopupVerification verification : current) {
            if (verification.getLastLocationAt() != null
                    && !sample.measuredAt().isAfter(verification.getLastLocationAt())) {
                continue; // 같은 측정값을 재전송해도 유효시간이나 이탈 시각을 변경하지 않는다.
            }
            boolean wasVerified = verification.isVerifiedAt(now);
            Popup popup = verification.getPopup();
            double distance = GeoDistance.meters(sample.lat(), sample.lng(),
                    popup.getLatitude(), popup.getLongitude());
            verification.recordLocation(sample.lat(), sample.lng(), sample.measuredAt());
            if (distance > VerificationPolicy.RADIUS_METERS) {
                verification.leaveAt(now);
            } else if (wasVerified) {
                verification.extendUntil(sample.measuredAt().plus(VerificationPolicy.VALIDITY));
            }
            // 만료되었거나 이미 이탈한 인증은 좌표만 갱신하고, 확인 버튼으로만 재인증한다.
        }
    }

    private VerificationResponse snapshot(Long userId, List<PopupVerification> verifications, LocalDateTime now,
            boolean includeInactive) {
        Map<Long, Long> likeCounts = popupLikeService.countLikes(verifications.stream()
                .map(verification -> verification.getPopup().getPopupId())
                .toList());
        // 내 현장 팝업 카드의 찜 표시용. 회원이 찜한 팝업 ID를 한 번에 읽는다.
        Set<Long> likedPopupIds = verifications.isEmpty() ? Set.of() : popupLikeService.findLikedPopupIds(userId);
        List<PopupVerificationItemResponse> items = verifications.stream()
                .map(verification -> toItem(verification, now, likeCounts, likedPopupIds))
                .filter(item -> includeInactive || item.isVerified())
                .sorted(Comparator.comparing(PopupVerificationItemResponse::isVerified).reversed()
                        .thenComparingDouble(PopupVerificationItemResponse::distanceMeters)
                        .thenComparing(PopupVerificationItemResponse::popupId))
                .toList();
        return new VerificationResponse(items, items.size(),
                items.stream().filter(PopupVerificationItemResponse::isVerified).count(),
                VerificationPolicy.RADIUS_METERS, VerificationPolicy.UPDATE_INTERVAL.toSeconds(), offset(now));
    }

    private PopupVerificationItemResponse toItem(PopupVerification verification, LocalDateTime now,
            Map<Long, Long> likeCounts, Set<Long> likedPopupIds) {
        Popup popup = verification.getPopup();
        PresenceStatus status;
        if (verification.getLeftAt() != null) {
            status = PresenceStatus.LEFT;
        } else {
            status = verification.isVerifiedAt(now) ? PresenceStatus.LIVE : PresenceStatus.EXPIRED;
        }
        BigDecimal lat = verification.getLastLat() == null
                ? verification.getVerifiedLat() : verification.getLastLat();
        BigDecimal lng = verification.getLastLng() == null
                ? verification.getVerifiedLng() : verification.getLastLng();
        return new PopupVerificationItemResponse(verification.getVerificationId(), popup.getPopupId(),
                popup.getName(), popup.getThumbnailUrl(), popup.getDistrict(), popup.getAddress(),
                popup.getStartDate(), popup.getEndDate(),
                PopupStatus.fromDates(popup.getStartDate(), popup.getEndDate(), now.toLocalDate()),
                likeCounts.getOrDefault(popup.getPopupId(), 0L),
                likedPopupIds.contains(popup.getPopupId()),
                GeoDistance.meters(lat, lng, popup.getLatitude(), popup.getLongitude()),
                status == PresenceStatus.LIVE, status,
                offset(verification.getVerifiedAt()), offset(verification.getExpiredAt()),
                offset(verification.getLastLocationAt()), offset(verification.getLeftAt()));
    }

    private static LocalDateTime databaseTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, VerificationPolicy.DATABASE_ZONE).truncatedTo(ChronoUnit.MICROS);
    }

    private static OffsetDateTime offset(LocalDateTime time) {
        return time == null ? null : time.atZone(VerificationPolicy.DATABASE_ZONE).toOffsetDateTime();
    }

    private record LocationSample(BigDecimal lat, BigDecimal lng, LocalDateTime measuredAt) {
    }
}
