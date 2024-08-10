package com.innim.okkycopy.domain.board.service;

import com.innim.okkycopy.domain.board.dao.TagCntQueryDao;
import com.innim.okkycopy.domain.board.repository.TagRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagCrudService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagCntQueryDao> findTopTagsByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate,
        int limit) {
        return tagRepository.findTopTagsByCreatedDateRange(startDate, endDate, PageRequest.of(0, limit));
    }
}
