package com.pickit.backend.interest.service;

import com.pickit.backend.category.dto.response.CategoryResponse;
import com.pickit.backend.category.entity.Category;
import com.pickit.backend.category.repository.CategoryRepository;
import com.pickit.backend.interest.dto.response.InterestResponse;
import com.pickit.backend.interest.entity.UserInterest;
import com.pickit.backend.interest.repository.UserInterestRepository;
import com.pickit.backend.verification.repository.VerificationUserStore;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;
    private final CategoryRepository categoryRepository;
    private final VerificationUserStore userStore;

    public UserInterestService(UserInterestRepository userInterestRepository,
            CategoryRepository categoryRepository, VerificationUserStore userStore) {
        this.userInterestRepository = userInterestRepository;
        this.categoryRepository = categoryRepository;
        this.userStore = userStore;
    }

    public InterestResponse getInterests(Long userId) {
        boolean premium = userStore.isPremium(userId, false);
        return toResponse(userInterestRepository.findAllByUserIdWithCategory(userId), maxCount(premium));
    }

    /** 요청한 카테고리 목록으로 관심사 전체를 교체한다. 유지되는 관심사의 기존 행은 그대로 둔다. */
    @Transactional
    public InterestResponse updateInterests(Long userId, Set<Long> categoryIds) {
        // 회원 행을 잠가 같은 회원의 동시 변경을 순서대로 처리한다.
        boolean premium = userStore.isPremium(userId, true);
        int maxCount = maxCount(premium);
        if (categoryIds.size() > maxCount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "관심사는 최대 " + maxCount + "개까지 선택할 수 있습니다.");
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리가 포함되어 있습니다.");
        }

        List<UserInterest> current = userInterestRepository.findAllByUserIdWithCategory(userId);
        userInterestRepository.deleteAll(current.stream()
                .filter(interest -> !categoryIds.contains(interest.getCategory().getCategoryId()))
                .toList());
        Set<Long> kept = current.stream()
                .map(interest -> interest.getCategory().getCategoryId())
                .collect(Collectors.toSet());
        categories.stream()
                .filter(category -> !kept.contains(category.getCategoryId()))
                .sorted((left, right) -> left.getCategoryId().compareTo(right.getCategoryId()))
                .forEach(category -> userInterestRepository.save(new UserInterest(userId, category)));
        userInterestRepository.flush();

        return toResponse(userInterestRepository.findAllByUserIdWithCategory(userId), maxCount);
    }

    /** 교환글 추천 등 다른 모듈이 서버에서 회원의 관심 카테고리 ID를 조회하는 연동 지점. */
    public List<Long> findCategoryIds(Long userId) {
        return userInterestRepository.findCategoryIdsByUserId(userId);
    }

    private int maxCount(boolean premium) {
        return premium ? (int) categoryRepository.count() : InterestPolicy.FREE_MAX_COUNT;
    }

    private InterestResponse toResponse(List<UserInterest> interests, int maxCount) {
        return new InterestResponse(interests.stream()
                .map(interest -> CategoryResponse.from(interest.getCategory()))
                .toList(), maxCount);
    }
}
