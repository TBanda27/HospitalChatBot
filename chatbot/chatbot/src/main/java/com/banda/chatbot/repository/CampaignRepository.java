package com.banda.chatbot.repository;

import com.banda.chatbot.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByActiveTrueOrderByCategoryAsc();
    List<Campaign> findByCategoryAndActiveTrueOrderByIdAsc(String category);
}
