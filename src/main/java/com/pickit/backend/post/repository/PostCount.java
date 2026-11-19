package com.pickit.backend.post.repository;

/** 교환글별 집계 결과(찜 수, 채팅방 수 등). */
public interface PostCount {

    Long getPostId();

    Long getCount();
}
