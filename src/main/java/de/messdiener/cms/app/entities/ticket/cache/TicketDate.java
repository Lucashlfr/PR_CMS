package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.web.utils.DateUtils;

import java.util.Date;

public class TicketDate {

    private final long created;
    private final long lastUpdate;
    private final long deadline;

    private final Date dateDeadline;

    public TicketDate(Ticket ticket) {
        this.created = ticket.getCreated();
        this.lastUpdate = ticket.getLastUpdate();
        this.deadline = ticket.getDeadline();

        this.dateDeadline = new Date(deadline);
    }

    public String getGermanDateCreated() {
        return DateUtils.convertLongToDate(created, DateUtils.DateType.GERMAN);
    }

    public String getEnglishDateCreated() {
        return DateUtils.convertLongToDate(created, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDateWithDayCreated() {
        return DateUtils.convertLongToDate(created, DateUtils.DateType.GERMAN_WITH_DAY_NAME);
    }

    public String getGermanDateLastUpdate() {
        return DateUtils.convertLongToDate(lastUpdate, DateUtils.DateType.GERMAN);
    }

    public String getEnglishDateLastUpdate() {
        return DateUtils.convertLongToDate(lastUpdate, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDateDeadline() {
        return DateUtils.convertLongToDate(deadline, DateUtils.DateType.GERMAN);
    }

    public String getEnglishDateDeadline() {
        return DateUtils.convertLongToDate(deadline, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDateWithDayDeadline() {
        return DateUtils.convertLongToDate(deadline, DateUtils.DateType.GERMAN_WITH_DAY_NAME);
    }

    public DateUtils.MonthNumberName getDeadlineMonthNumberName(){
        return DateUtils.getMonthNumberName(dateDeadline);
    }
}
