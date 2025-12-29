package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.FAQ;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.FAQRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FAQCategoriesHandler implements MessageHandler {

    private final FAQRepository faqRepository;

    public FAQCategoriesHandler(FAQRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.FAQ_CATEGORIES;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildCategoriesList();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.FAQ_CATEGORIES)
                .clearContext(true)
                .build();
        }

        if ("0".equals(request.getUserInput().trim())) {
            return HandlerResponse.builder()
                .message("")
                .nextStep(ConversationStep.MAIN_MENU)
                .contextData("show_initial")
                .build();
        }

        Integer choice = request.getParsedChoice();
        if (choice != null && request.getContextData() == null) {
            List<String> categories = getActiveCategories();
            if (choice > 0 && choice <= categories.size()) {
                String selectedCategory = categories.get(choice - 1);
                String message = buildQuestionsList(selectedCategory);
                message += "\n0️⃣ Main Menu";

                return HandlerResponse.builder()
                    .message(message)
                    .nextStep(ConversationStep.FAQ_CATEGORIES)
                    .contextData(selectedCategory)
                    .build();
            }
        }

        if (choice != null && request.getContextData() != null) {
            List<FAQ> faqs = getActiveFAQs(request.getContextData());
            if (choice > 0 && choice <= faqs.size()) {
                FAQ selectedFAQ = faqs.get(choice - 1);
                return HandlerResponse.builder()
                    .message("")
                    .nextStep(ConversationStep.FAQ_QUESTIONS)
                    .contextData(String.valueOf(selectedFAQ.getId()))
                    .build();
            }
        }

        String message = "⚠️ Invalid input.\n\n";
        if (request.getContextData() != null) {
            message += buildQuestionsList(request.getContextData());
        } else {
            message += buildCategoriesList();
        }
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.FAQ_CATEGORIES)
            .clearContext(false)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.FAQ_CATEGORIES;
    }

    private String buildCategoriesList() {
        StringBuilder sb = new StringBuilder();
        sb.append("❓ *Frequently Asked Questions*\n\n");
        sb.append("Select a category:\n\n");

        List<String> categories = getActiveCategories();

        if (categories.isEmpty()) {
            sb.append("No FAQs available at the moment.");
        } else {
            for (int i = 0; i < categories.size(); i++) {
                sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
            }
        }

        return sb.toString();
    }

    private String buildQuestionsList(String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("❓ *").append(category).append("*\n\n");

        List<FAQ> faqs = getActiveFAQs(category);

        if (faqs.isEmpty()) {
            sb.append("No questions available in this category.\n");
        } else {
            sb.append("Select a question:\n\n");
            for (int i = 0; i < faqs.size(); i++) {
                FAQ faq = faqs.get(i);
                sb.append(i + 1).append(". ").append(faq.getQuestion()).append("\n");
            }
        }

        return sb.toString();
    }

    private List<String> getActiveCategories() {
        return faqRepository.findByActiveTrueOrderByCategoryAsc()
            .stream()
            .map(FAQ::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }

    private List<FAQ> getActiveFAQs(String category) {
        return faqRepository.findByCategoryAndActiveTrueOrderByIdAsc(category);
    }
}
