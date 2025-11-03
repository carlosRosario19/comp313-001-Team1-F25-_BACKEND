package com.centennial.gamepickd.util.enums;

public enum PlatformType {

	PLAYSTATION_5("PlayStation 5"),
    PLAYSTATION_4("PlayStation 4"),
    PLAYSTATION_3("PlayStation 3"),
    PLAYSTATION_2("PlayStation 2"),
    PLAYSTATION_1("PlayStation 1"),
    XBOX_SERIES_S("Xbox Series S"),
    XBOX_ONE("Xbox One"),
    XBOX_360("Xbox 360"),
    NINTENDO_SWITCH("Nintendo Switch"),
    NINTENDO_WII_U("Nintendo Wii U"),
    NINTENDO_WII("Nintendo Wii"),
    NINTENDO_3DS("Nintendo 3DS"),
    NINTENDO_DS("Nintendo DS"),
    GAME_BOY_COLOR("Game Boy Color"),
    STEAM("Steam"),
    EPIC_GAMES("Epic Games"),
    ANDROID("Android"),
    IOS("iOS"),
    PLAYSTATION_VITA("PlayStation Vita"),
    NINTENDO_GAMECUBE("Nintendo GameCube"),
    NINTENDO_64("Nintendo 64"),
    SEGA_DREAMCAST("Sega Dreamcast");

    private final String val;

    PlatformType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static PlatformType fromValue(String value) {
        for (PlatformType platform : values()) {
            if (platform.val.equalsIgnoreCase(value)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("Unknown platform: " + value);
    }
}
