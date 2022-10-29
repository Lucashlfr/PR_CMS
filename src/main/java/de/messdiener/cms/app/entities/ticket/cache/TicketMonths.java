package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class TicketMonths {

    public static ArrayList<TicketMonths> createDisplay() throws SQLException {

        ArrayList<TicketMonths> ticketMonths = new ArrayList<>();

        for (DateUtils.MonthNumberName value : DateUtils.MonthNumberName.values()) {
            ticketMonths.add(new TicketMonths(value.getName(), value.getNumber(), new ArrayList<>(), value));
        }

        for(TicketMonths value : ticketMonths){
            for(Ticket ticket : Cache.TICKET_SERVICE.getTickets()){
                if(ticket.getDates().getDeadline_MonthNumberName() == value.getMonthNumberName()){
                    value.getTickets().add(ticket);
                }
            }
        }

        ArrayList<TicketMonths> nextYear = new ArrayList<>();
        for (int i = 0; i < ticketMonths.size(); i++) {
            if(ticketMonths.get(i).getTickets().isEmpty()){
                ticketMonths.remove(i);
                continue;
            }
            if(ticketMonths.get(i).getMonth() < DateUtils.getCurrentMonthNumber()){
                nextYear.add(ticketMonths.get(i));
                ticketMonths.remove(i);
            }
        }

        ArrayList<TicketMonths> returnAbleTicketMonths = new ArrayList<>();
        for(TicketMonths value : ticketMonths){
            if(!value.getTickets().isEmpty() && value.getMonth() >= DateUtils.getCurrentMonthNumber()){
                returnAbleTicketMonths.add(value);
            }
        }
        returnAbleTicketMonths.addAll(nextYear);
        return returnAbleTicketMonths;
    }

    private String nameMonth;
    private int month;
    private ArrayList<Ticket> tickets;
    private DateUtils.MonthNumberName monthNumberName;

    public TicketMonths(String nameMonth, int month, ArrayList<Ticket> tickets, DateUtils.MonthNumberName monthNumberName) {
        this.nameMonth = nameMonth;
        this.month = month;
        this.tickets = tickets;
        this.monthNumberName = monthNumberName;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public int getMonth() {
        return month;
    }

    public String getNameMonth() {
        return nameMonth;
    }

    public DateUtils.MonthNumberName getMonthNumberName() {
        return monthNumberName;
    }
}
