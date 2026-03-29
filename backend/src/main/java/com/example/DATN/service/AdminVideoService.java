package com.example.DATN.service;

import com.example.DATN.dto.AdminVideoChannelDto;
import com.example.DATN.dto.AdminVideoDto;
import com.example.DATN.dto.UpsertVideoChannelRequest;
import com.example.DATN.dto.UpsertVideoRequest;
import com.example.DATN.entity.Topic;
import com.example.DATN.entity.Video;
import com.example.DATN.entity.YouTubeChannel;
import com.example.DATN.repository.TopicRepository;
import com.example.DATN.repository.VideoRepository;
import com.example.DATN.repository.YouTubeChannelRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminVideoService {
    private final VideoRepository videoRepository;
    private final YouTubeChannelRepository youTubeChannelRepository;
    private final TopicRepository topicRepository;

    public AdminVideoService(
            VideoRepository videoRepository,
            YouTubeChannelRepository youTubeChannelRepository,
            TopicRepository topicRepository
    ) {
        this.videoRepository = videoRepository;
        this.youTubeChannelRepository = youTubeChannelRepository;
        this.topicRepository = topicRepository;
    }

    public List<AdminVideoChannelDto> findAllChannels() {
        return youTubeChannelRepository.findChannelManagementRows().stream().map(this::toChannelDto).toList();
    }

    public AdminVideoChannelDto findChannelById(Long id) {
        YouTubeChannel channel = youTubeChannelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));

        long videoCount = videoRepository.findVideoManagementRows().stream()
                .filter(v -> id.equals(v.getChannelId()))
                .count();

        return new AdminVideoChannelDto(
            toLong(channel.id),
                defaultString(channel.name, ""),
            extractHandle(channel.url),
            "General",
                videoCount,
            "Hoạt động"
        );
    }

    public AdminVideoChannelDto createChannel(UpsertVideoChannelRequest request) {
        YouTubeChannel channel = new YouTubeChannel();
        applyChannel(channel, request);
        YouTubeChannel saved = youTubeChannelRepository.save(channel);
        return findChannelById(toLong(saved.id));
    }

    public AdminVideoChannelDto updateChannel(Long id, UpsertVideoChannelRequest request) {
        YouTubeChannel channel = youTubeChannelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found"));
        applyChannel(channel, request);
        youTubeChannelRepository.save(channel);
        return findChannelById(id);
    }

    public void deleteChannel(Long id) {
        boolean hasVideos = videoRepository.findVideoManagementRows().stream().anyMatch(v -> id.equals(v.getChannelId()));
        if (hasVideos) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete channel with linked videos");
        }
        if (!youTubeChannelRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Channel not found");
        }
        youTubeChannelRepository.deleteById(id);
    }

    public List<AdminVideoDto> findAllVideos() {
        return videoRepository.findVideoManagementRows().stream().map(this::toVideoDto).toList();
    }

    public AdminVideoDto findVideoById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        return new AdminVideoDto(
            toLong(video.id),
                defaultString(video.title, ""),
                defaultString(video.url, ""),
            video.channel == null ? null : toLong(video.channel.id),
                video.channel == null ? "" : defaultString(video.channel.name, ""),
                video.topic == null ? null : toLong(video.topic.id),
                video.topic == null ? "" : defaultString(video.topic.name, ""),
            "Trung bình",
            "—",
            0,
            "Chờ biên tập"
        );
    }

    public AdminVideoDto createVideo(UpsertVideoRequest request) {
        Video video = new Video();
        applyVideo(video, request);
        Video saved = videoRepository.save(video);
        return findVideoById(toLong(saved.id));
    }

    public AdminVideoDto updateVideo(Long id, UpsertVideoRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));
        applyVideo(video, request);
        videoRepository.save(video);
        return findVideoById(id);
    }

    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found");
        }
        videoRepository.deleteById(id);
    }

    private void applyChannel(YouTubeChannel channel, UpsertVideoChannelRequest request) {
        String name = request == null ? "" : defaultString(request.name(), "").trim();
        if (name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel name is required");
        }
        String handle = normalizeHandle(request == null ? null : request.handle());
        if (handle.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel handle is required");
        }

        channel.name = name;
        channel.url = "https://www.youtube.com/" + handle;
        channel.description = defaultString(request == null ? null : request.topic(), "General").trim();
    }

    private void applyVideo(Video video, UpsertVideoRequest request) {
        String title = request == null ? "" : defaultString(request.title(), "").trim();
        if (title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video title is required");
        }
        if (request == null || request.channelId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video channel is required");
        }

        YouTubeChannel channel = youTubeChannelRepository.findById(request.channelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel not found"));

        Topic topic = null;
        if (request.topicId() != null) {
            topic = topicRepository.findById(request.topicId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic not found"));
        }

        video.title = title;
        video.url = defaultString(request.youtubeUrl(), "").trim();
        video.channel = channel;
        video.topic = topic;
        video.transcript = video.transcript == null ? "" : video.transcript;
    }

    private AdminVideoChannelDto toChannelDto(YouTubeChannelRepository.YouTubeChannelManagementProjection row) {
        return new AdminVideoChannelDto(
                row.getId(),
                defaultString(row.getName(), ""),
            extractHandle(row.getUrl()),
            "General",
                row.getVideoCount() == null ? 0 : row.getVideoCount(),
            "Hoạt động"
        );
    }

    private AdminVideoDto toVideoDto(VideoRepository.VideoManagementProjection row) {
        return new AdminVideoDto(
                row.getId(),
                defaultString(row.getTitle(), ""),
                defaultString(row.getUrl(), ""),
                row.getChannelId(),
                defaultString(row.getChannelName(), ""),
                row.getTopicId(),
                defaultString(row.getTopicName(), ""),
                "Trung bình",
                "—",
                0,
                "Chờ biên tập"
        );
    }

    private String normalizeHandle(String value) {
        String handle = defaultString(value, "").trim();
        if (handle.isBlank()) {
            return "";
        }
        return handle.startsWith("@") ? handle : "@" + handle;
    }

    private String extractHandle(String url) {
        String normalizedUrl = defaultString(url, "").trim();
        int idx = normalizedUrl.lastIndexOf('/');
        if (idx >= 0 && idx + 1 < normalizedUrl.length()) {
            String suffix = normalizedUrl.substring(idx + 1);
            return normalizeHandle(suffix);
        }
        return "";
    }

    private String defaultString(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private Long toLong(Integer value) {
        return value == null ? null : value.longValue();
    }
}
