package com.fleo.javaforum.service;

import com.fleo.javaforum.config.StorageProperties;
import com.fleo.javaforum.infrastructure.image.ImageService;
import com.fleo.javaforum.infrastructure.upload.UploadService;
import com.fleo.javaforum.mapper.UserMapper;
import com.fleo.javaforum.payload.request.ProfileRequest;
import com.fleo.javaforum.payload.response.UserResponse;
import com.fleo.javaforum.security.model.User;
import com.fleo.javaforum.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageService imageService;
    private Path avatarsLocation;

    public ProfileService(UserRepository userRepository, UserMapper userMapper, StorageProperties properties, UploadService uploadService, ImageService imageService) {
        if (properties.getLocation().getAvatars().trim().length() == 0) {
            throw new RuntimeException("Avatar upload location can not be empty");
        }
        this.avatarsLocation = Paths.get(properties.getLocation().getAvatars());
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.imageService = imageService;
    }

    public File uploadAvatarImage(MultipartFile avatarImage, int targetWidth, int targetHeight, boolean cropped, final Authentication auth) throws IOException {

        if (avatarImage.isEmpty()) {
            throw new IOException("No file found");
        }

        BufferedImage resizedImage = imageService.resize(avatarImage, targetWidth, targetHeight, cropped);

        User hydratedUser = findById(((User) auth.getPrincipal()).getId());

        Path storageLocation = this.avatarsLocation.resolve(Paths.get(Long.toString(hydratedUser.getId())))
                .normalize().toAbsolutePath();

        File uploadedFile = imageService.uploadImage(resizedImage, storageLocation, avatarImage.getOriginalFilename());

        User updatedUser = User.builder()
                .id(hydratedUser.getId())
                .firstName(hydratedUser.getFirstName())
                .lastName(hydratedUser.getLastName())
                .pseudo(hydratedUser.getPseudo())
                .email(hydratedUser.getEmail())
                .password(hydratedUser.getPassword())
                .role(hydratedUser.getRole())
                .createdAt(hydratedUser.getCreatedAt())
                .avatarName(uploadedFile.getName())
                .updatedAt(Instant.now())
                .build();

        userRepository.save(updatedUser);

        return uploadedFile;
    }

    public UserResponse updateProfile(ProfileRequest request, final Authentication auth) {
        User currentUser = findById(((User) auth.getPrincipal()).getId());
        User hydratedUser = User.builder()
                .id(currentUser.getId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .pseudo(request.pseudo())
                .email(currentUser.getEmail())
                .password(currentUser.getPassword())
                .role(currentUser.getRole())
                .avatarName(currentUser.getAvatarName())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        User updatedUser = userRepository.save(hydratedUser);
        return userMapper.toResponse(updatedUser);
    }

    public User findById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
