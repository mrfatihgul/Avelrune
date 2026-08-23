package com.avelrune.property.service.impl;

import com.avelrune.property.dto.FilterMetaResponse;
import com.avelrune.property.dto.PageResponse;
import com.avelrune.property.dto.PropertyDetailResponse;
import com.avelrune.property.dto.PropertyStatsResponse;
import com.avelrune.property.dto.PropertySummaryResponse;
import com.avelrune.property.dto.PropertyWriteRequest;
import com.avelrune.property.entity.Agent;
import com.avelrune.property.entity.Property;
import com.avelrune.property.entity.PropertyImage;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;
import com.avelrune.property.exception.ApiException;
import com.avelrune.property.repository.AgentRepository;
import com.avelrune.property.repository.PropertyRepository;
import com.avelrune.property.service.PropertyMapper;
import com.avelrune.property.service.PropertyService;
import com.avelrune.property.service.PropertySpecifications;
import com.avelrune.property.service.SlugService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final AgentRepository agentRepository;
    private final SlugService slugService;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            AgentRepository agentRepository,
            SlugService slugService
    ) {
        this.propertyRepository = propertyRepository;
        this.agentRepository = agentRepository;
        this.slugService = slugService;
    }

    @Override
    public PageResponse<PropertySummaryResponse> search(
            ListingType listingType,
            String city,
            String district,
            PropertyType propertyType,
            Long minPrice,
            Long maxPrice,
            String rooms,
            Integer minArea,
            Integer maxArea,
            Integer maxBuildingAge,
            Boolean featured,
            boolean includeInactive,
            String sort,
            int page,
            int size
    ) {
        Specification<Property> spec = PropertySpecifications.withFilters(
                listingType, city, district, propertyType, minPrice, maxPrice,
                rooms, minArea, maxArea, maxBuildingAge, featured,
                includeInactive ? null : true
        );
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 24), resolveSort(sort));
        Page<Property> result = propertyRepository.findAll(spec, pageable);
        return new PageResponse<>(
                result.getContent().stream().map(PropertyMapper::toSummary).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Override
    public List<PropertySummaryResponse> featured() {
        return propertyRepository.findByFeaturedTrueAndActiveTrueOrderByUpdatedAtDesc()
                .stream()
                .map(PropertyMapper::toSummary)
                .toList();
    }

    @Override
    public List<PropertySummaryResponse> listAssignedTo(Long userId) {
        return propertyRepository.findByAssignedUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(PropertyMapper::toSummary)
                .toList();
    }

    @Override
    public PropertyDetailResponse getBySlug(String slug) {
        Property property = propertyRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        return PropertyMapper.toDetail(property);
    }

    @Override
    public PropertyDetailResponse getById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        return PropertyMapper.toDetail(property);
    }

    @Override
    public List<PropertySummaryResponse> similar(String slug) {
        Property property = propertyRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        Specification<Property> spec = (root, query, cb) -> cb.and(
                cb.isTrue(root.get("active")),
                cb.equal(cb.lower(root.get("city")), property.getCity().toLowerCase(Locale.ROOT)),
                cb.notEqual(root.get("id"), property.getId())
        );
        return propertyRepository.findAll(spec, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "featured", "updatedAt")))
                .getContent()
                .stream()
                .map(PropertyMapper::toSummary)
                .toList();
    }

    @Override
    public FilterMetaResponse meta() {
        List<Property> properties = propertyRepository.findAll();
        Map<String, List<String>> cities = new LinkedHashMap<>();
        for (Property property : properties) {
            cities.computeIfAbsent(property.getCity(), key -> new ArrayList<>());
            if (!cities.get(property.getCity()).contains(property.getDistrict())) {
                cities.get(property.getCity()).add(property.getDistrict());
            }
        }
        List<FilterMetaResponse.CityOption> cityOptions = cities.entrySet().stream()
                .map(entry -> new FilterMetaResponse.CityOption(entry.getKey(), entry.getValue()))
                .toList();
        return new FilterMetaResponse(
                cityOptions,
                List.of(
                        new FilterMetaResponse.LabelValue("APARTMENT", "Daire"),
                        new FilterMetaResponse.LabelValue("RESIDENCE", "Rezidans"),
                        new FilterMetaResponse.LabelValue("VILLA", "Villa"),
                        new FilterMetaResponse.LabelValue("HOUSE", "Müstakil Ev"),
                        new FilterMetaResponse.LabelValue("YALI", "Yalı"),
                        new FilterMetaResponse.LabelValue("STONE_HOUSE", "Taş Ev")
                ),
                List.of(
                        new FilterMetaResponse.LabelValue("SALE", "Satılık"),
                        new FilterMetaResponse.LabelValue("RENT", "Kiralık")
                )
        );
    }

    @Override
    public PropertyStatsResponse stats() {
        return new PropertyStatsResponse(
                propertyRepository.count(),
                propertyRepository.countByActiveTrue(),
                propertyRepository.countByActiveFalse(),
                propertyRepository.countByListingTypeAndActiveTrue(ListingType.SALE),
                propertyRepository.countByListingTypeAndActiveTrue(ListingType.RENT),
                propertyRepository.countByFeaturedTrueAndActiveTrue()
        );
    }

    @Override
    @Transactional
    public PropertyDetailResponse create(PropertyWriteRequest request, Long actorId, boolean admin) {
        Property property = new Property();
        apply(property, request, true, admin);
        if (!admin) {
            property.setAssignedUserId(actorId);
            property.setFeatured(false);
            property.setActive(true);
        }
        return PropertyMapper.toDetail(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public PropertyDetailResponse create(PropertyWriteRequest request) {
        return create(request, request.getAssignedUserId(), true);
    }

    @Override
    @Transactional
    public PropertyDetailResponse update(Long id, PropertyWriteRequest request, Long actorId, boolean admin) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        assertOwner(property, actorId, admin);
        apply(property, request, false, admin);
        if (!admin) {
            property.setAssignedUserId(property.getAssignedUserId() == null ? actorId : property.getAssignedUserId());
            property.setFeatured(property.isFeatured());
        }
        return PropertyMapper.toDetail(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public PropertyDetailResponse update(Long id, PropertyWriteRequest request) {
        return update(id, request, null, true);
    }

    @Override
    @Transactional
    public void delete(Long id, Long actorId, boolean admin) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        assertOwner(property, actorId, admin);
        propertyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        delete(id, null, true);
    }

    @Override
    public PropertyDetailResponse getOwned(Long id, Long actorId, boolean admin) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        assertOwner(property, actorId, admin);
        return PropertyMapper.toDetail(property);
    }

    @Override
    public PropertyDetailResponse getActiveById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        if (!property.isActive()) {
            throw new ApiException(404, "İlan bulunamadı.");
        }
        return PropertyMapper.toDetail(property);
    }

    @Override
    @Transactional
    public PropertyDetailResponse toggleActive(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        property.setActive(!property.isActive());
        return PropertyMapper.toDetail(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public PropertyDetailResponse toggleFeatured(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "İlan bulunamadı."));
        property.setFeatured(!property.isFeatured());
        return PropertyMapper.toDetail(propertyRepository.save(property));
    }

    private void assertOwner(Property property, Long actorId, boolean admin) {
        if (admin) {
            return;
        }
        if (actorId == null || property.getAssignedUserId() == null || !actorId.equals(property.getAssignedUserId())) {
            throw new ApiException(403, "Bu ilanı yalnızca sahibi yönetebilir.");
        }
    }

    private void apply(Property property, PropertyWriteRequest request, boolean creating, boolean admin) {
        if (request.getAgentId() != null) {
            Agent agent = agentRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ApiException(400, "Danışman bulunamadı."));
            property.setAgent(agent);
        } else if (creating) {
            property.setAgent(null);
        }
        property.setTitle(request.getTitle().trim());
        property.setSlug(uniqueSlug(slugService.slugify(request.getTitle()), creating ? null : property.getId()));
        property.setDescription(request.getDescription().trim());
        property.setPrice(request.getPrice());
        property.setListingType(request.getListingType());
        property.setPropertyType(request.getPropertyType());
        property.setCity(request.getCity().trim());
        property.setDistrict(request.getDistrict().trim());
        property.setNeighborhood(request.getNeighborhood().trim());
        property.setGrossArea(request.getGrossArea());
        property.setNetArea(request.getNetArea());
        property.setRoomCount(request.getRoomCount().trim());
        property.setBuildingAge(request.getBuildingAge());
        property.setFloor(request.getFloor());
        property.setTotalFloors(request.getTotalFloors());
        property.setHeatingType(request.getHeatingType());
        property.setFurnished(request.isFurnished());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        if (admin) {
            property.setFeatured(request.isFeatured());
            property.setActive(request.isActive());
            property.setAssignedUserId(request.getAssignedUserId());
        }
        property.setFeatures(request.getFeatures() == null ? "" : String.join("|", request.getFeatures()));
        property.clearImages();
        int order = 0;
        for (String url : request.getImageUrls()) {
            PropertyImage image = new PropertyImage();
            image.setUrl(url);
            image.setAltText(request.getTitle());
            image.setSortOrder(order++);
            property.addImage(image);
        }
    }

    private String uniqueSlug(String base, Long currentId) {
        String slug = base;
        int i = 2;
        while (true) {
            boolean taken = currentId == null
                    ? propertyRepository.existsBySlug(slug)
                    : propertyRepository.existsBySlugAndIdNot(slug, currentId);
            if (!taken) {
                return slug;
            }
            slug = base + "-" + i++;
        }
    }

    private Sort resolveSort(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "area" -> Sort.by(Sort.Direction.DESC, "grossArea");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
