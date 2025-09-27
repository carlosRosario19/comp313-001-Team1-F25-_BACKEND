package com.centennial.gamepickd.util.enums;

public enum Routes {
    HEALTH_CHECK("/actuator/health"),
    LOGIN("/api/login");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    public String val() {
        return path;
    }
}
