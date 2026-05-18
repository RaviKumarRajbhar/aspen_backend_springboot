package com.example.aspen.Service;

import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.Mapper.PostMapper;
import com.example.aspen.Dto.PostRequest;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.PostRepository;
import com.example.aspen.Repository.UserRepository;
import com.example.aspen.Security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public PostResponse createPost(
            UUID userId,
            PostRequest request,
            MultipartFile image
    ) throws Exception {

        String fileName =
                UUID.randomUUID()
                        + "_"
                        + image.getOriginalFilename();


        String uploadDir =
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads";

        File folder =
                new File(uploadDir);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File destination =
                new File(
                        folder,
                        fileName
                );


        image.transferTo(
                destination
        );

        User user =
                userRepository.findById(
                        userId
                ).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );


        String imageUrl =
                "/uploads/" + fileName;

        Post post =
                new Post(
                        user,
                        request.getCaption()
                );

        post.setLandscape(
                request.getIsLandscape()
        );

        post.setImageUrl(
                imageUrl
        );

        postRepository.save(
                post
        );

        return PostMapper.toResponse(
                post
        );
    }


    public List<PostResponse> getAllPostsByUserId(
            UUID userId
    ) {

        List<Post> posts =
                postRepository.findByUserId(
                        userId
                );

        return posts.stream()
                .map(PostMapper::toResponse)
                .toList();
    }


    public Post getPostById(
            UUID postId
    ) {

        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Post Not Found"
                        )
                );
    }


    @Transactional
    public void deletePost(
            UUID userId,
            UUID postId
    ) {

        Post post =
                postRepository.findById(postId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Post Not Found"
                                )
                        );

        if (!post.getUser().getId().equals(userId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this post"
            );
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        user.setPostCount(
                user.getPostCount() - 1
        );

        userRepository.save(user);

        postRepository.delete(post);
    }


    public Post editPost(
            UUID userId,
            UUID postId,
            String newContent
    ) {

        if (newContent == null ||
                newContent.trim().isEmpty()) {

            throw new RuntimeException(
                    "Content cannot be empty"
            );
        }

        Post post =
                postRepository.findById(postId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Post no longer exists"
                                )
                        );

        if (!post.getUser().getId().equals(userId)) {

            throw new RuntimeException(
                    "You are not authorized to edit this post"
            );
        }

        post.setCaption(newContent);

        return postRepository.save(post);
    }
}