package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.Campaign;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.CampaignRepository;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class CampaignDetailHandler implements MessageHandler {

    private final CampaignRepository campaignRepository;

    public CampaignDetailHandler(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.CAMPAIGN_DETAIL;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if (request.getContextData() != null && !request.getContextData().isEmpty()) {
            try {
                Long campaignId = Long.parseLong(request.getContextData());
                Optional<Campaign> campaignOpt = campaignRepository.findById(campaignId);

                if (campaignOpt.isPresent()) {
                    Campaign campaign = campaignOpt.get();
                    String message = buildCampaignDetail(campaign);
                    message += "\n0️⃣ Main Menu";

                    return HandlerResponse.builder()
                        .message(message)
                        .nextStep(ConversationStep.CAMPAIGN_DETAIL)
                        .clearContext(true)
                        .build();
                }
            } catch (NumberFormatException e) {
                // Invalid campaign ID
            }
        }

        if ("0".equals(request.getUserInput().trim())) {
            return HandlerResponse.builder()
                .message("")
                .nextStep(ConversationStep.MAIN_MENU)
                .contextData("show_initial")
                .build();
        }

        String message = "⚠️ Campaign not found.";
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.CAMPAIGN_DETAIL)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.CAMPAIGN_DETAIL;
    }

    private String buildCampaignDetail(Campaign campaign) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        sb.append("📢 *").append(campaign.getTitle()).append("*\n\n");

        sb.append("📅 *Campaign Period:*\n");
        sb.append(campaign.getStartDate().format(formatter));
        sb.append(" - ");
        sb.append(campaign.getEndDate().format(formatter));
        sb.append("\n\n");

        if (campaign.getDescription() != null && !campaign.getDescription().isEmpty()) {
            sb.append(campaign.getDescription());
        } else {
            sb.append("No additional details available.");
        }

        return sb.toString();
    }
}
