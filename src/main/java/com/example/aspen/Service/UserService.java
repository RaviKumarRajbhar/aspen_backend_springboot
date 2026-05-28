package com.example.aspen.Service;

import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.CustomException.UserAlreadyExistsException;
import com.example.aspen.Dto.Mapper.UserMapper;
import com.example.aspen.Dto.UpdateProfileRequest;
import com.example.aspen.Dto.UserDetailsResponse;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private  final UserRepository userRepository;

    public UserService(UserRepository repo){
        this.userRepository = repo;
    }


    @Cacheable(value = "users" , key = "#p0")
    public UserDetailsResponse getUserById(UUID id){

        System.out.println("DB HIT");

        final User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));

        return UserMapper.toResponse(user);

    }

    @CacheEvict(value = "users" , key = "#p1")
    public UserDetailsResponse updateProfile(UpdateProfileRequest request , UUID userId) {

        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        Optional<User> userByUsername = userRepository.findByUsername(request.getUsername());

        if (userByUsername.isPresent() && !userByUsername.get().getId().equals(existingUser.getId())){

            throw new UserAlreadyExistsException("Username not available");

        }

        existingUser.setUsername(request.getUsername());
        existingUser.setBio(request.getBio());

        User savedUser = userRepository.save(existingUser);

        return UserMapper.toResponse(savedUser);
    }
}
