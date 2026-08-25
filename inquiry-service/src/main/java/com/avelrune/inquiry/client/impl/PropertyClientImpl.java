package com.avelrune.inquiry.client.impl;

import com.avelrune.inquiry.client.PropertyClient;
import com.avelrune.inquiry.dto.PropertyRef;
import com.avelrune.inquiry.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class PropertyClientImpl implements PropertyClient {

    private final RestClient restClient;

    public PropertyClientImpl(@Value("${property.service-url}") String propertyServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(propertyServiceUrl)
                .build();
    }

    @Override
    public PropertyRef findBySlug(String slug) {
        return get("/api/properties/{slug}", slug);
    }

    @Override
    public PropertyRef findById(Long id) {
        return get("/api/properties/id/{id}", id);
    }

    private PropertyRef get(String path, Object id) {
        try {
            PropertyRef property = restClient.get()
                    .uri(path, id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new ApiException(400, "Seçilen ilan bulunamadı.");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new ApiException(502, "İlan bilgisi şu anda doğrulanamadı.");
                    })
                    .body(PropertyRef.class);
            if (property == null || property.getId() == null) {
                throw new ApiException(400, "Seçilen ilan bulunamadı.");
            }
            return property;
        } catch (ApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ApiException(502, "İlan bilgisi şu anda doğrulanamadı.");
        }
    }
}
