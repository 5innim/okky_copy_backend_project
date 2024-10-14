package com.innim.okkycopy.domain.board.comment.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.Setter;

@Setter
public class CommentExpressionKey implements Serializable {

    private Long member;
    private Long comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentExpressionKey key = (CommentExpressionKey) o;
        return Objects.equals(member, key.member)
            && Objects.equals(comment, key.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, comment);
    }
}
