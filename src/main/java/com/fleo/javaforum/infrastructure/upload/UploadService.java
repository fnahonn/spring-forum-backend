package com.fleo.javaforum.infrastructure.upload;

import com.fleo.javaforum.config.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadService {

    private final Path rootLocation;

    private final UploadUtils utils;
    @Autowired
    public UploadService(StorageProperties properties, UploadUtils utils) {
        if (properties.getLocation().getRoot().trim().length() == 0) {
            throw new RuntimeException("File upload location can not be empty");
        }
        this.rootLocation = Paths.get(properties.getLocation().getRoot());
        this.utils = utils;
    }

    public File upload(final File file) {
        return upload(file, this.rootLocation);
    }

    public File upload(final File file, Path storageLocation) {
        try {
            Path destination = storageLocation.resolve(
                    Paths.get(file.getName()))
                    .normalize().toAbsolutePath();
            if (!destination.getParent().equals(storageLocation.toAbsolutePath())) {
                throw new IOException("Cannot store file outside current directory");
            }

            if (storageLocation != null && Files.notExists(storageLocation)) {
                Files.createDirectories(storageLocation);
            }
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            Files.write(destination, fileBytes);

            return destination.toFile();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

}
