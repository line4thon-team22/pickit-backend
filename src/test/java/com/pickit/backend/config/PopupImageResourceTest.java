package com.pickit.backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** 팝업 썸네일은 앱 안이 아니라 서버 폴더 uploads/popups에서 로그인 없이 내보낸다. */
@SpringBootTest
@AutoConfigureMockMvc
class PopupImageResourceTest {

    private static final Path POPUP_DIR = Path.of("uploads", "popups");
    private static final Path TEST_IMAGE = POPUP_DIR.resolve("zz-test-popup-image.txt");
    private static final byte[] TEST_BYTES = "popup-image".getBytes();

    @Autowired
    private MockMvc mockMvc;

    private boolean createdDir;

    @BeforeEach
    void createTestImage() throws Exception {
        createdDir = Files.notExists(POPUP_DIR);
        Files.createDirectories(POPUP_DIR);
        Files.write(TEST_IMAGE, TEST_BYTES);
    }

    @AfterEach
    void deleteTestImage() throws Exception {
        Files.deleteIfExists(TEST_IMAGE);
        if (createdDir) {
            Files.deleteIfExists(POPUP_DIR);
        }
    }

    @Test
    void popupImageInUploadsFolderIsServedWithoutLogin() throws Exception {
        mockMvc.perform(get("/images/popups/zz-test-popup-image.txt"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(TEST_BYTES));
    }

    @Test
    void missingPopupImageReturnsNotFound() throws Exception {
        mockMvc.perform(get("/images/popups/zz-missing-popup-image.txt"))
                .andExpect(status().isNotFound());
    }
}
