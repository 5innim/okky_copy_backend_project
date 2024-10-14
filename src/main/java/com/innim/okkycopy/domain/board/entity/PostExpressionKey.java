package com.innim.okkycopy.domain.board.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.Setter;

@Setter
public class PostExpressionKey implements Serializable {

    private Long member;
    private Long post;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostExpressionKey key = (PostExpressionKey) o;
        return Objects.equals(member, key.member)
            && Objects.equals(post, key.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, post);
    }
}
