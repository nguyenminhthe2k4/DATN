package com.example.DATN.repository;

import com.example.DATN.entity.Video;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long> {
	@Query("""
			select v.id as id,
				   v.title as title,
				   v.url as url,
				   c.id as channelId,
				   c.name as channelName,
				   t.id as topicId,
				   t.name as topicName
			from Video v
			left join v.channel c
			left join v.topic t
			order by v.id desc
			""")
	List<VideoManagementProjection> findVideoManagementRows();

	interface VideoManagementProjection {
		Long getId();

		String getTitle();

		String getUrl();

		Long getChannelId();

		String getChannelName();

		Long getTopicId();

		String getTopicName();
	}
}
