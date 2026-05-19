package com.example.aspen.Service;

import com.example.aspen.Dto.Mapper.PostMapper;
import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;
import com.example.aspen.Repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public FeedService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }


    @Transactional(readOnly = true)
    public PagedResponse<PostResponse> getPagedFeed(UUID UserId , int page , int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<PostResponse> pageResult =  postRepository.findAll(pageable)
                .map(postMapper::toResponse);

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
