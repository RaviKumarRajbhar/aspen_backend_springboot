package com.example.aspen.Service;

import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.Mapper.PostMapper;
import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Dto.PostRequest;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.PostRepository;
import com.example.aspen.Repository.UserRepository;
import com.example.aspen.Security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PostMapper postMapper;

    @Mock
    private MultipartFile image;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldCreatePostSuccessFully() throws Exception {

        UUID userId = UUID.randomUUID();

        PostRequest request = new PostRequest();
        request.setCaption("Test Post");
        request.setIsLandscape(true);

        User user = new User();
        user.setId(userId);

        PostResponse response = new PostResponse();
        when(image.getOriginalFilename())
                .thenReturn("photo.jpg");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(postMapper.toResponse(any(Post.class)))
                .thenReturn(response);

        PostResponse result = postService.createPost(userId, request, image);

        assertNotNull(result);

        verify(image).transferTo(any(File.class));

        verify(postRepository).save(any(Post.class));

    }

    @Test
    void shouldReturnPagedPostsSuccessfully() {

        UUID userId = UUID.randomUUID();

        Post post = new Post(); // fake post
        post.setCaption("Hello");

        List<Post> postList = List.of(post);

        PostResponse postResponse = new PostResponse();

        Page<Post> postPage = new PageImpl<>(postList);

        when(postRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(postPage);

        when(postMapper.toResponse(post)).thenReturn(postResponse);

        PagedResponse<PostResponse> result = postService.getAllPostsByUserId(userId, 0, 10);

        assertNotNull(result);

        assertEquals(1, result.getContent().size());

        assertEquals(1, result.getTotalElements());

        assertEquals(1, result.getTotalPages());

        verify(postRepository).findByUserId(eq(userId), any(Pageable.class));

        verify(postMapper).toResponse(post);
    }

    @Test
    void shouldReturnPostByIdSuccessfully() {

        UUID postId = UUID.randomUUID();

        Post post = new Post();
        post.setCaption("Hello");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

        assertEquals("Hello", result.getCaption());
        verify(postRepository).findById(postId);
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {

        UUID postId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(postId));

        verify(postRepository).findById(postId);

        assertEquals("Post Not Found", exception.getMessage());
    }

    @Test
    void ShouldThrowExceptionWhenDeletingPostNotFound() {

        UUID postId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(userId, postId));

        assertEquals("Post Not Found", exception.getMessage());

        verify(userRepository, never()).findById(userId);

    }

    @Test
    void shouldNotDeletePostIfActionNotPerformedByOwner() {

        UUID realUserId = UUID.randomUUID();

        User realUser = new User();
        realUser.setId(realUserId);

        UUID fakeUserId = UUID.randomUUID();

        UUID postId = UUID.randomUUID();

        Post post = new Post();
        post.setCaption("post");
        post.setUser(realUser);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> postService.deletePost(fakeUserId, postId));

        verify(postRepository , never()).delete(any(Post.class));
        assertEquals("You are not allowed to delete this post" , exception.getReason() );

        assertEquals(HttpStatus.FORBIDDEN , exception.getStatusCode());
    }

    @Test
    void shouldDeletePostSuccessfully() {

        UUID postId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setPostCount(77);

        Post post = new Post();
        post.setUser(user);
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        postService.deletePost(userId , postId);

        verify (postRepository).delete(post);

        verify(userRepository).save(user);


        assertEquals(76  , user.getPostCount());

    }

    // Tests on edit post pending
}
