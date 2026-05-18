package com.example.aspen.Service;

import com.example.aspen.Dto.Mapper.UserMapper;
import com.example.aspen.Dto.UserDetailsResponse;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserService {

    private  final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo = repo;
    }


    public UserDetailsResponse findUserById(UUID id){

        final User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));

        return UserMapper.toResponse(user);

    }






}
