package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.Service;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.ServiceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PreparationDetailHandler implements MessageHandler {

    private final ServiceRepository serviceRepository;

    public PreparationDetailHandler(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.PREPARATION_DETAIL;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if (request.getContextData() != null && !request.getContextData().isEmpty()) {
            try {
                Long serviceId = Long.parseLong(request.getContextData());
                Optional<Service> serviceOpt = serviceRepository.findById(serviceId);

                if (serviceOpt.isPresent()) {
                    Service service = serviceOpt.get();
                    String message = buildPreparationGuide(service);
                    message += "\n0️⃣ Main Menu";

                    return HandlerResponse.builder()
                        .message(message)
                        .nextStep(ConversationStep.PREPARATION_DETAIL)
                        .clearContext(true)
                        .build();
                }
            } catch (NumberFormatException e) {
                // Invalid service ID
            }
        }

        if ("0".equals(request.getUserInput().trim())) {
            return HandlerResponse.builder()
                .message("")
                .nextStep(ConversationStep.MAIN_MENU)
                .contextData("show_initial")
                .build();
        }

        String message = "⚠️ Service not found.";
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.PREPARATION_DETAIL)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.PREPARATION_DETAIL;
    }

    private String buildPreparationGuide(Service service) {
        StringBuilder sb = new StringBuilder();

        sb.append("📋 *").append(service.getName()).append("*\n\n");
        sb.append("*Preparation Guide:*\n\n");

        if (service.getPreparationGuide() != null && !service.getPreparationGuide().isEmpty()) {
            sb.append(service.getPreparationGuide());
        } else {
            sb.append("No special preparation required.");
        }

        return sb.toString();
    }
}
