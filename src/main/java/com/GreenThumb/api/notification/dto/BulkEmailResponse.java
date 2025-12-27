package com.GreenThumb.api.notification.dto;

import java.util.List;

public record BulkEmailResponse(
    String message,
    int totalRecipients,
    int successCount,
    int failureCount,
    List<String> failedEmails
) {
    public static BulkEmailResponse of(
        String message,
        int totalRecipients,
        int successCount,
        int failureCount,
        List<String> failedEmails
    ) {
        return new BulkEmailResponse(
            message,
            totalRecipients,
            successCount,
            failureCount,
            failedEmails
        );
    }
}