package com.banda.chatbot.repository;

import com.banda.chatbot.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByActiveTrueOrderByCategoryAsc();
    List<FAQ> findByCategoryAndActiveTrueOrderByIdAsc(String category);
}
