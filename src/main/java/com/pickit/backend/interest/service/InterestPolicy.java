package com.pickit.backend.interest.service;

public final class InterestPolicy {

    /** 일반 회원이 선택할 수 있는 최대 관심사 개수. Figma 관심설정 화면 기준이다. */
    public static final int FREE_MAX_COUNT = 3;

    // 프리미엄 회원(users.is_premium)은 개수 제한 없이 전체 카테고리를 선택할 수 있다.
    // 프리미엄 상한 수치가 확정되면 여기에 상수를 추가한다.

    private InterestPolicy() {
    }
}
