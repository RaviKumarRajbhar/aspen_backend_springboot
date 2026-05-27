package com.example.aspen.Service;

import com.example.aspen.Dto.RegisterDeviceRequest;
import com.example.aspen.Entities.User;
import com.example.aspen.Entities.UserDeviceToken;
import com.example.aspen.Repository.UserDeviceTokenRepository;
import com.example.aspen.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserDeviceTokenService {

    private final UserRepository userRepository;
    private final UserDeviceTokenRepository tokenRepository;


    public UserDeviceTokenService(UserRepository userRepository, UserDeviceTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void registerDevice(UUID userId , RegisterDeviceRequest request) {

        if (request.getToken() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "FCM token cannot by empty"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User Not Found"
                ));

        Optional<UserDeviceToken> existingToken = tokenRepository.findByFcmToken(request.getToken());

        if (existingToken.isPresent()) {

            UserDeviceToken token = existingToken.get();

            token.setUser(user);
            token.setType(request.getType());
            token.setDeviceName(request.getDeviceName());
            token.setActive(true);

            return;
        }

        UserDeviceToken token = new UserDeviceToken();

        token.setUser(user);
        token.setFcmToken(request.getToken());
        token.setType(request.getType());
        token.setActive(true);
        token.setDeviceName(request.getDeviceName());

        tokenRepository.save(token);

    }

    @Transactional
    public void deactivateDevice( String fcmToken ) {

        UserDeviceToken token = tokenRepository.findByFcmToken(fcmToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device token not Found"
                ));

        token.setActive(false);

    }

}
