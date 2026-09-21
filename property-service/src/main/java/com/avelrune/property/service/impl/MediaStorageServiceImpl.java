package com.avelrune.property.service.impl;

import com.avelrune.property.dto.MediaUploadResponse;
import com.avelrune.property.exception.ApiException;
import com.avelrune.property.service.MediaStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class MediaStorageServiceImpl implements MediaStorageService {

    static final long MAX_BYTES = 8L * 1024 * 1024;
    private static final Pattern SAFE_NAME = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)$"
    );

    private final Path mediaDir;

    public MediaStorageServiceImpl(@Value("${app.media.dir}") String dir) {
        this.mediaDir = Path.of(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.mediaDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Görsel klasörü oluşturulamadı.", ex);
        }
    }

    @Override
    public MediaUploadResponse store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(400, "Bir görsel seçin.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new ApiException(400, "Görsel en fazla 8 MB olabilir.");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new ApiException(400, "Görsel okunamadı.");
        }
        if (bytes.length > MAX_BYTES) {
            throw new ApiException(400, "Görsel en fazla 8 MB olabilir.");
        }
        String extension = sniffExtension(bytes);
        if (extension == null) {
            throw new ApiException(400, "Yalnızca JPEG, PNG veya WEBP yükleyebilirsiniz.");
        }
        String filename = UUID.randomUUID() + "." + extension;
        Path target = mediaDir.resolve(filename);
        try {
            Files.write(target, bytes);
        } catch (IOException ex) {
            throw new ApiException(500, "Görsel kaydedilemedi.");
        }
        return new MediaUploadResponse("/api/properties/media/" + filename);
    }

    @Override
    public StoredMedia load(String filename) {
        if (filename == null) {
            throw new ApiException(404, "Görsel bulunamadı.");
        }
        String safe = filename.toLowerCase(Locale.ROOT);
        if (!SAFE_NAME.matcher(safe).matches()) {
            throw new ApiException(404, "Görsel bulunamadı.");
        }
        Path target = mediaDir.resolve(safe).normalize();
        if (!target.startsWith(mediaDir) || !Files.isRegularFile(target)) {
            throw new ApiException(404, "Görsel bulunamadı.");
        }
        try {
            return new StoredMedia(
                    new FileSystemResource(target),
                    mediaTypeFor(filename),
                    Files.size(target)
            );
        } catch (IOException ex) {
            throw new ApiException(404, "Görsel bulunamadı.");
        }
    }

    static String sniffExtension(byte[] bytes) {
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47) {
            return "png";
        }
        if (bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P') {
            return "webp";
        }
        return null;
    }

    private static MediaType mediaTypeFor(String filename) {
        if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (filename.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }
}
