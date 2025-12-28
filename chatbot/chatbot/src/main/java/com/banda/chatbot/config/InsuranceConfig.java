package com.banda.chatbot.config;

import lombok.Data;
import java.util.List;

@Data
public class InsuranceConfig {
    private List<InsuranceProviderConfig> accepted;
}
