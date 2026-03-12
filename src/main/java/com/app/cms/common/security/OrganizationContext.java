package com.app.cms.common.security;

public class OrganizationContext {
    private static final ThreadLocal<Long> ORGANIZATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setOrganizationId(Long organizationId) {
        ORGANIZATION_ID.set(organizationId);
    }

    public static Long getOrganizationId() {
        Long orgId = ORGANIZATION_ID.get();
        if (orgId == null) {
            throw new SecurityException("No organization context");
        }
        return orgId;
    }

    public static void clear() {
        ORGANIZATION_ID.remove();
        USER_ID.remove();
    }

    public static void setUserId(Long userId) { USER_ID.set(userId); }

    public static Long getUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
            throw new SecurityException("No organization context");
        }
        return userId;
    }
}
