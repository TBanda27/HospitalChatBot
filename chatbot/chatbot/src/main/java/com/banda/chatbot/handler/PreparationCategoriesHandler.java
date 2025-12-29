package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.Service;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.ServiceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PreparationCategoriesHandler implements MessageHandler {

    private final ServiceRepository serviceRepository;

    public PreparationCategoriesHandler(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.PREPARATION_CATEGORIES;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildCategoriesList();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.PREPARATION_CATEGORIES)
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
            List<String> categories = getCategoriesWithPreparation();
            if (choice > 0 && choice <= categories.size()) {
                String selectedCategory = categories.get(choice - 1);
                String message = buildServicesList(selectedCategory);
                message += "\n0️⃣ Main Menu";

                return HandlerResponse.builder()
                    .message(message)
                    .nextStep(ConversationStep.PREPARATION_CATEGORIES)
                    .contextData(selectedCategory)
                    .build();
            }
        }

        if (choice != null && request.getContextData() != null) {
            List<Service> services = getServicesWithPreparation(request.getContextData());
            if (choice > 0 && choice <= services.size()) {
                Service selectedService = services.get(choice - 1);
                return HandlerResponse.builder()
                    .message("")
                    .nextStep(ConversationStep.PREPARATION_DETAIL)
                    .contextData(String.valueOf(selectedService.getId()))
                    .build();
            }
        }

        String message = "⚠️ Invalid input.\n\n";
        if (request.getContextData() != null) {
            message += buildServicesList(request.getContextData());
        } else {
            message += buildCategoriesList();
        }
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.PREPARATION_CATEGORIES)
            .clearContext(false)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.PREPARATION_CATEGORIES;
    }

    private String buildCategoriesList() {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 *Test Preparation Guides*\n\n");
        sb.append("Select a category:\n\n");

        List<String> categories = getCategoriesWithPreparation();

        if (categories.isEmpty()) {
            sb.append("No preparation guides available at the moment.");
        } else {
            for (int i = 0; i < categories.size(); i++) {
                sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
            }
        }

        return sb.toString();
    }

    private String buildServicesList(String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 *").append(category).append(" - Preparation Guides*\n\n");

        List<Service> services = getServicesWithPreparation(category);

        if (services.isEmpty()) {
            sb.append("No preparation guides available for this category.\n");
        } else {
            sb.append("Select a service to see preparation details:\n\n");
            for (int i = 0; i < services.size(); i++) {
                Service service = services.get(i);
                sb.append(i + 1).append(". ").append(service.getName()).append("\n");
            }
        }

        return sb.toString();
    }

    private List<String> getCategoriesWithPreparation() {
        return serviceRepository.findByActiveTrueOrderByCategoryAsc()
            .stream()
            .filter(service -> service.getPreparationGuide() != null && !service.getPreparationGuide().isEmpty())
            .map(Service::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Service> getServicesWithPreparation(String category) {
        return serviceRepository.findByCategoryAndActiveTrueOrderByIdAsc(category)
            .stream()
            .filter(service -> service.getPreparationGuide() != null && !service.getPreparationGuide().isEmpty())
            .collect(Collectors.toList());
    }
}
