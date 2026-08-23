package com.avelrune.request.repository;

import com.avelrune.request.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    long countByReadFlagFalse();

    List<Inquiry> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    List<Inquiry> findByPropertyIdAndOwnerUserIdOrderByCreatedAtDesc(Long propertyId, Long ownerUserId);

    List<Inquiry> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);

    long countByPropertyIdAndOwnerUserId(Long propertyId, Long ownerUserId);

    long countByPropertyIdAndOwnerUserIdAndReadFlagFalse(Long propertyId, Long ownerUserId);
}
