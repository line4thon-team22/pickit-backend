package com.pickit.backend.post.service;

import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostLike;
import com.pickit.backend.post.repository.ExchangePostRepository;
import com.pickit.backend.post.repository.PostLikeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final ExchangePostRepository exchangePostRepository;
    private final PostStateResolver postStateResolver;
    private final PostCountResolver postCountResolver;

    public PostLikeService(PostLikeRepository postLikeRepository, ExchangePostRepository exchangePostRepository,
                           PostStateResolver postStateResolver, PostCountResolver postCountResolver) {
        this.postLikeRepository = postLikeRepository;
        this.exchangePostRepository = exchangePostRepository;
        this.postStateResolver = postStateResolver;
        this.postCountResolver = postCountResolver;
    }

    @Transactional
    public Long likePost(Long userId, Long postId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        if (postLikeRepository.existsByUserIdAndExchangePost(userId, post)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 교환글입니다.");
        }

        try {
            return postLikeRepository.saveAndFlush(new PostLike(userId, post)).getLikeId();
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 UK(user_id, post_id)에 걸린 경우
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 교환글입니다.");
        }
    }

    /** 찜하지 않은 글의 취소 요청도 같은 결과(성공)를 반환한다. */
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        postLikeRepository.findByUserIdAndExchangePost(userId, post)
                .ifPresent(postLikeRepository::delete);
    }

    /** 최근에 찜한 순서로 내가 찜한 교환글을 조회한다. */
    public PostListResponse getMyLikedPosts(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeId"));
        Page<ExchangePost> postPage = postLikeRepository.findLikedPostsByUserId(userId, pageable);

        var states = postStateResolver.resolveAll(postPage.getContent());
        var counts = postCountResolver.countAll(postPage.getContent());
        var content = postPage.getContent().stream()
                .map(post -> PostListResponse.PostSummary.from(post, true, states.get(post.getPostId()), counts.get(post.getPostId())))
                .toList();

        return new PostListResponse(content, postPage.getNumber(), postPage.getSize());
    }
}
