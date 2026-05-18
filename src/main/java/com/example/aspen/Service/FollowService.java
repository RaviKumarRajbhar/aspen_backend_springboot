package com.example.aspen.Service;

import com.example.aspen.CustomException.BadRequestException;
import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.UserSummaryDto;
import com.example.aspen.Entities.Follow;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.FollowRepository;
import com.example.aspen.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
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

         return true;

    }


    public List<UserSummaryDto> getFollowers(UUID userId) {

        List<Follow> follows = followRepository.findByFollowingId(userId);

        List<User> followers = new ArrayList<>();

        for (Follow follow : follows) {
            User user = userRepository.findById(follow.getFollower().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

            followers.add(user);
        }



        return followers.stream()
                .map(user -> new UserSummaryDto(
                        user.getId(),
                        user.getUsername(),
                        user.getBio()
                        //profile image
                ))
                .toList();

    }

    public List<UserSummaryDto> getFollowing(UUID userId) {

        List<Follow> following = followRepository.findByFollowerId(userId);

        List<User> followings = new ArrayList<>();

        for (Follow follow : following){
            User user = userRepository.findById(follow.getFollowing().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

            followings.add(user);
        }

        return followings.stream()
                .map( user -> new UserSummaryDto(
                        user.getId(),
                        user.getUsername(),
                        user.getBio()
                ))
                .toList();
    }



}
