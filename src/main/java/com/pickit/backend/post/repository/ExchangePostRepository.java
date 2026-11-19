package com.pickit.backend.post.repository;

import com.pickit.backend.post.entity.ExchangePost;
import java.time.LocalDateTime;
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
              and (:tab is null
                   or (:tab = 'ONSITE' and exists (
                        select 1 from PopupVerification v
                        where v.verificationId = p.verificationId
                          and coalesce(v.leftAt, v.expiredAt) > :threshold))
                   or (:tab = 'GENERAL' and not exists (
                        select 1 from PopupVerification v
                        where v.verificationId = p.verificationId
                          and coalesce(v.leftAt, v.expiredAt) > :threshold)))
            """)
    Page<ExchangePost> findPosts(
            @Param("popupId") Long popupId,
            @Param("categoryId") Long categoryId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("tab") String tab,
            @Param("threshold") LocalDateTime threshold,
            Pageable pageable
    );

    @Query("""
            select p from ExchangePost p
            where p.category.categoryId in :categoryIds
              and p.status = 'OPEN'
              and not exists (
                  select 1 from PopupVerification v
                  where v.verificationId = p.verificationId
                    and coalesce(v.leftAt, v.expiredAt) > :threshold)
            order by p.createdAt desc
            """)
    List<ExchangePost> findByCategoryIdInAndStatusOpen(@Param("categoryIds") List<Long> categoryIds,
                                                       @Param("threshold") LocalDateTime threshold, Pageable pageable);

    @Query("""
            select p from ExchangePost p
            where p.status = 'OPEN'
              and not exists (
                  select 1 from PopupVerification v
                  where v.verificationId = p.verificationId
                    and coalesce(v.leftAt, v.expiredAt) > :threshold)
            order by p.createdAt desc
            """)
    List<ExchangePost> findByStatusOpen(@Param("threshold") LocalDateTime threshold, Pageable pageable);

    Page<ExchangePost> findByUserId(Long userId, Pageable pageable);

    Page<ExchangePost> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}
