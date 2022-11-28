package de.messdiener.cms.cache.enums;

public enum OGroup {

    BELLHEIM("Bellheim"), KNITTELSHEIM("Knittelsheim"), OTTERSHEIM("Ottersheim"), ZEISKAM("Zeiskam"), DEFAULT("Nicht zugeordnet");

    final String name;

    OGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
