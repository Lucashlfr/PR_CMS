package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.web.utils.DateUtils;

import java.util.Date;

public class TicketDate {

    private final long created;
    private final long lastUpdate;
    private final long deadline;

    private final Date date_created;
    private final Date date_lastUpdate;
    private final Date date_deadline;

    public TicketDate(Ticket ticket) {
        this.created = ticket.getCreated();
        this.lastUpdate = ticket.getLastUpdate();
        this.deadline = ticket.getDeadline();

        this.date_created = new Date(created);
        this.date_lastUpdate = new Date(lastUpdate);
        this.date_deadline = new Date(deadline);
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

    public long getCreated() {
        return created;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getDeadline() {
        return deadline;
    }

    public Date getDate_created() {
        return date_created;
    }

    public Date getDate_lastUpdate() {
        return date_lastUpdate;
    }

    public Date getDate_deadline() {
        return date_deadline;
    }

    public DateUtils.MonthNumberName getDeadline_MonthNumberName(){
        return DateUtils.getMonthNumberName(getDate_deadline());
    }
}
