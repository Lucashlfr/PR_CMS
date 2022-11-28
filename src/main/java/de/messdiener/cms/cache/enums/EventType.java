package de.messdiener.cms.cache.enums;

public enum EventType {

    WERKTAGSMESSE("Werktagsmesse"),
    VORABENDMESSE("Vorabendmesse"),
    GOTTESDIENST("Gottesdienst"),
    HOCHAMT("Hochamt (Weihrauch, etc.)"),
    THB("Taufe, Hochzeit, Beerdigung, etc."),
    WORTGOTTSDIENST("Wortgottesdienst"),
    OHNE("Ohne Messdiener"),
    DEFAULT("Noch nicht definiert");

    final String name;

    EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
