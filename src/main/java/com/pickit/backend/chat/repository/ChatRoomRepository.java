package com.pickit.backend.chat.repository;

import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.repository.PostCount;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPostAndBuyerId(ExchangePost post, Long buyerId);

    boolean existsByPost(ExchangePost post);

    long countByPost(ExchangePost post);

    /** 교환글별 채팅방 수(교환글 카드·상세의 chatCount). */
    @Query("""
            select r.post.postId as postId, count(r) as count
            from ChatRoom r
            where r.post.postId in :postIds
            group by r.post.postId
            """)
    List<PostCount> countByPostIds(@Param("postIds") Collection<Long> postIds);

    /** 내가 참여한 방을 마지막 메시지 시각(메시지가 없으면 방 생성 시각) 최신순으로 조회한다. */
    @Query(value = """
            select r from ChatRoom r
            where r.sellerId = :userId or r.buyerId = :userId
            order by coalesce((select max(m.createdAt) from ChatMessage m where m.room = r), r.createdAt) desc,
                     r.roomId desc
            """,
            countQuery = "select count(r) from ChatRoom r where r.sellerId = :userId or r.buyerId = :userId")
    Page<ChatRoom> findMyRooms(@Param("userId") Long userId, Pageable pageable);
}
