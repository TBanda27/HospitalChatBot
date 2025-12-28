package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageHandlerDispatcher {

    private final List<MessageHandler> handlers;
    private final MessageHandler fallbackHandler;

    public MessageHandlerDispatcher(List<MessageHandler> handlers,
                                   FallbackMessageHandler fallbackHandler) {
        this.handlers = handlers;
        this.fallbackHandler = fallbackHandler;
    }

    public HandlerResponse dispatch(HandlerRequest request) {
        return handlers.stream()
            .filter(handler -> handler.canHandle(request.getCurrentStep()))
            .findFirst()
            .orElse(fallbackHandler)
            .handle(request);
    }
}
