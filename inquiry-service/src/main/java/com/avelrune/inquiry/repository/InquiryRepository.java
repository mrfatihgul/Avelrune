package com.avelrune.inquiry.repository;

import com.avelrune.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    long countByReadFlagFalse();

    List<Inquiry> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    List<Inquiry> findByPropertyIdAndOwnerUserIdOrderByCreatedAtDesc(Long propertyId, Long ownerUserId);

    List<Inquiry> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);
}
