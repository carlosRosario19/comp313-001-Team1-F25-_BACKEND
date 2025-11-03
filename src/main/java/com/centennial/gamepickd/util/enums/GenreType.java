package com.centennial.gamepickd.util.enums;

public enum GenreType {

    INDIE("INDIE"),
    SHOOTER("SHOOTER"),
    RPG("RPG"),
    MMORPG("MMORPG"),
    SURVIVAL("SURVIVAL"),
    ACTION("ACTION"),
    MULTIPLAYER("MULTIPLAYER"),
    BATTLE_ROYALE("BATTLE ROYALE"),
    ADVENTURE("ADVENTURE"),
    OPEN_WORLD("OPEN WORLD"),
    PLATFORMER("PLATFORMER"),
    RACING("RACING"),
    FIGHTING("FIGHTING"),
    PUZZLE("PUZZLE"),
    STRATEGY("STRATEGY"),
    TOWER_DEFENSE("TOWER DEFENSE"),
    SIMULATION("SIMULATION"),
    HORROR("HORROR"),
    SANDBOX("SANDBOX"),
    MUSIC_RHYTHM("MUSIC/RHYTHM"),
    CARD_GAME("CARD GAME"),
    VISUAL_NOVEL("VISUAL NOVEL"),
    SPORTS("SPORTS"),
    EDUCATIONAL("EDUCATIONAL"),
    SURVIVAL_HORROR("SURVIVAL HORROR"),
    FPS("FPS"),
    TPS("TPS"),
    EXPLORATION("EXPLORATION"),
    VR("VR"),
    ARCADE("ARCADE"),
    CO_OP("CO-OP"),
    CITY_BUILDER("CITY BUILDER"),
    MANAGEMENT_SIMULATOR("MANAGEMENT SIMULATOR"),
    TRAINING_EDUCATIONAL("TRAINING/EDUCATIONAL"),
    CASUAL("CASUAL"),
    PARTY_GAME("PARTY GAME"),
    MECHA("MECHA"),
    ZOMBIE("ZOMBIE"),
    CYBERPUNK("CYBERPUNK"),
    FANTASY("FANTASY"),
    SCI_FI("SCI-FI"),
    HISTORICAL("HISTORICAL"),
    MYSTERY("MYSTERY"),
    BOARD_GAME("BOARD GAME"),
    QUIZ_TRIVIA("QUIZ/TRIVIA"),
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
