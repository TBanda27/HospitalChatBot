package com.banda.chatbot.handler;

import com.banda.chatbot.dto.HandlerRequest;
import com.banda.chatbot.dto.HandlerResponse;
import com.banda.chatbot.entity.LabResult;
import com.banda.chatbot.enums.ConversationStep;
import com.banda.chatbot.repository.LabResultRepository;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class LabResultsInquiryHandler implements MessageHandler {

    private final LabResultRepository labResultRepository;

    public LabResultsInquiryHandler(LabResultRepository labResultRepository) {
        this.labResultRepository = labResultRepository;
    }

    @Override
    public boolean canHandle(ConversationStep step) {
        return step == ConversationStep.LAB_RESULTS_INQUIRY;
    }

    @Override
    public HandlerResponse handle(HandlerRequest request) {
        if ("show_initial".equals(request.getContextData())) {
            String message = buildInitialMessage();
            message += "\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.LAB_RESULTS_INQUIRY)
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

        String referenceNumber = request.getUserInput().trim().toUpperCase();
        Optional<LabResult> labResultOpt = labResultRepository.findByReferenceNumber(referenceNumber);

        if (labResultOpt.isPresent()) {
            LabResult labResult = labResultOpt.get();
            String message = buildResultMessage(labResult);
            message += "\n\n0️⃣ Main Menu";

            return HandlerResponse.builder()
                .message(message)
                .nextStep(ConversationStep.LAB_RESULTS_INQUIRY)
                .clearContext(true)
                .build();
        }

        String message = "⚠️ Reference number not found.\n\n";
        message += "Please check your reference number and try again.\n\n";
        message += "If you need assistance, please call +263 242 123456 or visit our reception.";
        message += "\n\n0️⃣ Main Menu";

        return HandlerResponse.builder()
            .message(message)
            .nextStep(ConversationStep.LAB_RESULTS_INQUIRY)
            .clearContext(true)
            .build();
    }

    @Override
    public ConversationStep getHandledStep() {
        return ConversationStep.LAB_RESULTS_INQUIRY;
    }

    private String buildInitialMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("🔬 *Lab Results Inquiry*\n\n");
        sb.append("Please enter your lab reference number to check your results.\n\n");
        sb.append("*Example:* LAB-2025-001234\n\n");
        sb.append("_Your reference number was provided when you submitted your sample._");
        return sb.toString();
    }

    private String buildResultMessage(LabResult labResult) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        sb.append("🔬 *Lab Results*\n\n");
        sb.append("*Reference:* ").append(labResult.getReferenceNumber()).append("\n");
        sb.append("*Patient:* ").append(labResult.getPatientName()).append("\n");
        sb.append("*Test:* ").append(labResult.getTestName()).append("\n");
        sb.append("*Date Collected:* ").append(labResult.getDateCollected().format(formatter)).append("\n");

        if ("READY".equalsIgnoreCase(labResult.getStatus())) {
            sb.append("*Status:* ✅ Ready\n");
            if (labResult.getDateCompleted() != null) {
                sb.append("*Completed:* ").append(labResult.getDateCompleted().format(formatter)).append("\n");
            }
            sb.append("\n✅ Your results are ready for collection!\n\n");
            sb.append("Please visit our laboratory with:\n");
            sb.append("• Your ID\n");
            sb.append("• This reference number\n\n");
            sb.append("*Opening Hours:*\n");
            sb.append("Monday - Friday: 7:00 AM - 5:00 PM\n");
            sb.append("Saturday: 8:00 AM - 1:00 PM");

        } else if ("PENDING".equalsIgnoreCase(labResult.getStatus())) {
            sb.append("*Status:* ⏳ Pending\n\n");
            sb.append("Your results are not ready yet. Please check back later.\n\n");
            sb.append("For urgent inquiries, call +263 242 123456.");

        } else if ("COLLECTED".equalsIgnoreCase(labResult.getStatus())) {
            sb.append("*Status:* ✅ Collected\n");
            if (labResult.getDateCompleted() != null) {
                sb.append("*Collected On:* ").append(labResult.getDateCompleted().format(formatter)).append("\n\n");
            }
            sb.append("Your results have already been collected.\n\n");
            sb.append("If you need a duplicate copy, please visit our laboratory with your ID.");
        }

        return sb.toString();
    }
}
