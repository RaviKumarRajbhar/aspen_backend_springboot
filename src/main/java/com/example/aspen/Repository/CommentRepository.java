package com.example.aspen.Repository;

import com.example.aspen.Entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {


    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByPostId(UUID postId , Pageable pageable);


}
