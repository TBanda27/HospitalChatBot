package com.banda.chatbot.enums;

/**
 * State machine for conversation flow
 * Simple navigation system for information bot (no booking complexity)
 */
public enum ConversationStep {
    // Main entry point
    MAIN_MENU,

    // Feature 1: Hospital Information
    HOSPITAL_INFO,

    // Feature 2: Doctor Directory
    DOCTOR_LIST,
    DOCTOR_DETAIL,              // context: doctor_id

    // Feature 3: Service Pricing
    PRICE_CATEGORIES,
    PRICE_DETAIL,               // context: category

    // Feature 4: Insurance & Payment
    INSURANCE_INFO,

    // Feature 5: Preparation Guides
    PREPARATION_CATEGORIES,
    PREPARATION_DETAIL,         // context: guide_id

    // Feature 6: Directions & Transport
    DIRECTIONS,

    // Feature 7: Service Departments
    SERVICE_LIST,
    SERVICE_DETAIL,             // context: department_id

    // Feature 8: Health Campaigns
    CAMPAIGN_LIST,
    CAMPAIGN_DETAIL,            // context: campaign_id

    // Feature 9: Lab Results Inquiry
    LAB_RESULTS_INQUIRY,

    // Feature 10: Emergency Contact
    EMERGENCY_CONTACT,

    // FAQ System
    FAQ_CATEGORIES,
    FAQ_QUESTIONS;              // context: category_id

    /**
     * Get parent state for "back" navigation
     */
    public ConversationStep getParentState() {
        return switch(this) {
            case DOCTOR_DETAIL -> DOCTOR_LIST;
            case PRICE_DETAIL -> PRICE_CATEGORIES;
            case PREPARATION_DETAIL -> PREPARATION_CATEGORIES;
            case SERVICE_DETAIL -> SERVICE_LIST;
            case CAMPAIGN_DETAIL -> CAMPAIGN_LIST;
            case FAQ_QUESTIONS -> FAQ_CATEGORIES;
            case DOCTOR_LIST, PRICE_CATEGORIES, INSURANCE_INFO,
                 PREPARATION_CATEGORIES, DIRECTIONS, SERVICE_LIST,
                 CAMPAIGN_LIST, LAB_RESULTS_INQUIRY, EMERGENCY_CONTACT,
                 FAQ_CATEGORIES, HOSPITAL_INFO -> MAIN_MENU;
            default -> MAIN_MENU;
        };
    }

    /**
     * Check if state requires context data
     */
    public boolean requiresContext() {
        return this == DOCTOR_DETAIL
            || this == PRICE_DETAIL
            || this == PREPARATION_DETAIL
            || this == SERVICE_DETAIL
            || this == CAMPAIGN_DETAIL
            || this == FAQ_QUESTIONS;
    }
}
