package com.GreenThumb.api.notification.dto;

public record EmailRecipient(
    String email,
    String firstName,
    String lastName,
    String username
) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
