package com.pickit.backend.popup.dto.response;

import com.pickit.backend.popup.entity.Popup;
import com.pickit.backend.popup.entity.PopupStatus;
import java.time.LocalDate;

public record PopupListItemResponse(
        Long popupId,
        String name,
        String thumbnailUrl,
        String district,
        LocalDate startDate,
        LocalDate endDate,
        PopupStatus status,
        boolean isLiked,
        long likeCount
) {
    public static PopupListItemResponse of(Popup popup, LocalDate today, boolean isLiked, long likeCount) {
        return new PopupListItemResponse(
                popup.getPopupId(),
                popup.getName(),
                popup.getThumbnailUrl(),
                popup.getDistrict(),
                popup.getStartDate(),
                popup.getEndDate(),
                PopupStatus.fromDates(popup.getStartDate(), popup.getEndDate(), today),
                isLiked,
                likeCount
        );
    }
}
