package com.pickit.backend.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배포 서버·로드밸런서가 앱이 떠 있는지 확인하는 API. 로그인 없이 호출할 수 있어야 한다.
 * Spring Security가 붙으면 /api/health를 permitAll()에 넣는다.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<String> health() {
        return ApiResponse.ok("UP");
    }
}
