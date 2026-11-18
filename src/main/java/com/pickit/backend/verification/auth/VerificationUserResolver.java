package com.pickit.backend.verification.auth;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** JWT 로그인 필터가 넣어 둔 회원 ID를 꺼낸다. */
@Component
public class VerificationUserResolver {

    public Long requireUserId(Principal principal) {
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof Long userId) {
            return userId;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
}
