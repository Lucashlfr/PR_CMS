package de.messdiener.cms.cache.enums;

public enum PersonRank {

    DEFAULT("Messdiener"), ORGL("Organisatorischer Leiter");

    final String name;

    PersonRank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
