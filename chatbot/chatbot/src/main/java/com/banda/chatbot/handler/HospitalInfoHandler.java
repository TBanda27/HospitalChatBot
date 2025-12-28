package com.banda.chatbot.handler;

import com.banda.chatbot.config.HospitalConfig;
import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.enums.ConversationStep;
import org.springframework.stereotype.Component;

@Component
public class HospitalInfoHandler implements MessageHandler {

    private final HospitalConfig hospitalConfig;

    public HospitalInfoHandler(HospitalConfig hospitalConfig) {
        this.hospitalConfig = hospitalConfig;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.HOSPITAL_INFO;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildHospitalInfo();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.HOSPITAL_INFO)
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

        String message = "⚠️ Invalid input.\n\n" + buildHospitalInfo();
        message += "\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.HOSPITAL_INFO)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.HOSPITAL_INFO;
    }

    private String buildHospitalInfo() {
        StringBuilder info = new StringBuilder();

        info.append("🏥 *").append(hospitalConfig.getName()).append("*\n\n");

        info.append("📍 *Address:*\n");
        info.append(hospitalConfig.getAddress()).append("\n");
        info.append(hospitalConfig.getGoogleMapsLink()).append("\n\n");

        info.append("📞 *Contact:*\n");
        info.append("Phone: ").append(hospitalConfig.getMainPhone()).append("\n");
        info.append("Email: ").append(hospitalConfig.getEmail()).append("\n\n");

        info.append("🕐 *Operating Hours:*\n");
        info.append(hospitalConfig.getOperatingHours()).append("\n\n");

        info.append("🚗 *Parking:*\n");
        info.append(hospitalConfig.getParkingInfo()).append("\n\n");

        info.append("🚌 *Transport:*\n");
        info.append(hospitalConfig.getTransportRoutes()).append("\n\n");

        info.append("🚨 *Emergency:*\n");
        info.append("Primary Line: ").append(hospitalConfig.getEmergency().getPrimaryLine()).append("\n");
        info.append("Ambulance: ").append(hospitalConfig.getEmergency().getAmbulanceLine()).append("\n");
        info.append(hospitalConfig.getEmergency().getInstructions()).append("\n\n");

        info.append("🏥 *Accepted Insurance:*\n");
        hospitalConfig.getInsurance().getAccepted().forEach(provider ->
            info.append("• ").append(provider.getName()).append("\n")
        );

        return info.toString();
    }
}
