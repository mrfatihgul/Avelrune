package com.avelrune.request.client;

import com.avelrune.request.dto.PropertyRef;

public interface PropertyClient {

    PropertyRef findBySlug(String slug);

    PropertyRef findById(Long id);
}
