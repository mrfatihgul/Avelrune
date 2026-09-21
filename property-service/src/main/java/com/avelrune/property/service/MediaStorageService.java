package com.avelrune.property.service;

import com.avelrune.property.dto.MediaUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {

    MediaUploadResponse store(MultipartFile file);

    StoredMedia load(String filename);

    record StoredMedia(Resource resource, MediaType mediaType, long contentLength) {
    }
}
