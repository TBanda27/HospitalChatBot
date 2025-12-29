package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.Campaign;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.CampaignRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CampaignListHandler implements MessageHandler {

    private final CampaignRepository campaignRepository;

    public CampaignListHandler(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.CAMPAIGN_LIST;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildCategoriesList();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.CAMPAIGN_LIST)
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
                String message = buildCampaignsList(selectedCategory);
                message += "\n0️⃣ Main Menu";

                return HandlerResponse.builder()
                    .message(message)
                    .nextStep(ConversationStep.CAMPAIGN_LIST)
                    .contextData(selectedCategory)
                    .build();
            }
        }

        if (choice != null && request.getContextData() != null) {
            List<Campaign> campaigns = getActiveCampaigns(request.getContextData());
            if (choice > 0 && choice <= campaigns.size()) {
                Campaign selectedCampaign = campaigns.get(choice - 1);
                return HandlerResponse.builder()
                    .message("")
                    .nextStep(ConversationStep.CAMPAIGN_DETAIL)
                    .contextData(String.valueOf(selectedCampaign.getId()))
                    .build();
            }
        }

        String message = "⚠️ Invalid input.\n\n";
        if (request.getContextData() != null) {
            message += buildCampaignsList(request.getContextData());
        } else {
            message += buildCategoriesList();
        }
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.CAMPAIGN_LIST)
            .clearContext(false)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.CAMPAIGN_LIST;
    }

    private String buildCategoriesList() {
        StringBuilder sb = new StringBuilder();
        sb.append("📢 *Health Campaigns*\n\n");
        sb.append("Select a category:\n\n");

        List<String> categories = getActiveCategories();

        if (categories.isEmpty()) {
            sb.append("No active campaigns at the moment.");
        } else {
            for (int i = 0; i < categories.size(); i++) {
                sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
            }
        }

        return sb.toString();
    }

    private String buildCampaignsList(String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("📢 *").append(category).append("*\n\n");

        List<Campaign> campaigns = getActiveCampaigns(category);

        if (campaigns.isEmpty()) {
            sb.append("No active campaigns in this category.\n");
        } else {
            sb.append("Select a campaign for more details:\n\n");
            for (int i = 0; i < campaigns.size(); i++) {
                Campaign campaign = campaigns.get(i);
                sb.append(i + 1).append(". ").append(campaign.getTitle()).append("\n");
            }
        }

        return sb.toString();
    }

    private List<String> getActiveCategories() {
        return campaignRepository.findByActiveTrueOrderByCategoryAsc()
            .stream()
            .map(Campaign::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Campaign> getActiveCampaigns(String category) {
        return campaignRepository.findByCategoryAndActiveTrueOrderByIdAsc(category);
    }
}
