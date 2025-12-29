package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.FAQ;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.FAQRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FAQQuestionsHandler implements MessageHandler {

    private final FAQRepository faqRepository;

    public FAQQuestionsHandler(FAQRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.FAQ_QUESTIONS;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if (request.getContextData() != null && !request.getContextData().isEmpty()) {
            try {
                Long faqId = Long.parseLong(request.getContextData());
                Optional<FAQ> faqOpt = faqRepository.findById(faqId);

                if (faqOpt.isPresent()) {
                    FAQ faq = faqOpt.get();
                    String message = buildFAQAnswer(faq);
                    message += "\n0️⃣ Main Menu";

                    return HandlerResponse.builder()
                        .message(message)
                        .nextStep(ConversationStep.FAQ_QUESTIONS)
                        .clearContext(true)
                        .build();
                }
            } catch (NumberFormatException e) {
                // Invalid FAQ ID
            }
        }

        if ("0".equals(request.getUserInput().trim())) {
            return HandlerResponse.builder()
                .message("")
                .nextStep(ConversationStep.MAIN_MENU)
                .contextData("show_initial")
                .build();
        }

        String message = "⚠️ FAQ not found.";
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.FAQ_QUESTIONS)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.FAQ_QUESTIONS;
    }

    private String buildFAQAnswer(FAQ faq) {
        StringBuilder sb = new StringBuilder();

        sb.append("❓ *").append(faq.getQuestion()).append("*\n\n");

        if (faq.getAnswer() != null && !faq.getAnswer().isEmpty()) {
            sb.append(faq.getAnswer());
        } else {
            sb.append("Answer not available.");
        }

        return sb.toString();
    }
}
