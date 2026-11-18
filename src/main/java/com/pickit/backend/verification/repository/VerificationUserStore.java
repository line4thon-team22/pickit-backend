package com.pickit.backend.verification.repository;

import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public class VerificationUserStore {

    private final JdbcTemplate jdbcTemplate;

    public VerificationUserStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void requireUser(Long userId, boolean lock) {
        queryUserColumn("user_id", Long.class, userId, lock);
    }

    /** 회원이 없으면 404이며, 최종 ERD의 users.is_premium 값을 반환한다. */
    public boolean isPremium(Long userId, boolean lock) {
        return Boolean.TRUE.equals(queryUserColumn("is_premium", Boolean.class, userId, lock));
    }

    private <T> T queryUserColumn(String column, Class<T> type, Long userId, boolean lock) {
        List<T> users;
        try {
            users = jdbcTemplate.queryForList(
                    "SELECT " + column + " FROM users WHERE user_id = ?" + (lock ? " FOR UPDATE" : ""),
                    type, userId
            );
        } catch (DataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "회원 DB 연결을 확인해주세요. 개발 테스트는 회원 준비 SQL을 먼저 실행해주세요.", exception);
        }
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.");
        }
        return users.get(0);
    }
}
