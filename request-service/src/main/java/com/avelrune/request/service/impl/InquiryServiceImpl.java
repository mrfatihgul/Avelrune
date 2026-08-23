package com.avelrune.request.service.impl;

import com.avelrune.request.client.PropertyClient;
import com.avelrune.request.dto.InquiryRequest;
import com.avelrune.request.dto.InquiryResponse;
import com.avelrune.request.dto.InquiryStatsResponse;
import com.avelrune.request.dto.PropertyMessageSummary;
import com.avelrune.request.dto.PropertyRef;
import com.avelrune.request.entity.Inquiry;
import com.avelrune.request.exception.ApiException;
import com.avelrune.request.repository.InquiryRepository;
import com.avelrune.request.service.InquiryService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final PropertyClient propertyClient;

    public InquiryServiceImpl(InquiryRepository inquiryRepository, PropertyClient propertyClient) {
        this.inquiryRepository = inquiryRepository;
        this.propertyClient = propertyClient;
    }

    @Override
    @Transactional
    public InquiryResponse create(InquiryRequest request, Long senderUserId) {
        String slug = blankToNull(request.getPropertySlug());
        if (slug == null && request.getPropertyId() == null) {
            Inquiry entity = baseInquiry(request, senderUserId);
            return toResponse(inquiryRepository.save(entity));
        }
        PropertyRef property = resolveProperty(request);
        if (property.getAssignedUserId() == null) {
            throw new ApiException(400, "Bu ilanın sahibi yok, mesaj gönderilemiyor.");
        }
        if (senderUserId != null && senderUserId.equals(property.getAssignedUserId())) {
            throw new ApiException(400, "Kendi ilanınıza mesaj gönderemezsiniz.");
        }
        Inquiry entity = baseInquiry(request, senderUserId);
        entity.setPropertySlug(property.getSlug());
        entity.setPropertyId(property.getId());
        entity.setOwnerUserId(property.getAssignedUserId());
        return toResponse(inquiryRepository.save(entity));
    }

    @Override
    public List<InquiryResponse> list() {
        return inquiryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<InquiryResponse> listForProperty(Long propertyId, Long actorId, boolean admin) {
        if (admin) {
            return inquiryRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (actorId == null) {
            throw new ApiException(401, "Giriş yapmanız gerekiyor.");
        }
        return inquiryRepository.findByPropertyIdAndOwnerUserIdOrderByCreatedAtDesc(propertyId, actorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<PropertyMessageSummary> summaryForOwner(Long ownerId) {
        if (ownerId == null) {
            throw new ApiException(401, "Giriş yapmanız gerekiyor.");
        }
        Map<Long, PropertyMessageSummary> map = new LinkedHashMap<>();
        for (Inquiry inquiry : inquiryRepository.findByOwnerUserIdOrderByCreatedAtDesc(ownerId)) {
            if (inquiry.getPropertyId() == null) {
                continue;
            }
            PropertyMessageSummary current = map.get(inquiry.getPropertyId());
            long total = (current == null ? 0 : current.getTotal()) + 1;
            long unread = (current == null ? 0 : current.getUnread()) + (inquiry.isReadFlag() ? 0 : 1);
            map.put(inquiry.getPropertyId(), new PropertyMessageSummary(inquiry.getPropertyId(), total, unread));
        }
        return List.copyOf(map.values());
    }

    @Override
    @Transactional
    public InquiryResponse markRead(Long id, Long actorId, boolean admin) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Talep bulunamadı."));
        if (!admin && (actorId == null || !actorId.equals(inquiry.getOwnerUserId()))) {
            throw new ApiException(403, "Bu mesajı yalnızca ilan sahibi görebilir.");
        }
        inquiry.setReadFlag(true);
        return toResponse(inquiryRepository.save(inquiry));
    }

    @Override
    @Transactional
    public InquiryResponse markRead(Long id) {
        return markRead(id, null, true);
    }

    @Override
    public InquiryStatsResponse stats() {
        return new InquiryStatsResponse(inquiryRepository.count(), inquiryRepository.countByReadFlagFalse());
    }

    private Inquiry baseInquiry(InquiryRequest request, Long senderUserId) {
        if (blankToNull(request.getFullName()) == null
                || blankToNull(request.getPhone()) == null
                || blankToNull(request.getEmail()) == null) {
            throw new ApiException(400, "Ad, telefon ve e-posta gerekli.");
        }
        Inquiry entity = new Inquiry();
        entity.setFullName(request.getFullName().trim());
        entity.setPhone(request.getPhone().trim());
        entity.setEmail(request.getEmail().trim().toLowerCase());
        entity.setMessage(request.getMessage().trim());
        entity.setSenderUserId(senderUserId);
        return entity;
    }

    private PropertyRef resolveProperty(InquiryRequest request) {
        String slug = blankToNull(request.getPropertySlug());
        if (slug != null) {
            return propertyClient.findBySlug(slug);
        }
        if (request.getPropertyId() != null) {
            return propertyClient.findById(request.getPropertyId());
        }
        throw new ApiException(400, "Mesaj bir ilana bağlı olmalıdır.");
    }

    private InquiryResponse toResponse(Inquiry entity) {
        return new InquiryResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getMessage(),
                entity.getPropertySlug(),
                entity.getPropertyId(),
                entity.getOwnerUserId(),
                entity.getSenderUserId(),
                entity.getSenderUserId() != null,
                entity.isReadFlag(),
                entity.getCreatedAt()
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
