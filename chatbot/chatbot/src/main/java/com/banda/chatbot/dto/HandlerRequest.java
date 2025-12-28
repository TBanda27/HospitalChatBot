package com.banda.chatbot.dto;

import com.banda.chatbot.enums.ConversationStep;
import lombok.Builder;
import lombok.Data;

/**
 * Request object passed to message handlers
 * Contains all information needed to process a user's message
 */
@Data
@Builder
public class HandlerRequest {
    private String phoneNumber;

    private String userInput;

    private Integer parsedChoice;

    private ConversationStep currentStep;

    private String contextData;
}
