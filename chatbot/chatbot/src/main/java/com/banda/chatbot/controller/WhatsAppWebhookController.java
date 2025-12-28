package com.banda.chatbot.controller;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.ConversationState;
import com.banda.chatbot.handler.MessageHandlerDispatcher;
import com.banda.chatbot.service.ConversationStateService;
import com.banda.chatbot.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {

    private final MessageHandlerDispatcher dispatcher;
    private final ConversationStateService stateService;
    private final WhatsAppService whatsAppService;

    @PostMapping(value = "/whatsapp", produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveMessage(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        log.info("Received message from {}: {}", from, body);

        String phoneNumber = extractPhoneNumber(from);
        ConversationState state = stateService.getOrCreate(phoneNumber);

        HandlerRequest request = HandlerRequest.builder()
            .phoneNumber(phoneNumber)
            .userInput(body.trim())
            .parsedChoice(parseChoice(body.trim()))
            .currentStep(state.getCurrentStep())
            .contextData(state.getContextData())
            .build();

        HandlerResponse response = dispatcher.dispatch(request);

        if (response.isClearContext()) {
            stateService.updateStepAndContext(phoneNumber, response.getNextStep(), null);
        } else if (response.getContextData() != null) {
            stateService.updateStepAndContext(phoneNumber, response.getNextStep(), response.getContextData());
        } else {
            stateService.updateStep(phoneNumber, response.getNextStep());
        }

        if ("show_initial".equals(response.getContextData())) {
            ConversationState updatedState = stateService.getOrCreate(phoneNumber);
            HandlerRequest followUpRequest = HandlerRequest.builder()
                .phoneNumber(phoneNumber)
                .userInput("")
                .parsedChoice(null)
                .currentStep(updatedState.getCurrentStep())
                .contextData(updatedState.getContextData())
                .build();

            response = dispatcher.dispatch(followUpRequest);

            if (response.isClearContext()) {
                stateService.updateStepAndContext(phoneNumber, response.getNextStep(), null);
            } else if (response.getContextData() != null) {
                stateService.updateStepAndContext(phoneNumber, response.getNextStep(), response.getContextData());
            } else {
                stateService.updateStep(phoneNumber, response.getNextStep());
            }
        }

        if (!response.getMessage().isEmpty()) {
            whatsAppService.sendMessage(phoneNumber, response.getMessage());
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>";
    }

    private String extractPhoneNumber(String from) {
        return from.replace("whatsapp:", "");
    }

    private Integer parseChoice(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
