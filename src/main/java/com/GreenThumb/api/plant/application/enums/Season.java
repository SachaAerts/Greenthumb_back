package com.GreenThumb.api.plant.application.enums;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    public static Season fromMonth(int month) {
        if (month >= 3 && month <= 5) return SPRING;
        if (month >= 6 && month <= 8) return SUMMER;
        if (month >= 9 && month <= 11) return AUTUMN;
        return WINTER;
    }
}
