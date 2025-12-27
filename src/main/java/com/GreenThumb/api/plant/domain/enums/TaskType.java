package com.GreenThumb.api.plant.domain.enums;

public enum TaskType {
    ARROSAGE("Arrosage", "#3B82F6"),
    FERTILISATION("Fertilisation", "#10B981"),
    VAPORISATION("Vaporisation", "#06B6D4"),
    CONTROLE_LUMIERE("Contrôle lumière", "#F59E0B"),
    REMPOTAGE("Rempotage", "#8B5CF6"),
    TAILLE("Taille", "#EC4899"),
    INSPECTION_PARASITES("Inspection parasites", "#EF4444"),
    NETTOYAGE_FEUILLES("Nettoyage feuilles", "#14B8A6"),
    CONTROLE_PH_SOL("Contrôle pH sol", "#F97316");

    private final String displayName;
    private final String defaultcolor;

    TaskType(String displayName, String defaultcolor) {
        this.displayName = displayName;
        this.defaultcolor = defaultcolor;
    }

    public String getDefaultcolor() {
        return defaultcolor;
    }

    public String getDisplayName() {
        return displayName;
    }
}
