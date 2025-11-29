package com.example.health_log;

public class ReportRequestBody {
    private String reason;

    public ReportRequestBody(String reason) {
        this.reason = reason;
    }

    // Getter and setter
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
