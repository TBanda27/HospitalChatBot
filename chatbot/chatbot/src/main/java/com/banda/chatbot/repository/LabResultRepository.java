package com.banda.chatbot.repository;

import com.banda.chatbot.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    Optional<LabResult> findByReferenceNumber(String referenceNumber);
}
