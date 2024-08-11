package com.innim.okkycopy.domain.board.service;

import com.innim.okkycopy.domain.board.dto.response.top_tag.TopTagListResponse;
import com.innim.okkycopy.domain.board.entity.TopTag;
import com.innim.okkycopy.domain.board.repository.TopTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopTagService {

    private final TopTagRepository topTagRepository;

    @Transactional
    public void removeAllTopTag() {
        topTagRepository.deleteAll();
    }


    @Transactional
    public void addTopTag(int rank, String name, long creates) {
        TopTag topTag = new TopTag(rank, name, creates);
        topTagRepository.save(topTag);
    }

    @Transactional(readOnly = true)
    public TopTagListResponse findAll() {
        List<TopTag> topTagList = topTagRepository.findAll();
        return TopTagListResponse.from(topTagList);
    }
}
