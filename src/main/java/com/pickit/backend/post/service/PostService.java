package com.pickit.backend.post.service;

import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.UserRepository;
import com.pickit.backend.category.entity.Category;
import com.pickit.backend.category.repository.CategoryRepository;
import com.pickit.backend.common.service.FileService;
import com.pickit.backend.interest.repository.UserInterestRepository;
import com.pickit.backend.popup.entity.Popup;
import com.pickit.backend.popup.repository.PopupRepository;
import com.pickit.backend.post.dto.request.CreatePostRequest;
import com.pickit.backend.post.dto.request.UpdatePostRequest;
import com.pickit.backend.post.dto.response.PostDetailResponse;
import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostImage;
import com.pickit.backend.post.repository.ExchangePostRepository;
import com.pickit.backend.post.repository.PostLikeRepository;
import com.pickit.backend.verification.entity.PopupVerification;
import com.pickit.backend.verification.repository.PopupVerificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final ExchangePostRepository exchangePostRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final CategoryRepository categoryRepository;
    private final PopupVerificationRepository popupVerificationRepository;
    private final FileService fileService;

    public PostService(ExchangePostRepository exchangePostRepository,
                       PostLikeRepository postLikeRepository,
                       UserInterestRepository userInterestRepository,
                       UserRepository userRepository,
                       PopupRepository popupRepository,
                       CategoryRepository categoryRepository,
                       PopupVerificationRepository popupVerificationRepository,
                       FileService fileService) {
        this.exchangePostRepository = exchangePostRepository;
        this.postLikeRepository = postLikeRepository;
        this.userInterestRepository = userInterestRepository;
        this.userRepository = userRepository;
        this.popupRepository = popupRepository;
        this.categoryRepository = categoryRepository;
        this.popupVerificationRepository = popupVerificationRepository;
        this.fileService = fileService;
    }

    public PostListResponse getPosts(Long popupId, Long categoryId, String status, String sort, String keyword, int page, int size, Long currentUserId) {
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("RECOMMEND".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<ExchangePost> postPage = exchangePostRepository.findPosts(popupId, categoryId, status, keyword, pageable);

        var content = postPage.getContent().stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && postLikeRepository.existsByUserIdAndExchangePost(currentUserId, post);
                    return PostListResponse.PostSummary.from(post, isLiked);
                })
                .toList();

        return new PostListResponse(content, postPage.getNumber(), postPage.getSize());
    }

    public List<PostListResponse.PostSummary> getRecommendedPosts(Long userId, int size) {
        List<Long> categoryIds = userInterestRepository.findCategoryIdsByUserId(userId);
        Pageable pageable = PageRequest.of(0, size);

        List<ExchangePost> posts;
        if (categoryIds == null || categoryIds.isEmpty()) {
            posts = exchangePostRepository.findByStatusOpen(pageable);
        } else {
            posts = exchangePostRepository.findByCategoryIdInAndStatusOpen(categoryIds, pageable);
            if (posts.isEmpty()) {
                posts = exchangePostRepository.findByStatusOpen(pageable);
            }
        }

        return posts.stream()
                .map(post -> PostListResponse.PostSummary.from(post, false))
                .toList();
    }

    public PostDetailResponse getPostDetail(Long postId, Long currentUserId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        User writer = userRepository.findById(post.getUserId()).orElse(null);
        boolean isLiked = currentUserId != null && postLikeRepository.existsByUserIdAndExchangePost(currentUserId, post);

        return PostDetailResponse.of(post, writer, isLiked);
    }

    @Transactional
    public Long createPost(Long userId, CreatePostRequest request) {
        PopupVerification verification = popupVerificationRepository.findFirstByUserIdAndPopup_PopupIdOrderByVerificationIdDesc(userId, request.popupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 팝업에 대한 위치 인증이 필요합니다."));

        if (!verification.isVerifiedAt(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "위치 인증이 만료되었거나 유효하지 않습니다.");
        }

        Popup popup = popupRepository.findById(request.popupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팝업을 찾을 수 없습니다."));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."));

        ExchangePost post = new ExchangePost(
                userId,
                popup,
                category,
                request.haveItemName(),
                request.wantItemName(),
                request.preferredLocation(),
                request.preferredTime(),
                request.description()
        );

        if (request.imageUrls() != null) {
            for (int i = 0; i < request.imageUrls().size(); i++) {
                post.addImage(new PostImage(request.imageUrls().get(i), i));
            }
        }

        return exchangePostRepository.save(post).getPostId();
    }

    @Transactional
    public Long updatePost(Long userId, Long postId, UpdatePostRequest request) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        post.update(request.wantItemName(), request.preferredLocation(), request.preferredTime(), request.description());

        if (request.imageUrls() != null) {
            post.getImages().clear();
            for (int i = 0; i < request.imageUrls().size(); i++) {
                post.addImage(new PostImage(request.imageUrls().get(i), i));
            }
        }

        return post.getPostId();
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        postLikeRepository.deleteByExchangePost(post);
        exchangePostRepository.delete(post);
    }

    private static final int MAX_IMAGE_COUNT = 10;

    public List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 이미지가 없습니다.");
        }
        if (files.size() > MAX_IMAGE_COUNT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지는 최대 " + MAX_IMAGE_COUNT + "장까지 업로드할 수 있습니다.");
        }
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.");
            }
        }
        return files.stream()
                .map(file -> fileService.storeFile(file, "posts"))
                .toList();
    }

    public PostListResponse getMyPosts(Long userId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ExchangePost> postPage = (status == null || status.isBlank())
                ? exchangePostRepository.findByUserId(userId, pageable)
                : exchangePostRepository.findByUserIdAndStatus(userId, status, pageable);

        var content = postPage.getContent().stream()
                .map(post -> PostListResponse.PostSummary.from(post, postLikeRepository.existsByUserIdAndExchangePost(userId, post)))
                .toList();

        return new PostListResponse(content, postPage.getNumber(), postPage.getSize());
    }
}
