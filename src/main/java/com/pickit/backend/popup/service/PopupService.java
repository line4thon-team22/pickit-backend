package com.pickit.backend.popup.service;

import com.pickit.backend.category.service.CategoryService;
import com.pickit.backend.popup.dto.request.PopupListFilter;
import com.pickit.backend.popup.dto.response.NearbyPopupItemResponse;
import com.pickit.backend.popup.dto.response.NearbyPopupResponse;
import com.pickit.backend.popup.dto.response.PopupDetailResponse;
import com.pickit.backend.popup.dto.response.PopupFilterOptionsResponse;
import com.pickit.backend.popup.dto.response.PopupListItemResponse;
import com.pickit.backend.popup.entity.Popup;
import com.pickit.backend.popup.entity.PopupRegion;
import com.pickit.backend.popup.entity.PopupStatus;
import com.pickit.backend.popup.repository.PopupRepository;
import com.pickit.backend.popup.repository.PopupSpecifications;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PopupService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final PopupRepository popupRepository;
    private final CategoryService categoryService;
    private final PopupLikeService popupLikeService;

    public PopupService(PopupRepository popupRepository, CategoryService categoryService,
            PopupLikeService popupLikeService) {
        this.popupRepository = popupRepository;
        this.categoryService = categoryService;
        this.popupLikeService = popupLikeService;
    }

    /**
     * likedPopupIds는 회원이 찜한 팝업 ID 목록이다.
     * 정렬은 내가 찜한 팝업 → 진행 중·진행 전·진행 완료 → 같은 상태 안에서 찜 수 많은 순 → 시작일 최신순이다.
     */
    public List<PopupListItemResponse> getPopups(PopupListFilter filter, Set<Long> likedPopupIds) {
        LocalDate today = LocalDate.now(SEOUL);
        Set<Long> likes = likedPopupIds == null ? Set.of() : Set.copyOf(likedPopupIds);

        List<Popup> popups = popupRepository.findAll(PopupSpecifications.filteredAndOrdered(filter, today, likes));
        Map<Long, Long> likeCounts = popupLikeService.countLikes(popups.stream().map(Popup::getPopupId).toList());

        return popups.stream()
                .map(popup -> PopupListItemResponse.of(popup, today, likes.contains(popup.getPopupId()),
                        likeCounts.getOrDefault(popup.getPopupId(), 0L)))
                .toList();
    }

    public NearbyPopupResponse getNearbyPopups(BigDecimal lat, BigDecimal lng, int radiusMeters) {
        LocalDate today = LocalDate.now(SEOUL);

        List<NearbyPopupItemResponse> items = popupRepository.findNearbyPopups(
                lat, lng, radiusMeters
        ).stream().map(popup -> new NearbyPopupItemResponse(
                popup.getPopupId(),
                popup.getName(),
                popup.getThumbnailUrl(),
                popup.getDistrict(),
                popup.getAddress(),
                popup.getLatitude(),
                popup.getLongitude(),
                popup.getStartDate(),
                popup.getEndDate(),
                PopupStatus.fromDates(popup.getStartDate(), popup.getEndDate(), today),
                popup.getDistanceMeters()
        )).toList();

        return new NearbyPopupResponse(lat, lng, radiusMeters, items, items.size());
    }

    /** userId가 null이면 비로그인으로 보고 찜 여부를 false로 둔다. */
    public PopupDetailResponse getPopupDetail(Long popupId, Long userId) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팝업을 찾을 수 없습니다."));
        boolean isLiked = userId != null && popupLikeService.isLiked(userId, popupId);
        long likeCount = popupLikeService.countLikes(popupId);

        return new PopupDetailResponse(
                popup.getPopupId(),
                popup.getName(),
                popup.getThumbnailUrl(),
                popup.getCategory().getCategoryId(),
                popup.getCategory().getName(),
                popup.getDistrict(),
                popup.getAddress(),
                popup.getStartDate(),
                popup.getEndDate(),
                PopupStatus.fromDates(popup.getStartDate(), popup.getEndDate(), LocalDate.now(SEOUL)),
                popup.getDescription(),
                isLiked,
                likeCount
        );
    }

    public PopupFilterOptionsResponse getFilterOptions() {
        return new PopupFilterOptionsResponse(
                Arrays.stream(PopupStatus.values())
                        .map(status -> new PopupFilterOptionsResponse.StatusOption(status, status.getLabel()))
                        .toList(),
                Arrays.stream(PopupRegion.values())
                        .map(region -> new PopupFilterOptionsResponse.RegionOption(region, region.getLabel()))
                        .toList(),
                // 카테고리는 /api/categories와 같은 목록·이름을 사용한다.
                categoryService.getCategories()
        );
    }
}
