package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.web.utils.DateUtils;

public class TicketDate {

    private final long created;
    private final long lastUpdate;
    private final long deadline;

    public TicketDate(Ticket ticket) {
        this.created = ticket.getCreated();
        this.lastUpdate = ticket.getLastUpdate();
        this.deadline = ticket.getDeadline();
    }

    public String getGermanDate_CREATED() {
        return DateUtils.convertLongToDate(created, DateUtils.DateType.GERMAN);
    }

    public String getEnglishDate_CREATED() {
        return DateUtils.convertLongToDate(created, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDateWithDay_CREATED() {
        return DateUtils.convertLongToDate(created, DateUtils.DateType.GERMAN_WITH_DAY_NAME);
    }

    public String getGermanDate_LASTUPDATE() {
        return DateUtils.convertLongToDate(lastUpdate, DateUtils.DateType.GERMAN);
    }

    public String getEnglishDate_LASTUPDATE() {
        return DateUtils.convertLongToDate(lastUpdate, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDate_DEADLINE() {
        return DateUtils.convertLongToDate(deadline, DateUtils.DateType.GERMAN);
    }

    public String getEnglishDate_DEADLINE() {
        return DateUtils.convertLongToDate(deadline, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDateWithDay_DEADLINE() {
        return DateUtils.convertLongToDate(deadline, DateUtils.DateType.GERMAN_WITH_DAY_NAME);
    }
}
