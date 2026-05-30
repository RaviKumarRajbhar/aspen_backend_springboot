package com.example.aspen.Service;

import com.example.aspen.Dto.FeedResponse;
import com.example.aspen.Dto.Mapper.PostMapper;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;
import com.example.aspen.Repository.FollowRepository;
import com.example.aspen.Repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FollowRepository followRepository;

    public FeedService(PostRepository postRepository, PostMapper postMapper, FollowRepository followRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.followRepository = followRepository;
    }


    @Transactional(readOnly = true)
    public FeedResponse getFeedCursor(UUID userId , LocalDateTime cursorCreatedAt , UUID cursorId) {


        List<UUID> followedUserIds = followRepository.findFollowedUserIds(userId);

        FeedResponse response = new FeedResponse();

        if (followedUserIds.isEmpty()) {
            response.setPosts(List.of());
            response.setHasNext(false);

            return response;
        }

        List<Post> posts;

        if (cursorCreatedAt == null) {
            posts = postRepository.getFirstFeedPage(
                    followedUserIds,
                    PageRequest.of(0 , 21)
            );
        } else {
            posts = postRepository.getFeedAfterCursor(
                    followedUserIds,
                    cursorCreatedAt,
                    cursorId,
                    PageRequest.of(0,21)
            );
        }

        boolean hasNext = posts.size() > 20 ;

        if (hasNext) {
            posts.removeLast();
        }

        List<PostResponse> responses = posts.stream()
                .map(postMapper::toResponse)
                .toList();

        response.setHasNext(hasNext);
        response.setPosts(responses);

        if (!posts.isEmpty()) {
            Post lastPost = posts.getLast();

            response.setNextCursorCreatedAt(lastPost.getCreatedAt());

            response.setNextCursorId(lastPost.getId());
        }

        return response;
    }

}
