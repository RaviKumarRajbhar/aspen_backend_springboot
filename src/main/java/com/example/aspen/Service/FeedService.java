package com.example.aspen.Service;

import com.example.aspen.Dto.Mapper.PostMapper;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;
import com.example.aspen.Repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FeedService {

    private final PostRepository postRepository;

    public FeedService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostResponse> getFeed(UUID userId){

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream()
                .map(PostMapper::toResponse)
                .toList();
    }

}
