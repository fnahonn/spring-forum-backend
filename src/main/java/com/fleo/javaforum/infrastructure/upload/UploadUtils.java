package com.fleo.javaforum.infrastructure.upload;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class UploadUtils {

    public String generateRandomName(String originalFileName) {
        String filename =  Long.toString(Instant.now().toEpochMilli());
        String extension = getExtension(originalFileName);

        return extension != null ? filename + "." + extension : filename;
    }

    public String getExtension(String originalFileName) {
        Optional<String> extension = Optional.ofNullable(originalFileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFileName.lastIndexOf(".") + 1));
        return extension.isPresent() ? extension.get() : null;
    }
}
