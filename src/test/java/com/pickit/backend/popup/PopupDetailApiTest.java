package com.pickit.backend.popup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pickit.backend.auth.util.JwtProvider;
import com.pickit.backend.category.entity.Category;
import com.pickit.backend.category.repository.CategoryRepository;
import com.pickit.backend.popup.entity.Popup;
import com.pickit.backend.popup.entity.PopupLike;
import com.pickit.backend.popup.repository.PopupLikeRepository;
import com.pickit.backend.popup.repository.PopupRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** 팝업 상세도 목록처럼 찜 여부(isLiked)와 찜 수(likeCount)를 내려 준다. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PopupDetailApiTest {

    private static final long LIKED_USER_ID = 9_000_001L;
    private static final long OTHER_USER_ID = 9_000_002L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PopupRepository popupRepository;
    @Autowired
    private PopupLikeRepository popupLikeRepository;

    private Long popupId;

    @BeforeEach
    void savePopupWithOneLike() {
        Category category = categoryRepository.save(new Category("IT/전자제품"));
        Popup popup = popupRepository.save(new Popup(category, "테스트 팝업", "설명", "홍대/신촌",
                "서울 마포구 백범로31길 21", new BigDecimal("37.5470505"), new BigDecimal("126.9497895"),
                LocalDate.of(2026, 11, 22), LocalDate.of(2026, 11, 22), "UPCOMING", "/images/popups/pickit.png"));
        popupLikeRepository.save(new PopupLike(LIKED_USER_ID, popup, LocalDateTime.of(2026, 11, 20, 12, 0)));
        popupId = popup.getPopupId();
    }

    @Test
    void anonymousDetailShowsLikeCountButNotLiked() throws Exception {
        mockMvc.perform(get("/api/popups/{popupId}", popupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.popupId").value(popupId))
                .andExpect(jsonPath("$.data.isLiked").value(false))
                .andExpect(jsonPath("$.data.likeCount").value(1));
    }

    @Test
    void detailShowsLikedForUserWhoLikedIt() throws Exception {
        mockMvc.perform(get("/api/popups/{popupId}", popupId)
                        .header("Authorization", "Bearer " + jwtProvider.createAccessToken(LIKED_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLiked").value(true))
                .andExpect(jsonPath("$.data.likeCount").value(1));
    }

    @Test
    void detailShowsNotLikedForOtherUser() throws Exception {
        mockMvc.perform(get("/api/popups/{popupId}", popupId)
                        .header("Authorization", "Bearer " + jwtProvider.createAccessToken(OTHER_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLiked").value(false))
                .andExpect(jsonPath("$.data.likeCount").value(1));
    }
}
