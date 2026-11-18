package com.pickit.backend.popup.repository;

import com.pickit.backend.popup.entity.PopupLike;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PopupLikeRepository extends JpaRepository<PopupLike, Long> {

    boolean existsByUserIdAndPopup_PopupId(Long userId, Long popupId);

    long countByPopup_PopupId(Long popupId);

    @Query("select l.popup.popupId from PopupLike l where l.userId = :userId")
    List<Long> findPopupIdsByUserId(@Param("userId") Long userId);

    /** 회원이 찜한 팝업. 진행 중·진행 전·진행 완료 순, 같은 상태 안에서는 최근에 찜한 순이다. */
    @Query("""
            select l from PopupLike l
            join fetch l.popup p
            where l.userId = :userId
            order by case
                         when p.startDate <= :today and p.endDate >= :today then 0
                         when p.startDate > :today then 1
                         else 2
                     end,
                     l.likeId desc
            """)
    List<PopupLike> findLikedByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("""
            select l.popup.popupId as popupId, count(l) as likeCount
            from PopupLike l
            where l.popup.popupId in :popupIds
            group by l.popup.popupId
            """)
    List<PopupLikeCount> countByPopupIds(@Param("popupIds") Collection<Long> popupIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from PopupLike l where l.userId = :userId and l.popup.popupId = :popupId")
    int deleteByUserIdAndPopupId(@Param("userId") Long userId, @Param("popupId") Long popupId);
}
