package com.example.aspen.Service;

import com.example.aspen.Entities.Like;
import com.example.aspen.Entities.NotificationType;
import com.example.aspen.Entities.Post;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.LikeRepository;
import com.example.aspen.Repository.NotificationRepository;
import com.example.aspen.Repository.PostRepository;
import com.example.aspen.Repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository, NotificationService notificationService){
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public boolean toggleLike(UUID userId , UUID postId){

        Optional<Like> existing = likeRepository.findByUserIdAndPostId(userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new RuntimeException("Post Not Found"));

        if(existing.isPresent()){
           likeRepository.delete(existing.get());

           post.setLikeCount(post.getLikeCount() - 1);

           notificationService.deleteLikeNotification(userId , postId );
           return false;
        }

        User user = userRepository.getReferenceById(userId);
        // a optimized way to get user by not calling the complete object
        // this is called proxy object jiske paas sirf id hota no other fields
        Like like = new Like(user,post);

        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);

        if(!post.getUser().getId().equals(userId)){

            notificationService.createNotification(
                    post.getUser().getId(),
                    userId,
                    NotificationType.LIKE,
                    postId
            );

        }
        return true;

    }

    public long likeCount( UUID postId){
        return likeRepository.countByPostId(postId);
    }













}
