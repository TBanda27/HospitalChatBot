package com.banda.chatbot.handler;

import com.banda.chatbot.config.HospitalConfig;
import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.enums.ConversationStep;
import org.springframework.stereotype.Component;

@Component
public class PriceCategoriesHandler implements MessageHandler {

    private final HospitalConfig hospitalConfig;

    public PriceCategoriesHandler(HospitalConfig hospitalConfig) {
        this.hospitalConfig = hospitalConfig;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.PRICE_CATEGORIES;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildPriceInfo();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.PRICE_CATEGORIES)
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

        String message = "⚠️ Invalid input.\n\n" + buildPriceInfo();
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.PRICE_CATEGORIES)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.PRICE_CATEGORIES;
    }

    private String buildPriceInfo() {
        StringBuilder info = new StringBuilder();

        info.append("💳 *Payment & Billing Information*\n\n");

        info.append("📋 *Payment Methods Accepted:*\n");
        hospitalConfig.getPaymentMethods().forEach(payment -> {
            info.append("• ").append(payment.getName());
            if (payment.getInfo() != null && !payment.getInfo().isEmpty()) {
                info.append("\n  ").append(payment.getInfo());
            }
            info.append("\n");
        });
        info.append("\n");

        info.append("🏥 *Insurance:*\n");
        info.append("We accept medical aid from:\n");
        hospitalConfig.getInsurance().getAccepted().forEach(provider ->
            info.append("• ").append(provider.getName()).append("\n")
        );
        info.append("\nPlease bring your valid insurance card and ID.\n\n");

        info.append("💵 *Currency:*\n");
        info.append("• All prices in USD\n");
        info.append("• ZWL accepted at current exchange rates\n\n");

        info.append("ℹ️ *Note:*\n");
        info.append("For detailed service prices, select *Services & Procedures* from the main menu.");

        return info.toString();
    }
}
