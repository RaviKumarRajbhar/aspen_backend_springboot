package com.example.aspen.Controller.User;

import com.example.aspen.Dto.UpdateProfileRequest;
import com.example.aspen.Dto.UserDetailsResponse;
import com.example.aspen.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/my")
    public ResponseEntity<?> ownerDetails(
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        UserDetailsResponse response = userService.findUserById(userId);

        return ResponseEntity.ok(response);

    }

    @PostMapping("update")
    public ResponseEntity<UserDetailsResponse> updateProfile (@RequestBody UpdateProfileRequest request , Authentication authentication) {

        String userIdStr = authentication.getPrincipal().toString();
        UUID userId = UUID.fromString(userIdStr);

        UserDetailsResponse response = userService.updateProfile(request , userId);
        return  ResponseEntity.ok(response);
    }
}
