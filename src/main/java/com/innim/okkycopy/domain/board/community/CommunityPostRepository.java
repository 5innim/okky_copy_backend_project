package com.innim.okkycopy.domain.board.community;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findByPostId(long postId);

    @Query("SELECT c FROM CommunityPost c WHERE c.boardTopic = :boardTopic AND c.title LIKE CONCAT('%', :keyword, '%')")
    Page<CommunityPost> findPageByBoardTopicAndKeyword(@Param("boardTopic") BoardTopic boardTopic, String keyword,
        Pageable pageable);

    @Query("SELECT c FROM CommunityPost c WHERE c.title LIKE CONCAT('%', :keyword, '%')")
    Page<CommunityPost> findPageByKeyword(String keyword, Pageable pageable);
}
