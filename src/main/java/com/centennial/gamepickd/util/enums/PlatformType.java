package com.centennial.gamepickd.util.enums;

public enum PlatformType {

    // 🎮 Consoles
    PLAYSTATION_5("PlayStation 5"),
    PLAYSTATION_4("PlayStation 4"),
    PLAYSTATION_3("PlayStation 3"),
    XBOX_SERIES_X("Xbox Series X"),
    XBOX_SERIES_S("Xbox Series S"),
    XBOX_ONE("Xbox One"),
    XBOX_360("Xbox 360"),
    NINTENDO_SWITCH("Nintendo Switch"),
    NINTENDO_WII_U("Nintendo Wii U"),
    NINTENDO_WII("Nintendo Wii"),
    NINTENDO_3DS("Nintendo 3DS"),
    NINTENDO_DS("Nintendo DS"),

    // 💻 PC Platforms
    PC_WINDOWS("PC (Windows)"),
    PC_MAC("PC (Mac)"),
    PC_LINUX("PC (Linux)"),

    // 📱 Mobile Platforms
    ANDROID("Android"),
    IOS("iOS"),

    // ☁️ Cloud / Streaming Platforms
    GOOGLE_STADIA("Google Stadia"),
    AMAZON_LUNA("Amazon Luna"),
    NVIDIA_GEFORCENOW("NVIDIA GeForce NOW"),
    XBOX_CLOUD_GAMING("Xbox Cloud Gaming"),

    // 🕹️ Handheld / Legacy Platforms
    PLAYSTATION_VITA("PlayStation Vita"),
    PLAYSTATION_PORTABLE("PlayStation Portable"),
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
