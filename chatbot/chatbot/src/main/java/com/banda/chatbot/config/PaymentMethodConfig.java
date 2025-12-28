package com.banda.chatbot.config;

import lombok.Data;

@Data
public class PaymentMethodConfig {
    private String code;
    private String name;
    private String info;
    private boolean active;
}
