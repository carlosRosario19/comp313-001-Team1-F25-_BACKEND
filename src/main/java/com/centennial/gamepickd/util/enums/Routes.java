package com.centennial.gamepickd.util.enums;

public enum Routes {
    HEALTH_CHECK("/actuator/health"),
    LOGIN("/api/login"),
    ADD_MEMBER("/api/members"),
    ADD_CONTRIBUTOR("/api/contributors"),
    ADD_GAME("/api/games");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    public String val() {
        return path;
    }
}
