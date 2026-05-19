package com.example.aspen.Repository;

import com.example.aspen.Entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByUserId(UUID id);

    List<Post> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAll(Pageable pageable);


    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserId(
            UUID userId,
            Pageable pageable
    );


}