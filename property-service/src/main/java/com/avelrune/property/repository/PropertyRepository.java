package com.avelrune.property.repository;

import com.avelrune.property.entity.Property;
import com.avelrune.property.enums.ListingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    @Override
    @EntityGraph(attributePaths = "images")
    Page<Property> findAll(Specification<Property> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"images", "agent"})
    Optional<Property> findBySlugAndActiveTrue(String slug);

    @EntityGraph(attributePaths = {"images", "agent"})
    Optional<Property> findById(Long id);

    @EntityGraph(attributePaths = {"images", "agent"})
    Optional<Property> findBySlug(String slug);

    @Query("select p.id from Property p where p.active = true and p.featured = true")
    List<Long> findActiveFeaturedIds();

    @EntityGraph(attributePaths = "images")
    List<Property> findByIdIn(Collection<Long> ids);

    long countByActiveTrue();

    long countByActiveFalse();

    long countByListingTypeAndActiveTrue(ListingType listingType);

    long countByFeaturedTrueAndActiveTrue();

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @EntityGraph(attributePaths = "images")
    List<Property> findByAssignedUserIdOrderByUpdatedAtDesc(Long assignedUserId);

    @Query("select distinct p.city, p.district from Property p order by p.city, p.district")
    List<Object[]> findDistinctCityDistricts();
}
