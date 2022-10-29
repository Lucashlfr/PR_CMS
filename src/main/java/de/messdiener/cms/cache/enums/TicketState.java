package de.messdiener.cms.cache.enums;

import java.util.Arrays;
import java.util.List;

public enum TicketState {

    OPEN("Offen", "badge bg-red-soft text-red", "mdi mdi-alert-circle-outline text-info"),
    CORRECTOR_1("Lektoriat 1", "badge bg-yellow-soft text-yellow", "mdi mdi-glasses text-warning"),
    CORRECTOR_2("Lektoriat 2", "badge bg-yellow-soft text-yellow", "mdi mdi-glasses text-warning"),
    AWAITING_PUBLICATION("Zur Veröffentlichung", "badge bg-info-soft text-info", "mdi mdi-clock-outline text-info"),
    PUBLISHED("Veröffentlicht", "badge bg-success-soft text-success", "mdi mdi-check-circle-outline text-success"),
    REJECTED("Abgelehnt", "badge bg-danger-soft text-danger", "mdi mdi-cancel text-danger");

    private final String name;
    private final String className;
    private final String icon;
    TicketState(String name, String className, String icon){
        this.name = name;
        this.className = className;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getIcon() {
        return icon;
    }

    public static List<TicketState> toList(){
        return Arrays.asList(TicketState.values());
    }

}
