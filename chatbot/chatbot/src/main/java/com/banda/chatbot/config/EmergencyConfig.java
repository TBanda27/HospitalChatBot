package com.banda.chatbot.config;

import lombok.Data;

@Data
public class EmergencyConfig {
    private String primaryLine;
    private String ambulanceLine;
    private String instructions;
}
