package com.centennial.gamepickd.util.enums;

public enum PublisherType {

	UBISOFT("UBISOFT"),
    ELECTRONIC_ARTS("ELECTRONIC ARTS"),
    ACTIVISION("ACTIVISION"),
    BETHESDA_SOFTWORKS("BETHESDA SOFTWORKS"),
    CAPCOM("CAPCOM"),
    NINTENDO("NINTENDO"),
    SONY_INTERACTIVE_ENTERTAINMENT("SONY INTERACTIVE ENTERTAINMENT"),
    MICROSOFT_STUDIOS("MICROSOFT STUDIOS"),
    SEGA("SEGA"),
    SQUARE_ENIX("SQUARE ENIX"),
    BANDAI_NAMCO("BANDAI NAMCO"),
    TAKE_TWO_INTERACTIVE("TAKE-TWO INTERACTIVE"),
    ROCKSTAR_GAMES("ROCKSTAR GAMES"),
    CD_PROJEKT("CD PROJEKT"),
    EPIC_GAMES("EPIC GAMES"),
    BLIZZARD_ENTERTAINMENT("BLIZZARD ENTERTAINMENT"),
    PARADOX_INTERACTIVE("PARADOX INTERACTIVE"),
    WARNER_BROS_GAMES("WARNER BROS. GAMES"),
    VALVE("VALVE"),
    NAUGHTY_DOG("NAUGHTY DOG"),
    INSOMNIAC_GAMES("INSOMNIAC GAMES"),
    BUNGIE("BUNGIE"),
    KONAMI("KONAMI"),
    RIOT_GAMES("RIOT GAMES"),
    _2K_GAMES("2K GAMES"),
    OBSIDIAN_ENTERTAINMENT("OBSIDIAN ENTERTAINMENT"),
    LIONHEAD_STUDIOS("LIONHEAD STUDIOS"),
    DEEP_SILVER("DEEP SILVER");

    private final String val;

    PublisherType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static PublisherType fromValue(String value) {
        for (PublisherType publisher : values()) {
            if (publisher.val.equalsIgnoreCase(value)) {
                return publisher;
            }
        }
        throw new IllegalArgumentException("Unknown publisher: " + value);
    }
}
