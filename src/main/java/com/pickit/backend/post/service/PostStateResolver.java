package com.pickit.backend.post.service;

import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.verification.entity.PopupVerification;
import com.pickit.backend.verification.repository.PopupVerificationRepository;
import com.pickit.backend.verification.service.VerificationPolicy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 교환글이 현장/일반 중 어디에 속하는지(tab)와 카드에 띄울 태그(tag)를 계산한다.
 * 현장: 작성자가 인증을 유지 중(LIVE)이거나 이탈 후 30분 이내(JUST_NOW). 그 뒤엔 일반으로 넘어가며 되돌아가지 않는다.
 */
@Component
public class PostStateResolver {

    public static final String TAB_ONSITE = "ONSITE";
    public static final String TAB_GENERAL = "GENERAL";
    public static final Duration JUST_NOW_WINDOW = Duration.ofMinutes(30);

    private final PopupVerificationRepository verificationRepository;
    private final Duration newWindow;

    public PostStateResolver(PopupVerificationRepository verificationRepository,
                             @Value("${pickit.post.new-hours:24}") long newHours) {
        this.verificationRepository = verificationRepository;
        this.newWindow = Duration.ofHours(newHours);
    }

    public record PostState(String tab, String tag) {
    }

    /** 위치 인증 시각과 같은 기준(Asia/Seoul)의 현재 시각. */
    public static LocalDateTime now() {
        return LocalDateTime.now(VerificationPolicy.DATABASE_ZONE);
    }

    /** 현장 탭 판단 기준 시각: 이 시각 이후에 인증이 끝났거나 아직 유효하면 현장. */
    public static LocalDateTime onsiteThreshold(LocalDateTime now) {
        return now.minus(JUST_NOW_WINDOW);
    }

    public Map<Long, PostState> resolveAll(Collection<ExchangePost> posts) {
        List<Long> verificationIds = posts.stream()
                .map(ExchangePost::getVerificationId).filter(Objects::nonNull).distinct().toList();
        Map<Long, PopupVerification> verifications = new HashMap<>();
        if (!verificationIds.isEmpty()) {
            verificationRepository.findAllById(verificationIds)
                    .forEach(v -> verifications.put(v.getVerificationId(), v));
        }
        LocalDateTime now = now();
        Map<Long, PostState> result = new HashMap<>();
        for (ExchangePost post : posts) {
            PopupVerification v = post.getVerificationId() == null ? null : verifications.get(post.getVerificationId());
            result.put(post.getPostId(), resolve(post, v, now));
        }
        return result;
    }


    public PostState resolve(ExchangePost post) {
        PopupVerification v = post.getVerificationId() == null ? null
                : verificationRepository.findById(post.getVerificationId()).orElse(null);
        return resolve(post, v, now());
    }

    private PostState resolve(ExchangePost post, PopupVerification v, LocalDateTime now) {
        String location = null; // LIVE, JUST_NOW, null(일반)
        if (v != null) {
            if (v.isVerifiedAt(now)) {
                location = "LIVE";
            } else {
                LocalDateTime endedAt = v.getLeftAt() != null ? v.getLeftAt() : v.getExpiredAt();
                if (endedAt.isAfter(onsiteThreshold(now))) {
                    location = "JUST_NOW";
                }
            }
        }
        String tab = location != null ? TAB_ONSITE : TAB_GENERAL;

        // 교환 상태(예약 중/교환 완료)가 있으면 그 태그만, 없으면 위치 태그, 일반이면 New
        String tag;
        if (!"OPEN".equals(post.getStatus())) {
            tag = post.getStatus();
        } else if (location != null) {
            tag = location;
        } else if (post.getCreatedAt() != null && post.getCreatedAt().isAfter(LocalDateTime.now().minus(newWindow))) {
            tag = "NEW";
        } else {
            tag = null;
        }
        return new PostState(tab, tag);
    }
}
