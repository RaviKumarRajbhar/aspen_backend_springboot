package com.example.aspen.Repository;

import com.example.aspen.Entities.Follow;
import com.example.aspen.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository
        extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerAndFollowing( User follower, User following );

    int countByFollowing(User user);

    int countByFollower(User user);


    // Followers list
    @EntityGraph(attributePaths = {"follower"})
    Page<Follow> findByFollowingId( UUID followingId,Pageable pageable);


    List<Follow> findAllByFollowerId(UUID followerId);

    // Following list
    @EntityGraph(attributePaths = {"following"})
    Page<Follow> findByFollowerId( UUID followerId, Pageable pageable);

    void deleteByFollowerAndFollowing( User follower, User following );
}