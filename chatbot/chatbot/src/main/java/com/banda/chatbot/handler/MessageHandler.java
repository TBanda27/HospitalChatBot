package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.enums.ConversationStep;

public interface MessageHandler {
    boolean canHandle(ConversationStep step);

    HandlerResponse handle(HandlerRequest request);

    ConversationStep getHandledStep();
}
