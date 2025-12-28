package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.enums.ConversationStep;
import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler implements MessageHandler {

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.MAIN_MENU;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        String message = buildMainMenu();

        if ("show_initial".equals(request.getContextData())) {
            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.MAIN_MENU)
                .clearContext(true)
                .build();
        }

        Integer choice = request.getParsedChoice();
        if (choice == null) {
            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.MAIN_MENU)
                .clearContext(true)
                .build();
        }

        ConversationStep nextStep = getNextStepFromChoice(choice);

        if (nextStep == ConversationStep.MAIN_MENU) {
            return HandlerResponse.builder()
                .message(message)
                .nextStep(nextStep)
                .clearContext(true)
                .build();
        }

        return HandlerResponse.builder()
            .message("")
            .nextStep(nextStep)
            .contextData("show_initial")
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.MAIN_MENU;
    }

    private String buildMainMenu() {
        return """
            🏥 *Southview Medical Centre*

            Welcome! How can I help you today?

            1️⃣ Hospital Information
            2️⃣ Services & Procedures
            3️⃣ Price Information
            4️⃣ Test Preparation Guides
            5️⃣ Health Campaigns
            6️⃣ Lab Results Inquiry
            7️⃣ Frequently Asked Questions

            Reply with a number (1-7)
            """;
    }

    private ConversationStep getNextStepFromChoice(Integer choice) {
        return switch (choice) {
            case 1 -> ConversationStep.HOSPITAL_INFO;
            case 2 -> ConversationStep.SERVICE_LIST;
            case 3 -> ConversationStep.PRICE_CATEGORIES;
            case 4 -> ConversationStep.PREPARATION_CATEGORIES;
            case 5 -> ConversationStep.CAMPAIGN_LIST;
            case 6 -> ConversationStep.LAB_RESULTS_INQUIRY;
            case 7 -> ConversationStep.FAQ_CATEGORIES;
            default -> ConversationStep.MAIN_MENU;
        };
    }
}
