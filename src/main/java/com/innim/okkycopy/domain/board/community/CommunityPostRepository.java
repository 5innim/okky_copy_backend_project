package com.innim.okkycopy.domain.board.community;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findByPostId(long postId);
}
