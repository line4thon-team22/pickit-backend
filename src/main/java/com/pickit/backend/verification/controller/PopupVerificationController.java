package com.pickit.backend.verification.controller;

import com.pickit.backend.common.ApiResponse;
import com.pickit.backend.popup.dto.response.NearbyPopupResponse;
import com.pickit.backend.verification.auth.VerificationUserResolver;
import com.pickit.backend.verification.dto.request.LocationRequest;
import com.pickit.backend.verification.dto.response.LocationStatusResponse;
import com.pickit.backend.verification.dto.response.VerificationResponse;
import com.pickit.backend.verification.service.PopupVerificationService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PopupVerificationController {

    private final PopupVerificationService verificationService;
    private final VerificationUserResolver userResolver;

    public PopupVerificationController(PopupVerificationService verificationService,
            VerificationUserResolver userResolver) {
        this.verificationService = verificationService;
        this.userResolver = userResolver;
    }

    @PostMapping("/api/popup-verifications/preview")
    public ApiResponse<NearbyPopupResponse> preview(Principal principal, @Valid @RequestBody LocationRequest request) {
        return ApiResponse.ok(verificationService.preview(userResolver.requireUserId(principal), request));
    }

    @PostMapping("/api/popup-verifications")
    public ApiResponse<VerificationResponse> verify(Principal principal, @Valid @RequestBody LocationRequest request) {
        return ApiResponse.ok(verificationService.verify(userResolver.requireUserId(principal), request));
    }

    @PatchMapping("/api/me/location")
    public ApiResponse<VerificationResponse> updateLocation(Principal principal,
            @Valid @RequestBody LocationRequest request) {
        return ApiResponse.ok(verificationService.updateLocation(userResolver.requireUserId(principal), request));
    }

    @GetMapping("/api/me/popup-verifications")
    public ApiResponse<VerificationResponse> getCurrent(Principal principal,
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive,
            HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        return ApiResponse.ok(verificationService.getCurrent(userResolver.requireUserId(principal), includeInactive));
    }

    /** 헤더 버튼용: 위치 인증을 한 번이라도 완료했는지(false면 "위치 인증", true면 "팝업 확인"). */
    @GetMapping("/api/me/location-status")
    public ApiResponse<LocationStatusResponse> getLocationStatus(Principal principal,
            HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        return ApiResponse.ok(verificationService.getLocationStatus(userResolver.requireUserId(principal)));
    }
}
