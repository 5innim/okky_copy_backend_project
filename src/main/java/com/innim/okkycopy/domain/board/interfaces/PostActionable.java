package com.innim.okkycopy.domain.board.interfaces;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.entity.BoardTopic;

public interface PostActionable {

    void update(PostRequest updateRequest, BoardTopic boardTopic);

}
