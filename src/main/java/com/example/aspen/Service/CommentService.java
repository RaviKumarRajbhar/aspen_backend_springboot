package com.example.aspen.Service;



import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.CommentResponse;
import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Entities.*;
import com.example.aspen.Repository.CommentRepository;
import com.example.aspen.Repository.PostRepository;
import com.example.aspen.Repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final PushNotificationService pushNotificationService;


    public CommentService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, NotificationService notificationService, PushNotificationService pushNotificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }



    @Transactional
    public void createComment(UUID userId , UUID postId , String content){

        if(content == null || content.isBlank()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment cannot be empty"
            );
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND , "User not Found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND , "Post not Found"));

        Comment comment = new Comment(user,post,content);

        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);

        if(!post.getUser().getId().equals(userId)) {
            notificationService.createNotification(post.getUser().getId() , userId , NotificationType.COMMENT , comment.getId());

                pushNotificationService.sendNotification(
                        post.getUser().getId(),
                        "New Comment",
                        user.getUsername() + " commented on your post!"
                        );

        }
    }


    @Transactional
    public void deleteComment(UUID userId , UUID commentId){

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Comment Not Found"
        ));


        if(!comment.getUser().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN ,
                    "You are not Authorized to delete this Comment");
        }

        Post post = comment.getPost();

        commentRepository.delete(comment);

        post.setCommentCount(Math.max(post.getCommentCount() - 1 , 0));
    }


    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getComments(UUID postId , int page , int size) {

        Pageable pageable = PageRequest.of(page , size , Sort.by("createdAt").descending());

        Page<CommentResponse> comments = commentRepository.findByPostId(postId , pageable )
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getContent(),
                        c.getUser().getUsername()
                ));

        PagedResponse<CommentResponse> response = new PagedResponse<>();

        response.setCurrentPage(comments.getNumber());
        response.setContent(comments.getContent());
        response.setHasNext(comments.hasNext());
        response.setTotalPages(comments.getTotalPages());
        response.setTotalElements(comments.getTotalElements());

        return response;
    }

    public Comment getCommentById(UUID commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not Found"));
    }


















}
