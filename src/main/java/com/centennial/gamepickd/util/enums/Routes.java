package com.centennial.gamepickd.util.enums;

public enum Routes {
    HEALTH_CHECK("/actuator/health"),
    LOGIN("/api/login"),
    MEMBERS("/api/members"),
    CONTRIBUTORS("/api/contributors"),
    GAMES("/api/games"),
    IMAGES("/api/images/**"),
    GENRES("/api/genres"),
    PLATFORMS("/api/platforms"),
    PUBLISHERS("/api/publishers");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    public String val() {
        return path;
    }
}
