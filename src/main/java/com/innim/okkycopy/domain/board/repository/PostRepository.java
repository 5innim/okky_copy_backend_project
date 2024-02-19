package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(long postId);
}
