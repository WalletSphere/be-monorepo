package com.khomishchak.cryptoportfolio.model.enums;

public enum FeedbackType {
    BUG_REPORT("Bug Report"),
    FEATURE_REQUEST("Feature Request"),
    USABILITY_FEEDBACK("Usability Feedback"),
    GENERAL_INQUIRY("General Inquiry"),
    SECURITY_FEEDBACK("Security Feedback");

    private final String typeName;

    FeedbackType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
