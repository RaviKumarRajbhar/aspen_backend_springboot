package com.example.aspen.Dto.Mapper;

import com.example.aspen.Dto.UserDetailsResponse;
import com.example.aspen.Entities.User;

public class UserMapper {

    public static UserDetailsResponse toResponse(User user){

        UserDetailsResponse response = new UserDetailsResponse();

        response.setUserId(user.getId());
        response.setPostCount(user.getPostCount());
        response.setBio(user.getBio());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFollowers(user.getFollowersCount());
        response.setFollowing(user.getFollowingCount());


        return response ;

    }
}
