package com.avelrune.property.service.impl;

import com.avelrune.property.dto.MediaUploadResponse;
import com.avelrune.property.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private MediaStorageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MediaStorageServiceImpl(tempDir.toString());
    }

    @Test
    void storesJpegAndRejectsTraversal() {
        byte[] jpeg = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x01, 0x02, 0x03};
        MediaUploadResponse stored = service.store(new MockMultipartFile("file", "photo.jpg", "image/jpeg", jpeg));
        assertTrue(stored.getUrl().startsWith("/api/properties/media/"));
        assertTrue(stored.getUrl().endsWith(".jpg"));

        String filename = stored.getUrl().substring("/api/properties/media/".length());
        assertEquals(jpeg.length, service.load(filename).contentLength());

        ApiException missing = assertThrows(ApiException.class, () -> service.load("../secret.jpg"));
        assertEquals(404, missing.getStatus());
    }

    @Test
    void rejectsNonImageBytes() {
        ApiException error = assertThrows(
                ApiException.class,
                () -> service.store(new MockMultipartFile("file", "note.txt", "text/plain", "hello".getBytes()))
        );
        assertEquals(400, error.getStatus());
    }
}
