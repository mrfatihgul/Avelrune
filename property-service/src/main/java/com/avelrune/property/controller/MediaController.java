package com.avelrune.property.controller;

import com.avelrune.property.dto.MediaUploadResponse;
import com.avelrune.property.security.AuthHeaders;
import com.avelrune.property.service.MediaStorageService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/properties/media")
public class MediaController {

    private final MediaStorageService mediaStorageService;

    public MediaController(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    @PostMapping
    public ResponseEntity<MediaUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId
    ) {
        AuthHeaders.requireUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaStorageService.store(file));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> get(@PathVariable String filename) {
        MediaStorageService.StoredMedia media = mediaStorageService.load(filename);
        return ResponseEntity.ok()
                .contentType(media.mediaType())
                .contentLength(media.contentLength())
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .body(media.resource());
    }
}
