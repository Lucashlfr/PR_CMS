package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;

import java.sql.SQLException;
import java.util.UUID;

public class TicketSummery {

    private final int allocatedTickets;
    private final int open;
    private final int lecture;
    private final int waitingForPublication;
    private final int closed;

    public TicketSummery(UUID id) throws SQLException {

        allocatedTickets = Cache.TICKET_SERVICE.getTicketsByUser(id).size();
        open = Cache.TICKET_SERVICE.getTicketsByUser(id, TicketState.OPEN).size();
        lecture = Cache.TICKET_SERVICE.getTicketsByUser(id, TicketState.CORRECTOR_1).size() + Cache.TICKET_SERVICE.getTicketsByUser(id, TicketState.CORRECTOR_2).size();
        waitingForPublication = Cache.TICKET_SERVICE.getTicketsByUser(id, TicketState.AWAITING_PUBLICATION).size();
        closed = Cache.TICKET_SERVICE.getTicketsByUser(id, TicketState.PUBLISHED).size();
    }

    public int getAllocatedTickets() {
        return Math.max(allocatedTickets, 0);
    }
    
    public double calcAll(){
        return getAllocatedTickets() > 0 ? getAllocatedTickets() : 0.01;
    }

    public int getOpen() {
        return Math.max(open, 0);
    }

    public int getLecture() {
        return Math.max(lecture, 0);
    }

    public int getWaitingForPublication() {
        return Math.max(waitingForPublication, 0);
    }

    public int getClosed() {
        return Math.max(closed, 0);
    }

    public double openPercentage(){
        return (100 / (calcAll())) * open;
    }

    public double lecturePercentage(){
        return (100 / ( calcAll())) * lecture;
    }

    public double awaitingPercentage(){
        return (100 / ( calcAll())) * waitingForPublication;
    }
    public double closedPercentage(){
        return (100 / ( calcAll())) * closed;
    }

}
