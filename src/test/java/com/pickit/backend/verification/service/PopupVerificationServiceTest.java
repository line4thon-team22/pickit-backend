package com.pickit.backend.verification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.pickit.backend.popup.repository.PopupRepository;
import com.pickit.backend.popup.service.PopupLikeService;
import com.pickit.backend.popup.service.PopupService;
import com.pickit.backend.verification.dto.request.LocationRequest;
import com.pickit.backend.verification.dto.response.LocationStatusResponse;
import com.pickit.backend.verification.dto.response.VerificationResponse;
import com.pickit.backend.verification.entity.UserLocationVerification;
import com.pickit.backend.verification.repository.PopupVerificationRepository;
import com.pickit.backend.verification.repository.UserLocationVerificationRepository;
import com.pickit.backend.verification.repository.VerificationUserStore;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

class PopupVerificationServiceTest {

    private static final Long USER_ID = 1L;
    private static final Instant NOW = Instant.parse("2026-11-20T03:00:00Z");
    private static final LocalDateTime NOW_IN_SEOUL = LocalDateTime.of(2026, 11, 20, 12, 0);

    private final PopupVerificationRepository verificationRepository = mock(PopupVerificationRepository.class);
    private final VerificationUserStore userStore = mock(VerificationUserStore.class);
    private final PopupRepository popupRepository = mock(PopupRepository.class);
    private final UserLocationVerificationRepository locationVerificationRepository =
            mock(UserLocationVerificationRepository.class);
    private final PopupVerificationService service = new PopupVerificationService(verificationRepository,
            userStore, popupRepository, mock(PopupService.class), mock(PopupLikeService.class),
            locationVerificationRepository, Clock.fixed(NOW, ZoneOffset.UTC));

    // 반경 안 팝업·기존 인증은 mock 기본값인 빈 목록이다.

    @Test
    void verifyWithoutNearbyPopupsRecordsLocationVerificationAndReturnsEmptyResult() {
        VerificationResponse response = service.verify(USER_ID, location(new BigDecimal("20")));

        assertThat(response.content()).isEmpty();
        assertThat(response.verifiedCount()).isZero();
        ArgumentCaptor<UserLocationVerification> saved = ArgumentCaptor.forClass(UserLocationVerification.class);
        verify(locationVerificationRepository).save(saved.capture());
        assertThat(saved.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getValue().getFirstVerifiedAt()).isEqualTo(NOW_IN_SEOUL);
        assertThat(saved.getValue().getLastVerifiedAt()).isEqualTo(NOW_IN_SEOUL);
    }

    @Test
    void verifyAgainKeepsFirstVerifiedAtAndUpdatesLastVerifiedAt() {
        LocalDateTime first = LocalDateTime.of(2026, 11, 19, 10, 0);
        UserLocationVerification existing = new UserLocationVerification(USER_ID, first);
        when(locationVerificationRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        service.verify(USER_ID, location(new BigDecimal("20")));

        assertThat(existing.getFirstVerifiedAt()).isEqualTo(first);
        assertThat(existing.getLastVerifiedAt()).isEqualTo(NOW_IN_SEOUL);
        verify(locationVerificationRepository, never()).save(any());
    }

    @Test
    void inaccurateLocationIsNotRecorded() {
        assertThatThrownBy(() -> service.verify(USER_ID, location(new BigDecimal("101"))))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(locationVerificationRepository);
    }

    @Test
    void locationStatusIsFalseBeforeFirstVerification() {
        LocationStatusResponse status = service.getLocationStatus(USER_ID);

        assertThat(status.isLocationVerified()).isFalse();
        assertThat(status.firstVerifiedAt()).isNull();
        assertThat(status.lastVerifiedAt()).isNull();
    }

    @Test
    void locationStatusStaysTrueAfterVerificationWithSeoulOffset() {
        UserLocationVerification existing = new UserLocationVerification(USER_ID,
                LocalDateTime.of(2026, 11, 1, 9, 30));
        existing.verifyAgainAt(LocalDateTime.of(2026, 11, 2, 18, 0));
        when(locationVerificationRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        LocationStatusResponse status = service.getLocationStatus(USER_ID);

        assertThat(status.isLocationVerified()).isTrue();
        assertThat(status.firstVerifiedAt()).isEqualTo(OffsetDateTime.parse("2026-11-01T09:30:00+09:00"));
        assertThat(status.lastVerifiedAt()).isEqualTo(OffsetDateTime.parse("2026-11-02T18:00:00+09:00"));
    }

    private static LocationRequest location(BigDecimal accuracyMeters) {
        return new LocationRequest(new BigDecimal("37.5000000"), new BigDecimal("127.0000000"),
                accuracyMeters, NOW);
    }
}
