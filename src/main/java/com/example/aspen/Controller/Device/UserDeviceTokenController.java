package com.example.aspen.Controller.Device;


import com.example.aspen.Dto.RegisterDeviceRequest;
import com.example.aspen.Service.UserDeviceTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("device")
public class UserDeviceTokenController {

    private final  UserDeviceTokenService deviceTokenService;


    public UserDeviceTokenController(UserDeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerDevice(@RequestBody RegisterDeviceRequest request , Authentication authentication) {

        String userIdStr = authentication.getPrincipal().toString();
        UUID userId = UUID.fromString(userIdStr);

        deviceTokenService.registerDevice(userId , request);

        return ResponseEntity.ok().build();
    }
}
