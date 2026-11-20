package com.pickit.backend.popup.controller;

import com.pickit.backend.common.ApiResponse;
import com.pickit.backend.popup.dto.request.PopupListFilter;
import com.pickit.backend.popup.dto.response.PopupDetailResponse;
import com.pickit.backend.popup.dto.response.PopupFilterOptionsResponse;
import com.pickit.backend.popup.dto.response.PopupLikeResponse;
import com.pickit.backend.popup.dto.response.PopupListItemResponse;
import com.pickit.backend.popup.entity.PopupRegion;
import com.pickit.backend.popup.entity.PopupStatus;
import com.pickit.backend.popup.service.PopupLikeService;
import com.pickit.backend.popup.service.PopupService;
import com.pickit.backend.verification.auth.VerificationUserResolver;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/popups")
public class PopupController {

    private final PopupService popupService;
    private final PopupLikeService popupLikeService;
    private final VerificationUserResolver userResolver;

    public PopupController(PopupService popupService, PopupLikeService popupLikeService,
            VerificationUserResolver userResolver) {
        this.popupService = popupService;
        this.popupLikeService = popupLikeService;
        this.userResolver = userResolver;
    }

    @GetMapping
    public ApiResponse<List<PopupListItemResponse>> getPopups(
            Principal principal,
            @RequestParam(name = "q", required = false) @Size(max = 255) String q,
            @RequestParam(name = "statuses", required = false) Set<@NotNull PopupStatus> statuses,
            @RequestParam(name = "regions", required = false) Set<@NotNull PopupRegion> regions,
            @RequestParam(name = "categoryIds", required = false) Set<@NotNull @Positive Long> categoryIds
    ) {
        // 비로그인은 찜 표시·찜 우선 정렬 없이 보여 준다.
        Set<Long> likedPopupIds = principal == null
                ? Set.of()
                : popupLikeService.findLikedPopupIds(userResolver.requireUserId(principal));
        return ApiResponse.ok(popupService.getPopups(new PopupListFilter(q, statuses, regions, categoryIds),
                likedPopupIds));
    }

    @GetMapping("/{popupId}")
    public ApiResponse<PopupDetailResponse> getPopupDetail(
            Principal principal,
            @PathVariable("popupId") @Positive Long popupId
    ) {
        // 목록과 같이 비로그인은 찜 여부 false로 보여 준다.
        Long userId = principal == null ? null : userResolver.requireUserId(principal);
        return ApiResponse.ok(popupService.getPopupDetail(popupId, userId));
    }

    @PutMapping("/{popupId}/like")
    public ApiResponse<PopupLikeResponse> likePopup(
            Principal principal,
            @PathVariable("popupId") @Positive Long popupId
    ) {
        return ApiResponse.ok(popupLikeService.like(userResolver.requireUserId(principal), popupId));
    }

    @DeleteMapping("/{popupId}/like")
    public ApiResponse<PopupLikeResponse> unlikePopup(
            Principal principal,
            @PathVariable("popupId") @Positive Long popupId
    ) {
        return ApiResponse.ok(popupLikeService.unlike(userResolver.requireUserId(principal), popupId));
    }

    @GetMapping("/filters")
    public ApiResponse<PopupFilterOptionsResponse> getFilterOptions() {
        return ApiResponse.ok(popupService.getFilterOptions());
    }
}
