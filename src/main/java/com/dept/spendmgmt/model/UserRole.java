package com.dept.spendmgmt.model;

public enum UserRole {
    JUNIOR_ENGINEER,
    ASSISTANT_ENGINEER,
    EXECUTIVE_ENGINEER,
    HEAD_OFFICE;

    /** Returns the role that acts after this one in the invoice approval chain, or null if this is the last stage. */
    public UserRole next() {
        return switch (this) {
            case JUNIOR_ENGINEER -> ASSISTANT_ENGINEER;
            case ASSISTANT_ENGINEER -> EXECUTIVE_ENGINEER;
            case EXECUTIVE_ENGINEER -> null;
            case HEAD_OFFICE -> null;
        };
    }
}
