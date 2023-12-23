package com.fleo.javaforum.controller;

import com.fleo.javaforum.infrastructure.upload.UploadService;
import com.fleo.javaforum.service.ProfileService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/profile")
public class AvatarProfileController {

    final int TARGET_HEIGHT = 100;
    final int TARGET_WIDTH = 100;
    @Autowired
    UploadService uploadService;
    @Autowired
    ProfileService profileService;



    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadImage(@NotNull(message = "avatarImage file is null") @RequestBody MultipartFile avatarImage, Authentication auth) throws IOException {

        File uploadedFile = profileService.uploadImage(avatarImage, TARGET_WIDTH, TARGET_HEIGHT, auth);


        return ResponseEntity.ok(String.format("New image uploaded at : %s", uploadedFile.getAbsolutePath()));
    }
}
