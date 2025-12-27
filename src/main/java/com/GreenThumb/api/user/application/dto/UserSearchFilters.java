package com.GreenThumb.api.user.application.dto;

public record UserSearchFilters(
        String query,
        String status,
        Boolean enabled,
        String role
) {
    public static UserSearchFilters of(String query, String status, Boolean enabled, String role) {
        return new UserSearchFilters(
                query,
                status != null ? status.toLowerCase() : "all",
                enabled,
                role
        );
    }

    public boolean hasQuery() {
        return query != null && !query.isBlank();
    }

    public boolean isActiveOnly() {
        return "active".equals(status);
    }

    public boolean isDeletedOnly() {
        return "deleted".equals(status);
    }

    public boolean hasEnabledFilter() {
        return enabled != null;
    }

    public boolean hasRoleFilter() {
        return role != null && !role.isBlank();
    }
}