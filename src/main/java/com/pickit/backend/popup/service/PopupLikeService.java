package com.pickit.backend.popup.service;

import com.pickit.backend.popup.dto.response.PopupLikeResponse;
import com.pickit.backend.popup.dto.response.PopupListItemResponse;
import com.pickit.backend.popup.entity.Popup;
import com.pickit.backend.popup.entity.PopupLike;
import com.pickit.backend.popup.repository.PopupLikeCount;
import com.pickit.backend.popup.repository.PopupLikeRepository;
import com.pickit.backend.popup.repository.PopupRepository;
import com.pickit.backend.verification.repository.VerificationUserStore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PopupLikeService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final PopupLikeRepository popupLikeRepository;
    private final PopupRepository popupRepository;
    private final VerificationUserStore userStore;

    public PopupLikeService(PopupLikeRepository popupLikeRepository, PopupRepository popupRepository,
            VerificationUserStore userStore) {
        this.popupLikeRepository = popupLikeRepository;
        this.popupRepository = popupRepository;
        this.userStore = userStore;
    }

    /** 같은 팝업을 다시 찜해도 찜 기록은 하나만 유지한다. */
    @Transactional
    public PopupLikeResponse like(Long userId, Long popupId) {
        // 회원 행을 잠가 같은 회원의 동시 요청을 순서대로 처리한다.
        userStore.requireUser(userId, true);
        Popup popup = findPopup(popupId);
        if (!popupLikeRepository.existsByUserIdAndPopup_PopupId(userId, popupId)) {
            popupLikeRepository.saveAndFlush(new PopupLike(userId, popup,
                    LocalDateTime.now(SEOUL).truncatedTo(ChronoUnit.SECONDS)));
        }
        return new PopupLikeResponse(popupId, true, popupLikeRepository.countByPopup_PopupId(popupId));
    }

    /** 찜하지 않은 팝업의 취소 요청도 같은 결과를 반환한다. */
    @Transactional
    public PopupLikeResponse unlike(Long userId, Long popupId) {
        userStore.requireUser(userId, true);
        findPopup(popupId);
        popupLikeRepository.deleteByUserIdAndPopupId(userId, popupId);
        return new PopupLikeResponse(popupId, false, popupLikeRepository.countByPopup_PopupId(popupId));
    }

    /** 찜 탭의 찜한 팝업 전체. 카드 형식은 팝업 목록과 같다. */
    public List<PopupListItemResponse> getLikedPopups(Long userId) {
        userStore.requireUser(userId, false);
        LocalDate today = LocalDate.now(SEOUL);
        List<PopupLike> likes = popupLikeRepository.findLikedByUserId(userId, today);
        Map<Long, Long> likeCounts = countLikes(likes.stream().map(like -> like.getPopup().getPopupId()).toList());
        return likes.stream()
                .map(like -> PopupListItemResponse.of(like.getPopup(), today, true,
                        likeCounts.getOrDefault(like.getPopup().getPopupId(), 0L)))
                .toList();
    }

    public Set<Long> findLikedPopupIds(Long userId) {
        return Set.copyOf(popupLikeRepository.findPopupIdsByUserId(userId));
    }

    public boolean isLiked(Long userId, Long popupId) {
        return popupLikeRepository.existsByUserIdAndPopup_PopupId(userId, popupId);
    }

    public long countLikes(Long popupId) {
        return popupLikeRepository.countByPopup_PopupId(popupId);
    }

    /** 팝업 ID별 찜 수를 한 번의 쿼리로 센다. 찜이 없는 팝업은 결과에 없으므로 0으로 본다. */
    public Map<Long, Long> countLikes(Collection<Long> popupIds) {
        if (popupIds.isEmpty()) {
            return Map.of();
        }
        return popupLikeRepository.countByPopupIds(popupIds).stream()
                .collect(Collectors.toMap(PopupLikeCount::getPopupId, PopupLikeCount::getLikeCount));
    }

    private Popup findPopup(Long popupId) {
        return popupRepository.findById(popupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팝업을 찾을 수 없습니다."));
    }
}
