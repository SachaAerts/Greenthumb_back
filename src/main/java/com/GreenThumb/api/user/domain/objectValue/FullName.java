package com.GreenThumb.api.user.domain.objectValue;

public record FullName(String firstName, String lastName) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
