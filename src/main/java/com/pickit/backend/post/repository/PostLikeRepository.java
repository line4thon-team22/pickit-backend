package com.pickit.backend.post.repository;

import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostLike;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserIdAndExchangePost(Long userId, ExchangePost exchangePost);
    boolean existsByUserIdAndExchangePost(Long userId, ExchangePost exchangePost);

    void deleteByExchangePost(ExchangePost exchangePost);

    @Query(value = "select l.exchangePost from PostLike l where l.userId = :userId",
            countQuery = "select count(l) from PostLike l where l.userId = :userId")
    Page<ExchangePost> findLikedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
