package com.innim.okkycopy.global.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlPattern {
    /**
     * Authorization Level
     *      0: permit all
     *      1: MAIL_INVALID_USER, USER, ADMIN
     *      2: MAIL_INVALID_USER
     *      3: USER, ADMIN
     *      4: ADMIN
     *
     */


    POST_LEVEL_3(new String[] {
        "/board/knowledge/write",
        "/board/community/write",
        "/board/event/write",
        "/board/qna/write",
        "/board/posts/{id}/scrap",
        "/board/posts/{id}/comment",
        "/board/comments/{commentId}/recomment",
        "/board/posts/{id}/like",
        "/board/posts/{id}/hate",
        "/board/comments/{id}/like",
        "/board/comments/{id}/hate"}),
    POST_LEVEL_1(new String[] {
        "/board/file/upload",
        "/member/change-email"}),
    POST_LEVEL_0(new String[] {
        "/member/signup",
        "/member/{provider}/signup"}),
    DELETE_LEVEL_3(new String[] {
        "/board/posts/{id}/scrap",
        "/board/knowledge/posts/{id}",
        "/board/community/posts/{id}",
        "/board/event/posts/{id}",
        "/board/qna/posts/{id}",
        "/board/comments/{id}",
        "/board/posts/{id}/like",
        "/board/posts/{id}/hate",
        "/board/comments/{id}/like",
        "/board/comments/{id}/hate"}),
    DELETE_LEVEL_1(new String[] {
        "/member/delete"}),
    PUT_LEVEL_3(new String[] {
        "/board/knowledge/posts/{id}",
        "/board/community/posts/{id}",
        "/board/event/posts/{id}",
        "/board/qna/posts/{id}",
        "/board/comments/{id}"}),
    PUT_LEVEL_1(new String[] {
        "/member/logout",
        "/member/profile-update",
        "/member/change-password"}),
    PUT_LEVEL_0(new String[] {
        "/member/email-authenticate",
        "/member/email-change-authenticate"
    }),
    GET_LEVEL_1(new String[] {
        "/member/info"}),
    GET_LEVEL_0(new String[] {
        "/board/topics",
        "/board/top-tag-list",
        "/board/knowledge/posts/{id}",
        "/board/community/posts/{id}",
        "/board/event/posts/{id}",
        "/board/qna/posts/{id}",
        "/board/posts/{id}/comments",
        "/board/comments/{id}/recomments",
        "/board/knowledge/posts",
        "/board/community/posts",
        "/board/event/posts",
        "/board/qna/posts"});


    private final String[] patterns;
}
