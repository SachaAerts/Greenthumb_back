package com.GreenThumb.api.plant.application.enums;

public enum TaskStatus {
    PENDING("En attente"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée"),
    OVERDUE("En retard");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
