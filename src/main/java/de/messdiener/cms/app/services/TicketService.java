package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.app.entities.ticket.cache.TicketLink;
import de.messdiener.cms.app.entities.ticket.cache.TicketLog;
import de.messdiener.cms.app.entities.ticket.cache.TicketPerson;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;
import de.messdiener.cms.web.security.SecurityHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TicketService {

    private static final Logger LOGGER = Logger.getLogger("Manager.TicketService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    //UUID link_uuid, long date, UUID creator_uuid, String name, String link

    public TicketService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_tickets (ticket_uuid VARCHAR(255), creationDate LONG, lastUpdateDate LONG, deadline LONG, " +
                            "user_uuid VARCHAR(255), ticketState VARCHAR(255), ticketPerson_uuid VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_tickets_log (ticketLog_uuid VARCHAR(255), ticket_uuid VARCHAR(255), date LONG, user_uuid VARCHAR(255), text VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_tickets_links (ticketLink_uuid VARCHAR(255), ticket_uuid VARCHAR(255), date LONG, " +
                            "user_uuid VARCHAR(255), name VARCHAR(255), link VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_tickets_persons (ticketPerson_UUID VARCHAR(255), lastname VARCHAR(255), firstname VARCHAR(255), " +
                            "association VARCHAR(255), phone VARCHAR(255), email VARCHAR(255))"
            ).executeUpdate();

            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }
    }

    public void save(Ticket ticket) throws SQLException {

        delete(ticket);

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_tickets (ticket_uuid, creationDate, lastUpdateDate, deadline, user_uuid, ticketState, ticketPerson_uuid) VALUES (?,?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, ticket.getUUID().toString());
        preparedStatement.setLong(2, ticket.getCreated());
        preparedStatement.setLong(3, ticket.getLastUpdate());
        preparedStatement.setLong(4, ticket.getDeadline());
        preparedStatement.setString(5, ticket.getUser_UUID().toString());
        preparedStatement.setString(6, ticket.getTicketState().toString());
        preparedStatement.setString(7, ticket.getTicketPerson().getTicketPerson_UUID().toString());

        preparedStatement.executeUpdate();

        for (TicketLink link : ticket.getTicketLinks()) {
            addLink(ticket, link, false);
        }

        for (TicketLog log : ticket.getTicketLogs()) {
            addLog(ticket, log);
        }

        addPerson(ticket.getTicketPerson());
    }

    public void delete(Ticket ticket) {
        databaseService.delete("module_tickets", "ticket_uuid", ticket.getUUID().toString());
        databaseService.delete("module_tickets_log", "ticket_uuid", ticket.getUUID().toString());
        databaseService.delete("module_tickets_links", "ticket_uuid", ticket.getUUID().toString());
    }

    public void addLog(Ticket ticket, TicketLog ticketLog) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_tickets_log (ticketLog_uuid, ticket_uuid, date, user_uuid, text) VALUES (?,?,?,?,?)"
        );

        preparedStatement.setString(1, ticketLog.getTicketLog_UUID().toString());
        preparedStatement.setString(2, ticket.getUUID().toString());
        preparedStatement.setLong(3, ticketLog.getDate());
        preparedStatement.setString(4, ticketLog.getCreator_UUID().toString());
        preparedStatement.setString(5, ticketLog.getText());

        preparedStatement.executeUpdate();
    }

    public void addLink(Ticket ticket, TicketLink ticketLink, boolean log) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_tickets_links (ticketLink_uuid, ticket_uuid, date, user_uuid, name, link) VALUES (?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, ticketLink.getLink_UUID().toString());
        preparedStatement.setString(2, ticket.getUUID().toString());
        preparedStatement.setLong(3, ticketLink.getDate());
        preparedStatement.setString(4, ticketLink.getCreator_UUID().toString());
        preparedStatement.setString(5, ticketLink.getName());
        preparedStatement.setString(6, ticketLink.getLink());

        preparedStatement.executeUpdate();

        User user = Cache.USER_SERVICE.getUsers().stream().filter(users -> users.getUser_UUID().equals(ticketLink.getCreator_UUID())).findFirst().orElseThrow();

        if (log)
            addLog(ticket, new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), ticketLink.getCreator_UUID(),
                    "Neuer Link von " + user.getUsername() + " angelegt. " + ticketLink.getLink()));
    }

    public void addPerson(TicketPerson ticketPerson) throws SQLException {

        databaseService.delete("module_tickets_persons", "ticketPerson_UUID", ticketPerson.getTicketPerson_UUID().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_tickets_persons (ticketPerson_UUID, lastname, firstname, association, phone, email) VALUES (?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, ticketPerson.getTicketPerson_UUID().toString());
        preparedStatement.setString(2, ticketPerson.getLastname());
        preparedStatement.setString(3, ticketPerson.getFirstname());
        preparedStatement.setString(4, ticketPerson.getAssociation());
        preparedStatement.setString(5, ticketPerson.getPhone());
        preparedStatement.setString(6, ticketPerson.getEmail());

        preparedStatement.executeUpdate();

    }

    public void deleteLink(Ticket ticket, TicketLink ticketLink) throws SQLException {
        databaseService.delete("module_tickets_links", "ticketLink_uuid", ticketLink.getLink_UUID().toString());

        addLog(ticket, new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), ticketLink.getCreator_UUID(),
                "Link " + ticketLink.getLink() + " von " + SecurityHelper.getUsername() + " gelöscht."));
    }

    public Optional<Ticket> getTicket(UUID uuid) throws SQLException {
        return getTickets().stream().filter(ticket -> ticket.getUUID().equals(uuid)).findFirst();
    }

    public Set<Ticket> getTickets() throws SQLException {

        Set<Ticket> tickets = new HashSet<>();

        ResultSet resultSet = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_tickets"
        ).executeQuery();

        while (resultSet.next()) {

            UUID uuid = UUID.fromString(resultSet.getString("ticket_uuid"));
            long created = resultSet.getLong("creationDate");
            long lastUpdate = resultSet.getLong("lastUpdateDate");
            long deadline = resultSet.getLong("deadline");
            UUID user_uuid = UUID.fromString(resultSet.getString("user_uuid"));
            TicketState ticketState = TicketState.valueOf(resultSet.getString("ticketState"));
            ArrayList<TicketLog> ticketNotes = getTicketLog(uuid);
            ArrayList<TicketLink> ticketLinks = getTicketLinks(uuid);
            TicketPerson ticketPerson = getTicketPerson(UUID.fromString(resultSet.getString("ticketPerson_uuid"))).orElse(TicketPerson.empty());

            Ticket ticket = new Ticket(uuid, created, lastUpdate, deadline, user_uuid, ticketState, ticketNotes, ticketLinks, ticketPerson);

            tickets.add(ticket);
        }
        return tickets;
    }

    public ArrayList<TicketLog> getTicketLog(UUID uuid) throws SQLException {
        ArrayList<TicketLog> ticketLogs = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_tickets_log WHERE ticket_uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            UUID ticketLog_uuid = UUID.fromString(resultSet.getString("ticketLog_uuid"));
            long date = resultSet.getLong("date");
            UUID user_uuid = UUID.fromString(resultSet.getString("user_uuid"));
            String text = resultSet.getString("text");

            TicketLog ticketLog = new TicketLog(ticketLog_uuid, date, user_uuid, text);

            ticketLogs.add(ticketLog);
        }
        return ticketLogs;
    }

    public ArrayList<TicketLink> getTicketLinks(UUID uuid) throws SQLException {
        ArrayList<TicketLink> ticketLinks = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_tickets_links WHERE ticket_uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            UUID ticketLink_uuid = UUID.fromString(resultSet.getString("ticketLink_uuid"));
            long date = resultSet.getLong("date");
            UUID user_uuid = UUID.fromString(resultSet.getString("user_uuid"));
            String name = resultSet.getString("name");
            String link = resultSet.getString("link");

            TicketLink ticketLink = new TicketLink(ticketLink_uuid, date, user_uuid, name, link);

            ticketLinks.add(ticketLink);
        }
        return ticketLinks;
    }

    public ArrayList<TicketPerson> getTicketPersons() throws SQLException {

        ArrayList<TicketPerson> ticketPeople = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_tickets_persons"
        );

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {

            UUID ticketPerson_UUID = UUID.fromString(resultSet.getString("ticketPerson_UUID"));
            String lastname = resultSet.getString("lastname");
            String firstname = resultSet.getString("firstname");
            String association = resultSet.getString("association");
            String phone = resultSet.getString("phone");
            String email = resultSet.getString("email");

            ticketPeople.add(new TicketPerson(ticketPerson_UUID, lastname, firstname, association, phone, email));

        }
        return ticketPeople;

    }

    public Optional<TicketPerson> getTicketPerson(UUID uuid) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_tickets_persons WHERE ticketPerson_UUID = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {

            UUID ticketPerson_UUID = UUID.fromString(resultSet.getString("ticketPerson_UUID"));
            String lastname = resultSet.getString("lastname");
            String firstname = resultSet.getString("firstname");
            String association = resultSet.getString("association");
            String phone = resultSet.getString("phone");
            String email = resultSet.getString("email");

            return Optional.of(new TicketPerson(ticketPerson_UUID, lastname, firstname, association, phone, email));

        }
        return Optional.empty();

    }

    public Set<Ticket> getTicketsByUser(UUID user_uuid) throws SQLException {

        return getTickets().stream().filter(ticket -> ticket.getUser_UUID().equals(user_uuid)).collect(Collectors.toSet());

    }

    public Set<Ticket> getTicketsByUser(UUID user_uuid, TicketState ticketState) throws SQLException {
        return getTicketsByUser(user_uuid).stream().filter(ticket -> ticket.getTicketState() == ticketState).collect(Collectors.toSet());
    }


}
