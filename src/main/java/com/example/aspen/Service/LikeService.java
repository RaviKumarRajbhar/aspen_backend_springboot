package com.example.aspen.Service;

import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Entities.Like;
import com.example.aspen.Entities.NotificationType;
import com.example.aspen.Entities.Post;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.LikeRepository;
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
    private final PushNotificationService pushNotificationService;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository, NotificationService notificationService, PushNotificationService pushNotificationService){
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }

    @Transactional
    public boolean toggleLike(UUID userId , UUID postId){

        Optional<Like> existing = likeRepository.findByUserIdAndPostId(userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post Not Found"));

        if(existing.isPresent()){
           likeRepository.delete(existing.get());

           post.setLikeCount(Math.max(post.getLikeCount() - 1 , 0));

           notificationService.deleteLikeNotification(userId , postId );
           return false;
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Not a Valid User"));
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

            pushNotificationService.sendNotification(post.getUser().getId(), "New Like" , user.getUsername() + " liked your Post!");

        }

        return true;

    }

    public long likeCount( UUID postId){
        return likeRepository.countByPostId(postId);
    }

}
