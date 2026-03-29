package com.example.DATN.repository;

import com.example.DATN.entity.ArticleTopic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArticleTopicRepository extends JpaRepository<ArticleTopic, Long> {
    @Query("""
            select t.id as id,
                   t.name as name,
                   t.description as description,
                   count(a.id) as articleCount
            from ArticleTopic t
            left join Article a on a.topic.id = t.id
            group by t.id, t.name, t.description
            order by t.id desc
            """)
    List<ArticleTopicManagementProjection> findArticleTopicManagementRows();

    interface ArticleTopicManagementProjection {
        Long getId();

        String getName();

        String getDescription();

        Long getArticleCount();
    }
}
