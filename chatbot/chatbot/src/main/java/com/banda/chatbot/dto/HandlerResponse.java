package com.banda.chatbot.dto;

import com.banda.chatbot.enums.ConversationStep;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandlerResponse {
    private String message;
    private ConversationStep nextStep;
    private String contextData;
    private boolean clearContext;
}
