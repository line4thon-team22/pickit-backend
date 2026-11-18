package com.pickit.backend.post.repository;

import com.pickit.backend.post.entity.ExchangePost;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExchangePostRepository extends JpaRepository<ExchangePost, Long> {

    @Query("""
            select p from ExchangePost p
            where (:popupId is null or p.popup.popupId = :popupId)
              and (:categoryId is null or p.category.categoryId = :categoryId)
              and (:status is null or p.status = :status)
              and (:keyword is null or :keyword = '' or p.haveItemName like %:keyword% or p.wantItemName like %:keyword% or p.description like %:keyword%)
            """)
    Page<ExchangePost> findPosts(
            @Param("popupId") Long popupId,
            @Param("categoryId") Long categoryId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            select p from ExchangePost p
            where p.category.categoryId in :categoryIds
              and p.status = 'OPEN'
            order by p.createdAt desc
            """)
    List<ExchangePost> findByCategoryIdInAndStatusOpen(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("""
            select p from ExchangePost p
            where p.status = 'OPEN'
            order by p.createdAt desc
            """)
    List<ExchangePost> findByStatusOpen(Pageable pageable);

    Page<ExchangePost> findByUserId(Long userId, Pageable pageable);

    Page<ExchangePost> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}
