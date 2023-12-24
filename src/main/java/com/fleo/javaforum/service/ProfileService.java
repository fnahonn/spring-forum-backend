package com.fleo.javaforum.service;

import com.fleo.javaforum.config.StorageProperties;
import com.fleo.javaforum.infrastructure.image.ImageService;
import com.fleo.javaforum.infrastructure.upload.UploadService;
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

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UploadService uploadService;

    private final ImageService imageService;
    private Path avatarsLocation;

    public ProfileService(UserRepository userRepository, StorageProperties properties, UploadService uploadService, ImageService imageService) {
        if (properties.getLocation().getAvatars().trim().length() == 0) {
            throw new RuntimeException("Avatar upload location can not be empty");
        }
        this.uploadService = uploadService;
        this.avatarsLocation = Paths.get(properties.getLocation().getAvatars());
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public File uploadImage(MultipartFile avatarImage, int targetWidth, int targetHeight, boolean cropped, final Authentication auth) throws IOException {

        if (avatarImage.isEmpty()) {
            throw new IOException("No file found");
        }

        BufferedImage resizedImage = imageService.resize(avatarImage, targetWidth, targetHeight, cropped);

        User hydratedUser = findById(((User) auth.getPrincipal()).getId());

        Path storageLocation = this.avatarsLocation.resolve(Paths.get(Long.toString(hydratedUser.getId())))
                .normalize().toAbsolutePath();

        File uploadedFile = imageService.uploadImage(resizedImage, storageLocation, avatarImage.getOriginalFilename());

        return uploadedFile;
    }

    public User findById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
