package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.enums.ConversationStep;
import org.springframework.stereotype.Component;

@Component
public class FallbackMessageHandler implements MessageHandler {

    @Override
    public boolean canHandle(ConversationStep step) {
        return false;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        String message = "⚠️ Something went wrong. Let's start over!";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.MAIN_MENU)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return null;
    }
}
