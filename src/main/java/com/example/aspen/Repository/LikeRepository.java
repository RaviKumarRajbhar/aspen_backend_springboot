package com.example.aspen.Repository;

import com.example.aspen.Entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {


    Optional<Like> findByUserIdAndPostId(UUID userId , UUID postId);

    long countByPostId(UUID postId);
}
