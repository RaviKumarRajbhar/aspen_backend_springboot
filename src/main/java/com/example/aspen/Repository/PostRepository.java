package com.example.aspen.Repository;

import com.example.aspen.Entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByUserId(UUID id);

    List<Post> findAllByOrderByCreatedAtDesc();


    @EntityGraph(attributePaths = {"user"})
    @Query("""
SELECT p
FROM Post p
WHERE p.user.id IN :userIds
ORDER BY p.createdAt DESC, p.id DESC
""")
    List<Post> getFirstFeedPage(
            @Param("userIds") List<UUID> userIds,
            Pageable pageable
    );


    @EntityGraph(attributePaths = {"user"})
    @Query("""
SELECT p
FROM Post p
WHERE p.user.id IN :userIds
AND (
    p.createdAt < :cursorCreatedAt
    OR
    (
        p.createdAt = :cursorCreatedAt
        AND p.id < :cursorId
    )
)
ORDER BY p.createdAt DESC, p.id DESC
""")
    List<Post> getFeedAfterCursor(
            @Param("userIds") List<UUID> userIds,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserId(
            UUID userId,
            Pageable pageable
    );


}