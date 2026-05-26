package com.example.aspen.Service;

import com.example.aspen.Dto.Mapper.PostMapper;
import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Follow;
import com.example.aspen.Repository.FollowRepository;
import com.example.aspen.Repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PagedResponse<PostResponse> getFeed(UUID userId , int page , int size) {

        List<Follow> follows = followRepository.findAllByFollowerId(userId);

        List<UUID> followedUserIds = follows.stream()
                .map(follow ->
                        follow.getFollowing().getId())
                .toList();

        if(followedUserIds.isEmpty()) {

            PagedResponse<PostResponse> emptyResponse = new PagedResponse<>();

            emptyResponse.setContent(List.of());
            emptyResponse.setTotalPages(0);
            emptyResponse.setHasNext(false);
            emptyResponse.setTotalElements(0);
            emptyResponse.setCurrentPage(0);

            return emptyResponse;
        }


        Pageable pageable = PageRequest.of(
                page,
                size
        );

        Page<PostResponse> pageResult = postRepository.findByUserIdInOrderByCreatedAtDesc(followedUserIds, pageable)
                .map(postMapper::toResponse);

//        Page<PostResponse> pageResult =  postRepository.findAll(pageable)
//                .map(postMapper::toResponse);

        PagedResponse<PostResponse> response = new PagedResponse<>();

        response.setContent(pageResult.getContent());
        response.setCurrentPage(pageResult.getNumber());
        response.setTotalPages(pageResult.getTotalPages());
        response.setHasNext(pageResult.hasNext());
        response.setTotalElements(pageResult.getTotalElements());

        return response;


//        return postRepository.findAll(pageable) // causes problem - LazyInitializationException dur to proxy object
//                .map(postMapper::toResponse);
    }

}
