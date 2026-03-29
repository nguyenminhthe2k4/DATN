package com.example.DATN.repository;

import com.example.DATN.entity.UserVocabularyLearning;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserVocabularyLearningRepository extends JpaRepository<UserVocabularyLearning, Long> {
    long countByNextReviewLessThanEqual(LocalDateTime time);

    long countByStreakCorrectGreaterThanEqual(Integer streak);

    @Query("""
            select u.id as userId,
                   u.email as email,
                   count(uvl.id) as wordsTracked,
                   coalesce(sum(coalesce(uvl.totalErrors, 0)), 0) as totalErrors,
                   coalesce(sum(coalesce(uvl.totalAttempts, 0)), 0) as totalAttempts,
                   coalesce(avg(coalesce(uvl.difficulty, 2.5)), 2.5) as avgDifficulty
            from UserVocabularyLearning uvl
            join uvl.user u
            where u.deletedAt is null
            group by u.id, u.email
            order by coalesce(sum(coalesce(uvl.totalErrors, 0)), 0) desc, count(uvl.id) desc
            """)
    List<ResetCandidateProjection> findResetCandidates(Pageable pageable);

    @Query("""
            select uvl
            from UserVocabularyLearning uvl
            where uvl.user.id = :userId
            """)
        List<UserVocabularyLearning> findByUserId(@Param("userId") Long userId);

    interface ResetCandidateProjection {
        Long getUserId();

        String getEmail();

        Long getWordsTracked();

        Long getTotalErrors();

        Long getTotalAttempts();

        Double getAvgDifficulty();
    }
}
