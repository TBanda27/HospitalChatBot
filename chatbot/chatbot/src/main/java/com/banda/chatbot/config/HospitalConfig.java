package com.banda.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "hospital")
@Data
public class HospitalConfig {

    // Basic hospital information
    private String name;
    private String address;
    private String mainPhone;
    private String email;
    private String operatingHours;
    private String parkingInfo;
    private String transportRoutes;
    private String googleMapsLink;

    // Emergency contact
    private EmergencyConfig emergency;

    // Payment & insurance
    private List<PaymentMethodConfig> paymentMethods;
    private InsuranceConfig insurance;
}
