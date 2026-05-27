package com.example.aspen.Repository;

import com.example.aspen.Entities.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken , UUID> {

    Optional<UserDeviceToken> findByFcmToken(String fcmToken);

    List<UserDeviceToken> findByUserIdAndIsActiveTrue(UUID userId);

    boolean existsByFcmToken (String fcmToken);

    void deleteByFcmToken(String fcmToken);


}
