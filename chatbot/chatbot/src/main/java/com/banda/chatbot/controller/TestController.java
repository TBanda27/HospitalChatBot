package com.banda.chatbot.controller;

import com.banda.chatbot.config.HospitalConfig;
import com.banda.chatbot.config.TwilioConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final HospitalConfig hospitalConfig;
    private final TwilioConfig twilioConfig;

    @GetMapping("/hospital")
    public HospitalConfig getHospitalInfo() {
        return hospitalConfig;
    }

    @GetMapping("/hospital/basic")
    public Map<String, String> getBasicInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("name", hospitalConfig.getName());
        info.put("address", hospitalConfig.getAddress());
        info.put("phone", hospitalConfig.getMainPhone());
        info.put("email", hospitalConfig.getEmail());
        return info;
    }

    @GetMapping("/hospital/emergency")
    public Map<String, String> getEmergencyInfo() {
        Map<String, String> emergency = new HashMap<>();
        emergency.put("primaryLine", hospitalConfig.getEmergency().getPrimaryLine());
        emergency.put("ambulanceLine", hospitalConfig.getEmergency().getAmbulanceLine());
        emergency.put("instructions", hospitalConfig.getEmergency().getInstructions());
        return emergency;
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("message", "Hospital Config loaded successfully!");
        health.put("hospitalName", hospitalConfig.getName());
        return health;
    }

    @GetMapping("/twilio")
    public Map<String, String> getTwilioInfo() {
        Map<String, String> twilio = new HashMap<>();
        // Mask sensitive data for security
        twilio.put("accountSid", maskSensitive(twilioConfig.getAccountSid()));
        twilio.put("authToken", maskSensitive(twilioConfig.getAuthToken()));
        twilio.put("phoneNumber", twilioConfig.getPhoneNumber());
        twilio.put("status", "Twilio config loaded ✅");
        return twilio;
    }

    @GetMapping("/twilio/test")
    public Map<String, String> testTwilioConnection() {
        Map<String, String> result = new HashMap<>();

        boolean configValid = twilioConfig.getAccountSid() != null
                && twilioConfig.getAuthToken() != null
                && twilioConfig.getPhoneNumber() != null;

        result.put("configLoaded", String.valueOf(configValid));
        result.put("phoneNumber", twilioConfig.getPhoneNumber());
        result.put("message", configValid ? "Twilio config is valid ✅" : "Twilio config is missing ❌");

        return result;
    }

    private String maskSensitive(String value) {
        if (value == null || value.length() < 8) return "****";
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
}
