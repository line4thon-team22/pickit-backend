package com.pickit.backend.auth.service;

import com.pickit.backend.auth.dto.request.GoogleLoginRequest;
import com.pickit.backend.auth.dto.request.LoginRequest;
import com.pickit.backend.auth.dto.request.LogoutRequest;
import com.pickit.backend.auth.dto.request.RefreshTokenRequest;
import com.pickit.backend.auth.dto.request.SignupRequest;
import com.pickit.backend.auth.dto.response.GoogleLoginResponse;
import com.pickit.backend.auth.dto.response.LoginResponse;
import com.pickit.backend.auth.dto.response.RefreshTokenResponse;
import com.pickit.backend.auth.dto.response.SignupResponse;
import com.pickit.backend.auth.entity.AuthAccount;
import com.pickit.backend.auth.entity.AuthProvider;
import com.pickit.backend.auth.entity.RefreshToken;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.AuthAccountRepository;
import com.pickit.backend.auth.repository.RefreshTokenRepository;
import com.pickit.backend.auth.repository.UserRepository;
import com.pickit.backend.auth.util.JwtProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository,
                       AuthAccountRepository authAccountRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.authAccountRepository = authAccountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public SignupResponse signup(SignupRequest request) {
        if (authAccountRepository.existsByProviderAndProviderUserId(AuthProvider.LOCAL, request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = userRepository.save(new User(request.username()));
        authAccountRepository.save(new AuthAccount(
                user,
                AuthProvider.LOCAL,
                request.email(),
                passwordEncoder.encode(request.password())
        ));

        return new SignupResponse(user.getUserId());
    }

    public LoginResponse login(LoginRequest request) {
        AuthAccount authAccount = authAccountRepository.findByProviderAndProviderUserId(AuthProvider.LOCAL, request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.password(), authAccount.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        User user = authAccount.getUser();
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        String tokenHash = encodeHash(refreshToken);
        refreshTokenRepository.save(new RefreshToken(
                user.getUserId(),
                tokenHash,
                LocalDateTime.now().plusNanos(jwtProvider.getRefreshTokenExpirationMillis() * 1000000L)
        ));

        return new LoginResponse(
                accessToken,
                refreshToken,
                new LoginResponse.UserSummary(
                        user.getUserId(),
                        user.getUsername(),
                        user.getProfileImageUrl(),
                        user.getMannerScore(),
                        user.getIsPremium()
                )
        );
    }

    public GoogleLoginResponse googleLogin(GoogleLoginRequest request) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.idToken();
        Map<String, Object> googleUserInfo;
        try {
            RestClient restClient = RestClient.create();
            googleUserInfo = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 구글 ID 토큰입니다.");
        }

        if (googleUserInfo == null || !googleUserInfo.containsKey("email")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "구글 토큰에서 이메일을 확인할 수 없습니다.");
        }

        String email = (String) googleUserInfo.get("email");
        String name = googleUserInfo.containsKey("name") ? (String) googleUserInfo.get("name") : email.split("@")[0];

        boolean isNewUser = false;
        AuthAccount authAccount = authAccountRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, email).orElse(null);

        User user;
        if (authAccount == null) {
            isNewUser = true;
            user = userRepository.save(new User(name));
            authAccountRepository.save(new AuthAccount(
                    user,
                    AuthProvider.GOOGLE,
                    email,
                    null
            ));
        } else {
            user = authAccount.getUser();
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        String tokenHash = encodeHash(refreshToken);
        refreshTokenRepository.save(new RefreshToken(
                user.getUserId(),
                tokenHash,
                LocalDateTime.now().plusNanos(jwtProvider.getRefreshTokenExpirationMillis() * 1000000L)
        ));

        return new GoogleLoginResponse(accessToken, refreshToken, isNewUser);
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (!jwtProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        String tokenHash = encodeHash(token);
        RefreshToken savedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않거나 폐기된 리프레시 토큰입니다."));

        if (savedToken.getRevokedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이미 폐기된 리프레시 토큰입니다.");
        }

        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다.");
        }

        // Refresh Token Rotation: 기존 토큰 폐기
        savedToken.revoke();
        refreshTokenRepository.save(savedToken);

        // 새 토큰 발급
        Long userId = savedToken.getUserId();
        String newAccessToken = jwtProvider.createAccessToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(userId);

        String newTokenHash = encodeHash(newRefreshToken);
        refreshTokenRepository.save(new RefreshToken(
                userId,
                newTokenHash,
                LocalDateTime.now().plusNanos(jwtProvider.getRefreshTokenExpirationMillis() * 1000000L)
        ));

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(LogoutRequest request) {
        String tokenHash = encodeHash(request.refreshToken());
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);
        });
    }

    private String encodeHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
