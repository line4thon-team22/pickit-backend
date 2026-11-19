package com.pickit.backend.config;

import com.pickit.backend.auth.security.JsonAuthenticationEntryPoint;
import com.pickit.backend.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jsonAuthenticationEntryPoint = jsonAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 브라우저의 CORS 사전 요청(OPTIONS)을 인증 검사 전에 처리한다. 허용 주소는 CorsConfig(CORS_ALLOWED_ORIGINS)를 따른다.
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 배포 확인용 헬스체크와, 400·500 오류 응답을 만드는 /error는 로그인 없이 연다.
                        .requestMatchers("/api/health", "/error").permitAll()
                        // WebSocket 핸드셰이크는 브라우저가 헤더를 못 붙이므로 열어두고, 토큰은 STOMP CONNECT에서 검증한다.
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        // 팝업 조회는 비로그인도 가능 (찜·관심사·위치 인증은 로그인 필요)
                        .requestMatchers(HttpMethod.GET, "/api/popups", "/api/popups/*", "/api/categories",
                                "/images/**").permitAll()
                        // 교환글 목록·상세 조회도 비로그인 가능. 추천 교환글은 관심사 기준이라 로그인 필요
                        .requestMatchers(HttpMethod.GET, "/api/posts/recommended").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*").permitAll()
                        .anyRequest().authenticated()
                )
                // 토큰이 없거나 만료·위조된 요청은 403이 아니라 401로 응답해 프론트가 재로그인·토큰 재발급을 판단하게 한다.
                // 본문은 다른 오류와 같은 { status, message, data } 형식이다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint));

        return http.build();
    }
}
