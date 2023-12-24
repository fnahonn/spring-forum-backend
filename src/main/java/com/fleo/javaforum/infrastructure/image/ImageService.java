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

    public BufferedImage resize(MultipartFile file, int targetWidth, int targetHeight, boolean cropped) throws IOException {

        BufferedImage originalImage = null;
        BufferedImage croppedImage = null;

        if (targetWidth <= 0 || targetHeight <= 0)
            throw new IllegalArgumentException("width [" + targetWidth
                    + "] and height [" + targetHeight + "] must be > 0");

        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            if (!checkSafeImage(inputStream)) {
                throw new IllegalArgumentException("L'image ne doit pas dépasser 1920px x 1080px");
            }

            originalImage = ImageIO.read(inputStream);

        } catch (IOException e) {
            log.error("An error occurred reading file from request", e);
            throw new RuntimeException("Failed to store file", e);
        }
        float ratio = (float) originalImage.getWidth() / (float) originalImage.getHeight();

        if (cropped) {
            //On conserve la dimension la plus petite
            int targetDimension = (ratio <= 1) ? originalImage.getWidth() : originalImage.getHeight();

            croppedImage = crop(originalImage, targetDimension, targetDimension);
        } else {
            //En réalité on ne se sert que d'une dimension pour respecter les proportions
            if (ratio <= 1) {
                targetHeight = Math.round((float) targetWidth / ratio);
            } else {
                targetWidth = Math.round((float) targetHeight * ratio);
            }
        }
        BufferedImage resizedImage = resizeImageIncrementally(
                cropped ? croppedImage : originalImage, 
                targetWidth, 
                targetHeight, 
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

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

    /**
     * <p>Prévention contre les attaques de type Pixel Flood</p>
     * @param input input stream à contrôler
     * @return boolean
     * @throws IOException
     * @Descti
     */
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
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationHintValue);
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

    private BufferedImage crop(BufferedImage src, int targetWidth, int targetHeight) {
        int currentWidth = src.getWidth();
        int currentHeight = src.getHeight();

        BufferedImage croppedImage = new BufferedImage(
                targetWidth,
                targetHeight,
                (src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB)
        );
        //Le crop part toujours du centre de l'image (amplitude variable en fonction des target dimensions fournies)
        int sourceX = Math.round((float) (currentWidth - targetWidth) / 2);
        int sourceY = Math.round((float) (currentHeight - targetHeight) / 2);

        Graphics2D graphics2D = croppedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(
                src,
                0,
                0,
                targetWidth,
                targetHeight,
                sourceX,
                sourceY,
                sourceX + targetWidth,
                sourceY + targetHeight,
                null
        );
        graphics2D.dispose();
        src.flush();

        return croppedImage;
    }
}
