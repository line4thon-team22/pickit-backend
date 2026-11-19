package com.pickit.backend.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인증 실패(토큰 없음·잘못된 토큰·만료된 토큰)를 다른 오류와 같은 { status, message, data } 형식의 401로 응답한다.
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    static final String MESSAGE_NO_TOKEN = "로그인이 필요합니다.";
    static final String MESSAGE_INVALID_TOKEN = "유효하지 않거나 만료된 토큰입니다.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String message = authHeader == null || authHeader.isBlank() ? MESSAGE_NO_TOKEN : MESSAGE_INVALID_TOKEN;

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // message는 위의 고정 문구만 쓰므로 직접 JSON 문자열로 만든다.
        response.getWriter().write(
                "{\"status\":" + HttpStatus.UNAUTHORIZED.value() + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}
