package de.messdiener.cms.app.entities.ticket;

import de.messdiener.cms.app.entities.ticket.cache.TicketDate;
import de.messdiener.cms.app.entities.ticket.cache.TicketLink;
import de.messdiener.cms.app.entities.ticket.cache.TicketLog;
import de.messdiener.cms.app.entities.ticket.cache.TicketPerson;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;
import de.messdiener.cms.web.security.SecurityHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
@Data
public class Ticket {

    private UUID id;
    private long created;
    private long lastUpdate;
    private long deadline;
    private UUID userUUID;
    private TicketState ticketState;
    private ArrayList<TicketLog> ticketLogs;
    private ArrayList<TicketLink> ticketLinks;
    private TicketPerson ticketPerson;
    private String html;

    public static Ticket empty(){
        return new Ticket(UUID.randomUUID(), -1, -1, -1, UUID.randomUUID(),
                TicketState.REJECTED, new ArrayList<>(), new ArrayList<>(), TicketPerson.empty(), "<h1>Kein Text! </h1>");
    }

    public TicketDate getDates(){
        return new TicketDate(this);
    }

    public User getEditor() throws SQLException {
        return Cache.USER_SERVICE.getUsers().stream().filter(u -> u.getUserID().equals(userUUID)).findFirst().orElseThrow();
    }

    public boolean userCanLector() throws SQLException {
        return checkRightsForLector() && (userUUID.equals(SecurityHelper.getUser().getUserID()) || SecurityHelper.getUser().userHasPermission("LEKTOR_1"));
    }

    private boolean checkRightsForLector() throws SQLException{
        return ((ticketState == TicketState.OPEN || ticketState == TicketState.CORRECTOR_1)
                && (SecurityHelper.getUser().userHasPermission("LEKTOR_1") || SecurityHelper.getUser().userHasPermission("ADMIN")))
                || ((ticketState == TicketState.CORRECTOR_2) && (SecurityHelper.getUser().userHasPermission("LEKTOR_2")
                || SecurityHelper.getUser().userHasPermission("ADMIN")));
    }


    public String getReason(){
        if(ticketState == TicketState.REJECTED){
            return ticketLogs.get(getTicketLogs().size()-1).getText().replace("Ticket wurde von oneUser abgelehnt: ","");
        }
        return "";
    }

    public boolean isCloseable() throws SQLException {
        return (ticketState == TicketState.AWAITING_PUBLICATION) && SecurityHelper.getUser().isAdmin();
    }

    public String getName(){
        return ticketPerson.getName() + " [" + ticketPerson.getAssociation() +  "]";
    }


    public String getHTML() {
        return html.replace("dc/cloud/file?uuid=", "/public/dc/cloud/file?uuid=");
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isClosed(){
        switch (ticketState){
            case REJECTED:
            case PUBLISHED:
            case AWAITING_PUBLICATION:
                return true;
            default:
                return false;
        }
    }

    public boolean isCorrection(){
        switch (ticketState){
            case CORRECTOR_1:
            case CORRECTOR_2:
                return true;
            default:
                return false;
        }
    }

    public boolean userCanAddData() throws SQLException {
        User user = SecurityHelper.getUser();
        if(isClosed()) return false;
        return userUUID.equals(user.getUserID()) || user.isAdmin();
    }

    public String getDescription(){
        return getTicketPerson().getName() + " | " + getTicketPerson().getAssociation();
    }

    public boolean isRejected(){
        return ticketState == TicketState.REJECTED;
    }
}
