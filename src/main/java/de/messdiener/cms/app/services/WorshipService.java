package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.event.WorshipEvent;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.EventType;
import de.messdiener.cms.cache.enums.OGroup;
import de.messdiener.cms.web.utils.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorshipService {


    private static final Logger LOGGER = Logger.getLogger("Manager.PersonService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public WorshipService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_worships (uuid VARCHAR(255), date long, " +
                            "eventType VARCHAR(255), oGroup VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_worships_available (uuid_person VARCHAR(255), uuid_worship VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_worships_duty (uuid_person VARCHAR(255), uuid_worship VARCHAR(255))"
            ).executeUpdate();

            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }

    }


    public void saveEvent(WorshipEvent event) throws SQLException {
        databaseService.delete("module_worships", "uuid", event.getUUID().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_worships (uuid, date, eventType, oGroup) VALUES (?,?,?,?)"
        );

        preparedStatement.setString(1, event.getUUID().toString());
        preparedStatement.setLong(2, event.getDate());
        preparedStatement.setString(3, event.getEventType().toString());
        preparedStatement.setString(4, event.getoGroup().toString());

        preparedStatement.executeUpdate();
    }

    public ArrayList<WorshipEvent> getEvents() throws SQLException {
        ArrayList<WorshipEvent> eventSet = new ArrayList<>();
        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_worships").executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            long date = resultSet.getLong("date");
            EventType eventType = EventType.valueOf(resultSet.getString("eventType"));
            OGroup oGroup = OGroup.valueOf(resultSet.getString("oGroup"));

            WorshipEvent event = WorshipEvent.of(uuid, date, oGroup, eventType, new HashSet<>());
            eventSet.add(event);
        }
        return eventSet;
    }

    public void addAvailability(Messdiener messdiener, WorshipEvent event) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_worships_available (uuid_person, uuid_worship) VALUES (?,?)"
        );

        preparedStatement.setString(1, messdiener.getUUID().toString());
        preparedStatement.setString(2, event.getUUID().toString());

        preparedStatement.executeUpdate();

    }

    public void clearAvailability(Messdiener messdiener) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "DELETE FROM module_worships_available WHERE uuid_person = ?"
        );
        preparedStatement.setString(1, messdiener.getUUID().toString());

        preparedStatement.executeUpdate();
    }

    public void setAvailability(Messdiener messdiener, Set<WorshipEvent> events) throws SQLException {
        clearAvailability(messdiener);
        for (WorshipEvent event : events) {
            addAvailability(messdiener, event);
        }
    }

    public ArrayList<Pair<WorshipEvent, Boolean>> getAvailabilityByPerson(Messdiener messdiener) throws SQLException {
        ArrayList<Pair<WorshipEvent, Boolean>> pairs = new ArrayList<>();
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_worships_available WHERE uuid_person = ?"
        );
        preparedStatement.setString(1, messdiener.getUUID().toString());

        Set<UUID> uuids = new HashSet<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            uuids.add(UUID.fromString(resultSet.getString("uuid_worship")));
        }

        for (WorshipEvent event : getEvents()) {
            pairs.add(new Pair<>(event, uuids.contains(event.getUUID())));
        }
        return pairs;
    }

    public Set<Messdiener> getAvailablePersons(WorshipEvent event) throws SQLException {
        Set<Messdiener> availableMessdieners = new HashSet<>();
        for (Messdiener messdiener : Cache.MESSDIENER_SERVICE.getPersons()) {
            for (Pair<WorshipEvent, Boolean> pair : getAvailabilityByPerson(messdiener)) {

               if(pair.getSecond() && event.getUUID().equals(pair.getFirst().getUUID())) availableMessdieners.add(messdiener);
            }
        }
        return availableMessdieners;
    }

    public void dutyPerson(Messdiener messdiener, WorshipEvent event) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_worships_duty (uuid_person, uuid_worship) VALUES (?,?)"
        );

        preparedStatement.setString(1, messdiener.getUUID().toString());
        preparedStatement.setString(2, event.getUUID().toString());

        preparedStatement.executeUpdate();
    }

    public void clearDutyByPerson(Messdiener messdiener) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "DELETE FROM module_worships_duty WHERE uuid_person = ?"
        );
        preparedStatement.setString(1, messdiener.getUUID().toString());

        preparedStatement.executeUpdate();
    }

    public void clearDutyByEvent(WorshipEvent event) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "DELETE FROM module_worships_duty WHERE uuid_worship = ?"
        );
        preparedStatement.setString(1, event.getUUID().toString());

        preparedStatement.executeUpdate();
    }

    public Set<Messdiener> getPersonByDuty(WorshipEvent event) throws SQLException {
        Set<Messdiener> messdienerSet = new HashSet<>();
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_worships_duty WHERE uuid_worship = ?"
        );
        preparedStatement.setString(1, event.getUUID().toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid_person"));
            Messdiener messdiener = Cache.MESSDIENER_SERVICE.getPersons().stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow();

            messdienerSet.add(messdiener);
        }

        return messdienerSet;
    }

    public Set<WorshipEvent> getEventByDuty(Messdiener messdiener) throws SQLException {
        Set<WorshipEvent> eventSet = new HashSet<>();
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_worships_duty WHERE uuid_person = ?"
        );
        preparedStatement.setString(1, messdiener.getUUID().toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid_worship"));
            WorshipEvent event = getEvents().stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElseThrow();

            eventSet.add(event);
        }
        return eventSet;
    }

    public Optional<WorshipEvent> find(UUID uuid) throws SQLException {
        return getEvents().stream().filter(event -> event.getUUID().equals(uuid)).findFirst();
    }
}
