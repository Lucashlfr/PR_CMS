package de.messdiener.cms.app.entities.ticket;

import de.messdiener.cms.app.entities.ticket.cache.TicketDate;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.entities.ticket.cache.TicketLink;
import de.messdiener.cms.app.entities.ticket.cache.TicketLog;
import de.messdiener.cms.app.entities.ticket.cache.TicketPerson;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;
import de.messdiener.cms.cache.enums.UserGroup;
import de.messdiener.cms.web.security.SecurityHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Ticket {

    private UUID uuid;
    private long created;
    private long lastUpdate;
    private long deadline;
    private UUID user_uuid;
    private TicketState ticketState;
    private ArrayList<TicketLog> ticketNotes;
    private ArrayList<TicketLink> ticketLinks;
    private TicketPerson ticketPerson;

    public Ticket(UUID uuid, long created, long lastUpdate, long deadline, UUID user_uuid, TicketState ticketState, ArrayList<TicketLog> ticketNotes, ArrayList<TicketLink> ticketLinks, TicketPerson ticketPerson) {
        this.uuid = uuid;
        this.created = created;
        this.lastUpdate = lastUpdate;
        this.deadline = deadline;
        this.user_uuid = user_uuid;
        this.ticketState = ticketState;
        this.ticketNotes = ticketNotes;
        this.ticketLinks = ticketLinks;
        this.ticketPerson = ticketPerson;
    }


    public TicketDate getDates(){
        return new TicketDate(this);
    }

    public User getEditor() throws SQLException {
        return Cache.USER_SERVICE.getUsers().stream().filter(u -> u.getUser_UUID().equals(user_uuid)).findFirst().orElseThrow();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public UUID getUser_UUID() {
        return user_uuid;
    }

    public void setUser_UUID(UUID user_uuid) {
        this.user_uuid = user_uuid;
    }

    public TicketState getTicketState() {
        return ticketState;
    }

    public void setTicketState(TicketState ticketState) {
        this.ticketState = ticketState;
    }

    public ArrayList<TicketLog> getTicketLogs() {
        return ticketNotes;
    }

    public void setTicketNotes(ArrayList<TicketLog> ticketNotes) {
        this.ticketNotes = ticketNotes;
    }

    public ArrayList<TicketLink> getTicketLinks() {
        return ticketLinks;
    }

    public void setTicketLinks(ArrayList<TicketLink> ticketLinks) {
        this.ticketLinks = ticketLinks;
    }

    public TicketPerson getTicketPerson() {
        return ticketPerson;
    }

    public void setTicketPerson(TicketPerson ticketPerson) {
        this.ticketPerson = ticketPerson;
    }

    public boolean userCanLector() throws SQLException {
        return ((ticketState == TicketState.OPEN || ticketState == TicketState.CORRECTOR_1)
                && (SecurityHelper.getUser().getGroup() == UserGroup.LEKTOR1 || SecurityHelper.getUser().getGroup() == UserGroup.ADMIN))
                || ((ticketState == TicketState.CORRECTOR_2) && (SecurityHelper.getUser().getGroup() == UserGroup.LEKTOR2
                || SecurityHelper.getUser().getGroup() == UserGroup.ADMIN));
    }

    public String getReason(){
        if(ticketState == TicketState.REJECTED){
            return getTicketLogs().get(getTicketLogs().size()-1).getText().replace("Ticket wurde von oneUser abgelehnt: ","");
        }
        return "";
    }

    public boolean isCloseable() throws SQLException {
        return (ticketState == TicketState.AWAITING_PUBLICATION) && SecurityHelper.getUser().isAdmin();
    }

}
