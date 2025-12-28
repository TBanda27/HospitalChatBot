package com.banda.chatbot.entity;

import com.banda.chatbot.enums.ConversationStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStep currentStep;

    @Column(columnDefinition = "TEXT")
    private String contextData;

    @Column(nullable = false)
    private LocalDateTime lastActivity;

    @PrePersist
    @PreUpdate
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
}
