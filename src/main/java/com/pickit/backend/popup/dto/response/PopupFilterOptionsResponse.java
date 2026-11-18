package com.pickit.backend.popup.dto.response;

import com.pickit.backend.category.dto.response.CategoryResponse;
import com.pickit.backend.popup.entity.PopupRegion;
import com.pickit.backend.popup.entity.PopupStatus;
import java.util.List;

public record PopupFilterOptionsResponse(
        List<StatusOption> statuses,
        List<RegionOption> regions,
        List<CategoryResponse> categories
) {
    public record StatusOption(PopupStatus value, String label) {
    }

    public record RegionOption(PopupRegion value, String label) {
    }
}
