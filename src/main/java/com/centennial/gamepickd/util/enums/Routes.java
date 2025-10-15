package com.centennial.gamepickd.util.enums;

public enum Routes {
    HEALTH_CHECK("/actuator/health"),
    LOGIN("/api/login"),
    MEMBERS("/api/members"),
    CONTRIBUTORS("/api/contributors"),
    GAMES("/api/games");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    public String val() {
        return path;
    }
}
