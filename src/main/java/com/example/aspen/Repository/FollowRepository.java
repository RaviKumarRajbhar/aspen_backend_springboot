package com.example.aspen.Repository;

import com.example.aspen.Entities.Follow;
import com.example.aspen.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    int countByFollowing(User user); // followers count

    int countByFollower(User user); // following count

    List<Follow> findByFollowingId(UUID followingId);

    List<Follow> findByFollowerId(UUID followerId);

    void deleteByFollowerAndFollowing(User follower, User following);
}
