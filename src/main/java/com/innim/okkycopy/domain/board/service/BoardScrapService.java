package com.innim.okkycopy.domain.board.service;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardScrapService {

    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void addScrap(Member member, long postId) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        entityManager.persist(Scrap.of(post, mergedMember));
    }

    @Transactional
    public void removeScrap(Member member, long postId) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        Scrap scrap = scrapRepository.findByMemberAndPost(post, mergedMember)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_SCRAP));
        Scrap.remove(entityManager, scrap);
    }
}
