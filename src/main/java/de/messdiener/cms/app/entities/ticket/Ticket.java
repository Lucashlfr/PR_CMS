package de.messdiener.cms.app.entities.ticket;

import de.messdiener.cms.app.entities.ticket.cache.TicketDate;
import de.messdiener.cms.app.entities.ticket.cache.TicketLink;
import de.messdiener.cms.app.entities.ticket.cache.TicketLog;
import de.messdiener.cms.app.entities.ticket.cache.TicketPerson;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;
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
    private String html;

    public Ticket(UUID uuid, long created, long lastUpdate, long deadline, UUID user_uuid, TicketState ticketState, ArrayList<TicketLog> ticketNotes, ArrayList<TicketLink> ticketLinks, TicketPerson ticketPerson, String html) {
        this.uuid = uuid;
        this.created = created;
        this.lastUpdate = lastUpdate;
        this.deadline = deadline;
        this.user_uuid = user_uuid;
        this.ticketState = ticketState;
        this.ticketNotes = ticketNotes;
        this.ticketLinks = ticketLinks;
        this.ticketPerson = ticketPerson;
        this.html = html;
    }

    public static Ticket empty(){
        return new Ticket(UUID.randomUUID(), -1, -1, -1, UUID.randomUUID(),
                TicketState.REJECTED, new ArrayList<>(), new ArrayList<>(), TicketPerson.empty(), "<h1>Kein Text! </h1>");
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
        return checkRightsForLector() && (getUser_UUID().equals(SecurityHelper.getUser().getUser_UUID()) || SecurityHelper.getUser().userHasPermission("LEKTOR_1"));
    }

    private boolean checkRightsForLector() throws SQLException{
        return ((ticketState == TicketState.OPEN || ticketState == TicketState.CORRECTOR_1)
                && (SecurityHelper.getUser().userHasPermission("LEKTOR_1") || SecurityHelper.getUser().userHasPermission("ADMIN")))
                || ((ticketState == TicketState.CORRECTOR_2) && (SecurityHelper.getUser().userHasPermission("LEKTOR_2")
                || SecurityHelper.getUser().userHasPermission("ADMIN")));
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

    public String getName(){
        return ticketPerson.getName() + " [" + ticketPerson.getAssociation() +  "]";
    }


    public String getHTML() {
        return html;
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
        }
        return false;
    }

    public boolean isCorrection(){
        switch (ticketState){
            case CORRECTOR_1:
            case CORRECTOR_2:
                return true;
        }
        return false;
    }

    public boolean userCanAddData() throws SQLException {
        User user = SecurityHelper.getUser();
        if(isClosed()) return false;
        return getUser_UUID().equals(user.getUser_UUID()) || user.isAdmin();
    }

    public String getDescription(){
        return getTicketPerson().getName() + " | " + getTicketPerson().getAssociation();
    }

    public boolean isRejected(){
        return ticketState == TicketState.REJECTED;
    }
}
