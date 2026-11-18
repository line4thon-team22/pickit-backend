package com.pickit.backend.interest.repository;

import com.pickit.backend.interest.entity.UserInterest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    @Query("""
            select i from UserInterest i
            join fetch i.category
            where i.userId = :userId
            order by i.userInterestId
            """)
    List<UserInterest> findAllByUserIdWithCategory(@Param("userId") Long userId);

    @Query("select i.category.categoryId from UserInterest i where i.userId = :userId order by i.userInterestId")
    List<Long> findCategoryIdsByUserId(@Param("userId") Long userId);
}
