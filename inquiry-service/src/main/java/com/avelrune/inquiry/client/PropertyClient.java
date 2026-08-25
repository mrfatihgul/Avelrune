package com.avelrune.inquiry.client;

import com.avelrune.inquiry.dto.PropertyRef;

public interface PropertyClient {

    PropertyRef findBySlug(String slug);

    PropertyRef findById(Long id);
}
