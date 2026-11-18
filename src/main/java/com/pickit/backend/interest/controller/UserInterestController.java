package com.pickit.backend.interest.controller;

import com.pickit.backend.common.ApiResponse;
import com.pickit.backend.interest.dto.request.InterestUpdateRequest;
import com.pickit.backend.interest.dto.response.InterestResponse;
import com.pickit.backend.interest.service.UserInterestService;
import com.pickit.backend.verification.auth.VerificationUserResolver;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInterestController {

    private final UserInterestService userInterestService;
    private final VerificationUserResolver userResolver;

    public UserInterestController(UserInterestService userInterestService,
            VerificationUserResolver userResolver) {
        this.userInterestService = userInterestService;
        this.userResolver = userResolver;
    }

    @GetMapping("/api/me/interests")
    public ApiResponse<InterestResponse> getInterests(Principal principal) {
        return ApiResponse.ok(userInterestService.getInterests(userResolver.requireUserId(principal)));
    }

    @PutMapping("/api/me/interests")
    public ApiResponse<InterestResponse> updateInterests(Principal principal,
            @Valid @RequestBody InterestUpdateRequest request) {
        return ApiResponse.ok(
                userInterestService.updateInterests(userResolver.requireUserId(principal), request.categoryIds()));
    }
}
