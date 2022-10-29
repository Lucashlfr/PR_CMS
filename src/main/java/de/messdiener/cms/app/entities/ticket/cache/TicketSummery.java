package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketSummery {

    private final int allocatedTickets;
    private final int open;
    private final int lectore;
    private final int waiting_for_publication;
    private final int closed;

    public TicketSummery(UUID uuid) throws SQLException {

        allocatedTickets = Cache.TICKET_SERVICE.getTicketsByUser(uuid).size();
        open = Cache.TICKET_SERVICE.getTicketsByUser(uuid, TicketState.OPEN).size();
        lectore = Cache.TICKET_SERVICE.getTicketsByUser(uuid, TicketState.CORRECTOR_1).size() + Cache.TICKET_SERVICE.getTicketsByUser(uuid, TicketState.CORRECTOR_2).size();
        waiting_for_publication = Cache.TICKET_SERVICE.getTicketsByUser(uuid, TicketState.AWAITING_PUBLICATION).size();
        closed = Cache.TICKET_SERVICE.getTicketsByUser(uuid, TicketState.PUBLISHED).size();
    }

    public int getAllocatedTickets() {
        return Math.max(allocatedTickets, 0);
    }
    
    public double calcAllc(){
        return getAllocatedTickets() > 0 ? getAllocatedTickets() : 0.01;
    }

    public int getOpen() {
        return Math.max(open, 0);
    }

    public int getLectore() {
        return Math.max(lectore, 0);
    }

    public int getWaiting_for_publication() {
        return Math.max(waiting_for_publication, 0);
    }

    public int getClosed() {
        return Math.max(closed, 0);
    }

    public double openPercentage(){
        return (100 / (calcAllc())) * open;
    }

    public double lectorePercentage(){
        return (100 / ( calcAllc())) * lectore;
    }

    public double awaitingPercentage(){
        return (100 / ( calcAllc())) * waiting_for_publication;
    }
    public double closedPercentage(){
        return (100 / ( calcAllc())) * closed;
    }

}
