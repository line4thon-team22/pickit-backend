package com.pickit.backend.user.service;

import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.UserRepository;
import com.pickit.backend.common.service.FileService;
import com.pickit.backend.user.dto.request.UpdateProfileRequest;
import com.pickit.backend.user.dto.response.ProfileImageResponse;
import com.pickit.backend.user.dto.response.UserProfileResponse;
import com.pickit.backend.user.dto.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    public UserService(UserRepository userRepository, FileService fileService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    public UserResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserResponse updateMyProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        user.updateUsername(request.username());
        return UserResponse.from(user);
    }

    @Transactional
    public ProfileImageResponse uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        String imageUrl = fileService.storeFile(file);
        user.updateProfileImageUrl(imageUrl);
        return new ProfileImageResponse(imageUrl);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        user.clearProfileImageUrl();
    }
}
