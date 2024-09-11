package com.fedkoroma.gateway.enums;

public enum Roles {
    USER("USER"),ADMIN("ADMIN");

    public final String label;

    private Roles(String label) {
        this.label = label;
    }
}
