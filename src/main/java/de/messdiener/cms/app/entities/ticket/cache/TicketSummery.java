package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

public class TicketSummery {
    
    private int open;
    private int correction;
    private int global;
    private int publication;
    private int waiting;

    public TicketSummery(int open, int correction, int global, int publication, int waiting) {
        this.open = open;
        this.correction = correction;
        this.global = global;
        this.publication = publication;
        this.waiting = waiting;
    }

    public static TicketSummery create() throws SQLException {

        Set<Ticket> tickets = Cache.TICKET_SERVICE.getTickets();
        int open = collect(TicketState.OPEN);
        int correction = collect(TicketState.CORRECTOR_1) + collect(TicketState.CORRECTOR_2);
        int global = tickets.size();
        int publication = collect(TicketState.PUBLISHED);
        int waiting = collect(TicketState.AWAITING_PUBLICATION);

        return new TicketSummery(open, correction, global, publication, waiting);
    }

    private static int collect(TicketState ticketState) throws SQLException {
        Set<Ticket> tickets = Cache.TICKET_SERVICE.getTickets();
        return tickets.stream().filter(t -> t.getTicketState() == ticketState).collect(Collectors.toSet()).size();
    }

    public int getOpen() {
        return open;
    }

    private void setOpen(int open) {
        this.open = open;
    }

    public int getCorrection() {
        return correction;
    }

    private void setCorrection(int correction) {
        this.correction = correction;
    }

    public int getGlobal() {
        return global;
    }

    private void setGlobal(int global) {
        this.global = global;
    }

    public int getPublication() {
        return publication;
    }

    private void setPublication(int publication) {
        this.publication = publication;
    }

    public int getWaiting() {
        return waiting;
    }

    private void setWaiting(int waiting) {
        this.waiting = waiting;
    }
}
