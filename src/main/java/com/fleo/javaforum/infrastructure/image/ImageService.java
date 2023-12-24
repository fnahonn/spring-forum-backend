package com.fleo.javaforum.infrastructure.image;

import com.fleo.javaforum.infrastructure.upload.UploadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
public class ImageService {

    private final UploadUtils uploadUtils;

    private final Logger log = LoggerFactory.getLogger(ImageService.class);

    public ImageService(UploadUtils uploadUtils) {
        this.uploadUtils = uploadUtils;
    }

    public BufferedImage resize(MultipartFile file, int targetWidth, int targetHeight) throws IOException {

        BufferedImage originalImage;

        if (targetWidth <= 0 || targetHeight <= 0)
            throw new IllegalArgumentException("width [" + targetWidth
                    + "] and height [" + targetHeight + "] must be > 0");

        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            if (!checkSafeImage(inputStream)) {
                throw new IllegalArgumentException("Incorrect image size");
            }
            originalImage = ImageIO.read(inputStream);
        }

        float ratio = (float) originalImage.getWidth() / (float) originalImage.getHeight();

        if (ratio <= 1) {
            targetHeight = Math.round((float) targetWidth / ratio);
        } else {
            targetWidth = Math.round((float) targetHeight * ratio);
        }


        BufferedImage resizedImage = resizeImageIncrementally(originalImage, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR);


        return resizedImage;
    }

    public File uploadImage(final BufferedImage imageToUpload, final Path storageLocation, final String originalFileName) {
        try {
            Path destinationImage = storageLocation.resolve(
                            Paths.get(uploadUtils.generateRandomName(originalFileName)))
                    .normalize().toAbsolutePath();

            if (!destinationImage.getParent().equals(storageLocation.toAbsolutePath())) {
                throw new IOException("Cannot store file outside current directory");
            }

            if (storageLocation != null && Files.notExists(storageLocation)) {
                Files.createDirectories(storageLocation);
            }
            File outputFile = destinationImage.toFile();

            boolean write = ImageIO.write(imageToUpload, uploadUtils.getExtension(originalFileName), outputFile);

            if (!write) {
                throw new IOException("An error occured during image upload");
            }
            return outputFile;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private boolean checkSafeImage(InputStream input) throws IOException {

        input.mark(Integer.MAX_VALUE);

        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(input)) {

            Iterator<ImageReader> iter = ImageIO.getImageReaders(imageInputStream);
            long maxSize = 1920L * 1080L;

            if (!iter.hasNext()) {
                return false;
            }

            ImageReader reader = iter.next();
            reader.setInput(imageInputStream, true, true);

            long width = reader.getWidth(0);
            long height = reader.getHeight(0);

            return (width * height) <= maxSize;

        } catch(IOException e) {
            return false;
        } finally {
            input.reset();
        }
    }

    private BufferedImage resizeImageIncrementally(BufferedImage src, int targetWidth, int targetHeight, Object interpolationHintValue) {
        int incrementCount = 0;
        boolean hasReassignedSrc = false;
        int currentWidth = src.getWidth();
        int currentHeight = src.getHeight();
        int fraction = 2;

        while (targetWidth != currentWidth || targetHeight != currentHeight) {
            int prevCurrentWidth = currentWidth;
            int prevCurrentHeight = currentHeight;

            currentWidth = Math.max(targetWidth, currentWidth - (currentWidth / fraction));

            currentHeight = Math.max(targetHeight, currentHeight - (currentHeight / fraction));

            if (prevCurrentHeight == currentHeight && prevCurrentWidth == currentWidth) {
                break;
            }

            BufferedImage incrementalImage = new BufferedImage(
                    currentWidth,
                    currentHeight,
                    (src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB)
            );

            Graphics2D graphics2D = incrementalImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage(src, 0, 0, currentWidth, currentHeight, null);
            graphics2D.dispose();

            if (hasReassignedSrc) {
                src.flush();
            }

            src = incrementalImage;

            hasReassignedSrc = true;

            incrementCount++;
        }

        log.info("Ended in {} increments", incrementCount);

        return src;
    }
}
