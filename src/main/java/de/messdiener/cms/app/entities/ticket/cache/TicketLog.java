package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TicketLog {

    private UUID id;
    private long date;
    private UUID creatorUUID;
    private String text;

    public static TicketLog create(UUID uuid, long date, UUID creatorUUID, String text){
        return new TicketLog(uuid, date, creatorUUID, text);
    }

    public static TicketLog create(String text) throws SQLException {
        return new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUserID(), text);
    }

    public String getGermandate(){
        return DateUtils.convertLongToDate(getDate(), DateUtils.DateType.GERMAN);
    }
}
