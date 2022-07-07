package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;

import java.sql.SQLException;
import java.util.UUID;

public class TicketLog {

    private UUID ticketLog_uuid;
    private long date;
    private UUID creator_uuid;
    private String text;

    public TicketLog(UUID ticketNote_uuid, long date, UUID creator_uuid, String text) {
        this.ticketLog_uuid = ticketNote_uuid;
        this.date = date;
        this.creator_uuid = creator_uuid;
        this.text = text;
    }

    public static TicketLog create(UUID ticketNote_uuid, long date, UUID creator_uuid, String text){
        return new TicketLog(ticketNote_uuid, date, creator_uuid, text);
    }

    public static TicketLog create(String text) throws SQLException {
        return new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUser_UUID(), text);
    }

    public UUID getTicketLog_UUID() {
        return ticketLog_uuid;
    }

    public void setTicketLog_uuid(UUID ticketLog_uuid) {
        this.ticketLog_uuid = ticketLog_uuid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public UUID getCreator_UUID() {
        return creator_uuid;
    }

    public void setCreator_uuid(UUID creator_uuid) {
        this.creator_uuid = creator_uuid;
    }

    public String getText() {
        return text;
    }

    public String getGermandate(){
        return DateUtils.convertLongToDate(getDate(), DateUtils.DateType.GERMAN);
    }

    public void setText(String text) {
        this.text = text;
    }
}
