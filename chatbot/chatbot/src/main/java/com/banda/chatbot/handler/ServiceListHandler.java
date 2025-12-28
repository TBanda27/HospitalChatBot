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
public class ServiceListHandler implements MessageHandler {

    private final ServiceRepository serviceRepository;

    public ServiceListHandler(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.SERVICE_LIST;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildCategoriesList();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.SERVICE_LIST)
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
            List<String> categories = getCategories();
            if (choice > 0 && choice <= categories.size()) {
                String selectedCategory = categories.get(choice - 1);
                String message = buildServicesList(selectedCategory);
                message += "\n0️⃣ Main Menu";

                return HandlerResponse.builder()
                    .message(message)
                    .nextStep(ConversationStep.SERVICE_LIST)
                    .contextData(selectedCategory)
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
            .nextStep(ConversationStep.SERVICE_LIST)
            .clearContext(false)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.SERVICE_LIST;
    }

    private String buildCategoriesList() {
        StringBuilder sb = new StringBuilder();
        sb.append("🏥 *Services & Procedures*\n\n");
        sb.append("Select a category:\n\n");

        List<String> categories = getCategories();
        for (int i = 0; i < categories.size(); i++) {
            sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
        }

        return sb.toString();
    }

    private String buildServicesList(String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏥 *").append(category).append("*\n\n");

        List<Service> services = serviceRepository.findByCategoryAndActiveTrueOrderByIdAsc(category);

        if (services.isEmpty()) {
            sb.append("No services available in this category.\n");
        } else {
            for (int i = 0; i < services.size(); i++) {
                Service service = services.get(i);
                sb.append(i + 1).append(". ").append(service.getName());
                if (service.getPriceMin() != null && service.getPriceMax() != null) {
                    sb.append(" ($").append(service.getPriceMin())
                      .append("-$").append(service.getPriceMax()).append(")");
                }
                sb.append("\n");
            }
            sb.append("\n_Prices in USD. ZWL accepted at prevailing rates_\n");
        }

        return sb.toString();
    }

    private List<String> getCategories() {
        return serviceRepository.findByActiveTrueOrderByCategoryAsc()
            .stream()
            .map(Service::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }
}
