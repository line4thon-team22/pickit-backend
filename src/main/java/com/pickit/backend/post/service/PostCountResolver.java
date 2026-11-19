package com.pickit.backend.post.service;

import com.pickit.backend.chat.repository.ChatRoomRepository;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.repository.PostCount;
import com.pickit.backend.post.repository.PostLikeRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 교환글 카드·상세에 보여줄 찜 수(likeCount)와 채팅 수(chatCount)를 계산한다. */
@Component
public class PostCountResolver {

    public static final PostCounts EMPTY = new PostCounts(0L, 0L);

    private final PostLikeRepository postLikeRepository;
    private final ChatRoomRepository chatRoomRepository;

    public PostCountResolver(PostLikeRepository postLikeRepository, ChatRoomRepository chatRoomRepository) {
        this.postLikeRepository = postLikeRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public record PostCounts(long likeCount, long chatCount) {
    }

    /** 목록 조회용: 글 수와 관계없이 찜·채팅 각각 쿼리 한 번으로 집계한다. */
    public Map<Long, PostCounts> countAll(Collection<ExchangePost> posts) {
        List<Long> postIds = posts.stream().map(ExchangePost::getPostId).distinct().toList();
        Map<Long, PostCounts> result = new HashMap<>();
        if (postIds.isEmpty()) {
            return result;
        }
        Map<Long, Long> likeCounts = toMap(postLikeRepository.countByPostIds(postIds));
        Map<Long, Long> chatCounts = toMap(chatRoomRepository.countByPostIds(postIds));
        for (Long postId : postIds) {
            result.put(postId, new PostCounts(likeCounts.getOrDefault(postId, 0L), chatCounts.getOrDefault(postId, 0L)));
        }
        return result;
    }

    public PostCounts count(ExchangePost post) {
        return new PostCounts(postLikeRepository.countByExchangePost(post), chatRoomRepository.countByPost(post));
    }

    private static Map<Long, Long> toMap(List<PostCount> counts) {
        Map<Long, Long> map = new HashMap<>();
        counts.forEach(c -> map.put(c.getPostId(), c.getCount()));
        return map;
    }
}
