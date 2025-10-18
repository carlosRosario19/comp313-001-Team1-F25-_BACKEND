package com.centennial.gamepickd.util.enums;

public enum GenreType {

    INDIE("INDIE"),
    SHOOTER("SHOOTER"),
    RPG("RPG"),
    MMORPG("MMORPG"),
    SURVIVAL("SURVIVAL"),
    MOBA("MOBA");

    private final String val;

    GenreType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static GenreType fromValue(String label) {
        for (GenreType genre : values()) {
            if (genre.val.equalsIgnoreCase(label)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown genre label: " + label);
    }
}
