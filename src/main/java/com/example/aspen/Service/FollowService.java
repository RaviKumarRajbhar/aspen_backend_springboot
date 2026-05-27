package com.example.aspen.Service;

import com.example.aspen.CustomException.BadRequestException;
import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Dto.UserSummaryDto;
import com.example.aspen.Entities.Follow;
import com.example.aspen.Entities.Notification;
import com.example.aspen.Entities.NotificationType;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.FollowRepository;
import com.example.aspen.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PushNotificationService pushNotificationService;

    public FollowService(FollowRepository followRepository, UserRepository userRepository, NotificationService notificationService, PushNotificationService pushNotificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }

    @Transactional
    public boolean toggleFollow(UUID followerId , UUID followingId){

        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower Not found"));

        User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("Target Account Not Found"));

         if(follower.getId().equals(following.getId())){
             throw new BadRequestException("You cannot follow yourself");
         }

         boolean alreadyFollowing = followRepository.existsByFollowerAndFollowing(follower,following);

         if(alreadyFollowing) {
             followRepository.deleteByFollowerAndFollowing(follower, following);

             follower.setFollowingCount(Math.max(0,following.getFollowingCount() - 1));
             following.setFollowersCount(Math.max(0,following.getFollowersCount() - 1));

             return false;
         }

         Follow follow = new Follow();
         follow.setFollower(follower);
         follow.setFollowing(following);

         followRepository.save(follow);

         follower.setFollowingCount(follower.getFollowingCount() + 1);
         following.setFollowersCount(following.getFollowersCount() + 1);

         notificationService.createNotification(followingId , followerId , NotificationType.FOLLOW , follow.getId());

        pushNotificationService.sendNotification(followingId , "New Follower" , follower.getUsername() + " started Following you!");

        return true;

    }


    public PagedResponse<UserSummaryDto> getFollowers(UUID userId , int page , int size) {

        Pageable pageable = PageRequest.of(page ,size , Sort.by("createdAt").descending());

        Page<UserSummaryDto> follows = followRepository.findByFollowingId(userId , pageable)
                .map(follow ->{
                    User user = follow.getFollower();

                    return new UserSummaryDto(
                            user.getId(),
                            user.getUsername(),
                            user.getBio()
                    );
                });
        PagedResponse<UserSummaryDto> response = new PagedResponse<>();
        response.setTotalElements(follows.getTotalElements());
        response.setTotalPages(follows.getTotalPages());
        response.setContent(follows.getContent());
        response.setHasNext(follows.hasNext());
        response.setCurrentPage(follows.getNumber());

        return response;

    }

    @Transactional
    public PagedResponse<UserSummaryDto> getFollowing(UUID userId , int page , int size ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<UserSummaryDto> following = followRepository.findByFollowerId(userId , pageable)
                .map(follow -> {
                    User user = follow.getFollowing();

                    return new UserSummaryDto(
                            user.getId(),
                            user.getUsername(),
                            user.getBio()
                    );
                } );
        PagedResponse<UserSummaryDto> response = new PagedResponse<>();

        response.setCurrentPage(following.getNumber());
        response.setContent(following.getContent());
        response.setTotalElements(following.getTotalElements());
        response.setHasNext(following.hasNext());
        response.setTotalPages(following.getTotalPages());

        return response;


    }
}
