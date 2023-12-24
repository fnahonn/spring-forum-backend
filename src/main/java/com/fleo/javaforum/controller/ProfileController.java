package com.fleo.javaforum.controller;

import com.fleo.javaforum.infrastructure.upload.UploadService;
import com.fleo.javaforum.payload.request.ProfileRequest;
import com.fleo.javaforum.payload.response.UserResponse;
import com.fleo.javaforum.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    final int TARGET_HEIGHT = 150;
    final int TARGET_WIDTH = 150;
    @Autowired
    UploadService uploadService;
    @Autowired
    ProfileService profileService;



    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadImage(@NotNull(message = "avatarImage file is null") @RequestBody MultipartFile avatarImage, Authentication auth) throws IOException {

        File uploadedFile = profileService.uploadAvatarImage(avatarImage, TARGET_WIDTH, TARGET_HEIGHT, true, auth);


        return ResponseEntity.ok(String.format("New image uploaded at : %s", uploadedFile.getAbsolutePath()));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody ProfileRequest request, Authentication auth) {
        UserResponse response = profileService.updateProfile(request, auth);

        return ResponseEntity.ok(response);
    }
}
