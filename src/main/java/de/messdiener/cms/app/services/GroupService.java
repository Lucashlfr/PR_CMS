package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.messdiener.GroupSession;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.entities.messdiener.MessdienerGroup;
import de.messdiener.cms.app.entities.messdiener.MessdienerRank;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupService {

    public static final MessdienerRank DEFAULT = MessdienerRank.of(UUID.fromString("e9c9a475-8c7d-4725-a334-712c713ed657"), "Messdiener", 0);
    private static final MessdienerRank LEITUNGSTEAM = MessdienerRank.of(UUID.fromString("1ae38fbf-dd4d-4fee-bbd8-717ebd6ff742"), "Leitungsteam-Mitglied", 5);
    private static final MessdienerRank OBERMESSDIENER = MessdienerRank.of(UUID.fromString("1ae38fbf-dd4d-4fee-bbd8-717ebd6ff741"), "Obermessdiener", 10);

    private static final Logger LOGGER = Logger.getLogger("Manager.GroupService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public GroupService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_messdiener_group (uuid VARCHAR(255), name VARCHAR(255), logo_svg TEXT)"
            ).executeUpdate();
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_messdiener_rank (uuid VARCHAR(255), name VARCHAR(255), power INT)"
            ).executeUpdate();
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_messdiener_group_map (messdiener_uuid VARCHAR(255), group_uuid VARCHAR(255), rank_uuid VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_group_sessions (uuid VARCHAR(255), dateTime long, group_uuid VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_group_sessions_map (uuid_event VARCHAR(255), person_uuid VARCHAR(255))"
            ).executeUpdate();

            saveRank(DEFAULT);
            saveRank(LEITUNGSTEAM);
            saveRank(OBERMESSDIENER);

            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }
    }

    public void saveGroup(MessdienerGroup group) throws SQLException {
        databaseService.delete("module_messdiener_group", "uuid", group.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_messdiener_group (uuid, name, logo_svg) " +
                        "VALUES (?,?,?)"
        );
        preparedStatement.setString(1, group.getId().toString());
        preparedStatement.setString(2, group.getName());
        preparedStatement.setString(3, group.getLogoSvg());

        preparedStatement.executeUpdate();
    }

    public List<MessdienerGroup> getGroups() throws SQLException {
        List<MessdienerGroup> groups = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_messdiener_group ORDER BY name"
        );
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String name = resultSet.getString("name");
            String logoSvg = resultSet.getString("logo_svg");

            groups.add(MessdienerGroup.of(uuid, name, logoSvg, getByGroup(uuid)));
        }
        return groups;
    }
    public List<MessdienerGroup> getByUser(User user) throws SQLException {
        if(user.userHasPermission("*"))
            return getGroups();
        if(user.getPerson().isPresent()) {
            Messdiener messdiener = user.getPerson().get();
            return List.of(messdiener.getGroup());
        }
        return new ArrayList<>();
    }

    public Optional<MessdienerGroup> getGroup(UUID uuid) throws SQLException {
        return getGroups().stream().filter(group -> group.getId().equals(uuid)).findFirst();
    }

    public void map(MessdienerGroup group, Messdiener messdiener) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_messdiener_group_map (messdiener_uuid, group_uuid, rank_uuid) " +
                        "VALUES (?,?,?)"
        );
        preparedStatement.setString(1, messdiener.getId().toString());
        preparedStatement.setString(2, group.getId().toString());
        preparedStatement.setString(3, DEFAULT.getId().toString());

        preparedStatement.executeUpdate();
    }

    public List<Pair<Messdiener, MessdienerRank>> getByGroup(UUID groupUUID) throws SQLException {
        List<Pair<Messdiener, MessdienerRank>> pairArrayList = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_messdiener_group_map map, module_persons WHERE map.group_uuid = ? AND map.messdiener_uuid = module_persons.uuid ORDER BY module_persons.lastname"
        );
        preparedStatement.setString(1, groupUUID.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("messdiener_uuid"));
            Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(uuid).orElseThrow();

            MessdienerRank rank = getRankByUUID(UUID.fromString(resultSet.getString("rank_uuid")));

            Pair<Messdiener, MessdienerRank> pair = new Pair<>();
            pair.setFirst(messdiener);
            pair.setSecond(rank);

            pairArrayList.add(pair);
        }
        return pairArrayList;
    }

    private MessdienerRank getRankByUUID(UUID uuid) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_messdiener_rank WHERE uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String name = resultSet.getString("name");
            int power = resultSet.getInt("power");

            return MessdienerRank.of(uuid, name, power);
        }
        return DEFAULT;
    }

    public boolean personIsInGroup(UUID person) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_messdiener_group_map WHERE messdiener_uuid = ?"
        );
        preparedStatement.setString(1, person.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }

    public Optional<MessdienerGroup> getGroupByPerson(UUID person) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_messdiener_group_map WHERE messdiener_uuid = ?"
        );
        preparedStatement.setString(1, person.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return getGroup(UUID.fromString(resultSet.getString("group_uuid")));
        }
        return Optional.empty();
    }

    public void unmap(MessdienerGroup group, Messdiener messdiener) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "DELETE FROM module_messdiener_group_map WHERE group_uuid = ? AND messdiener_uuid = ?");
        preparedStatement.setString(1, group.getId().toString());
        preparedStatement.setString(2, messdiener.getId().toString());

        preparedStatement.executeUpdate();
    }

    public void saveRank(MessdienerRank rank) throws SQLException {
        databaseService.delete("module_messdiener_rank", "uuid", rank.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_messdiener_rank (uuid, name, power) VALUES (?,?,?)"
        );
        preparedStatement.setString(1, rank.getId().toString());
        preparedStatement.setString(2, rank.getName());
        preparedStatement.setInt(3, rank.getPower());

        preparedStatement.executeUpdate();
    }

    public void promote(Messdiener messdiener) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "UPDATE module_messdiener_group_map SET rank_uuid = ? WHERE messdiener_uuid = ?"
        );

        if (messdiener.getRank().getPower() == 0) {
            preparedStatement.setString(1, LEITUNGSTEAM.getId().toString());
        } else if (messdiener.getRank().getPower() == 5) {
            preparedStatement.setString(1, OBERMESSDIENER.getId().toString());
        } else {
            return;
        }

        preparedStatement.setString(2, messdiener.getId().toString());

        preparedStatement.executeUpdate();
    }

    public Optional<MessdienerRank> getRankByPerson(UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_messdiener_group_map WHERE messdiener_uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return Optional.of(getRankByUUID(UUID.fromString(resultSet.getString("rank_uuid"))));
        }
        return Optional.empty();
    }

    public void saveGroupSession(GroupSession groupSession) throws SQLException {

        databaseService.delete("module_group_sessions", "uuid", groupSession.getId().toString());


        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_group_sessions (uuid, dateTime, group_uuid) VALUES (?,?,?)"
        );
        preparedStatement.setString(1, groupSession.getId().toString());
        preparedStatement.setLong(2, groupSession.getDateTime());
        preparedStatement.setString(3, groupSession.getGroup().toString());

        mapSession(groupSession);

        preparedStatement.executeUpdate();
    }

    public List<GroupSession> getSessions(UUID groupUUID) throws SQLException {
        List<GroupSession> sessions = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_group_sessions WHERE group_uuid = ?"
        );
        preparedStatement.setString(1, groupUUID.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            long dateTime = resultSet.getLong("dateTime");

            GroupSession groupSession = GroupSession.of(uuid, dateTime, groupUUID);
            groupSession.setMessdieners(getAttendances(uuid));

            sessions.add(groupSession);
        }

        return sessions;
    }

    public List<Messdiener> getAttendances(UUID uuid) throws SQLException {
        List<Messdiener> messdieners = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_group_sessions_map WHERE uuid_event = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            UUID messdienerUuid = UUID.fromString(resultSet.getString("person_uuid"));

            Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(messdienerUuid).orElseThrow();
            messdieners.add(messdiener);
        }
        return messdieners;
    }

    public void mapSession(GroupSession groupSession) throws SQLException {

        databaseService.delete("module_group_sessions_map", "uuid_event", groupSession.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_group_sessions_map (uuid_event, person_uuid) VALUES (?,?)"
        );
        preparedStatement.setString(1, groupSession.getId().toString());

        for (Messdiener messdiener : groupSession.getMessdieners()) {

            preparedStatement.setString(2, messdiener.getId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void remove(GroupSession groupSession, Messdiener messdiener) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "DELETE FROM module_group_sessions_map WHERE uuid_event = ? AND person_uuid = ?");
        preparedStatement.setString(1, groupSession.getId().toString());
        preparedStatement.setString(2, messdiener.getId().toString());

        preparedStatement.executeUpdate();
    }

    public Optional<GroupSession> getSession(UUID uuid) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_group_sessions WHERE uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            UUID groupUUID = UUID.fromString(resultSet.getString("group_uuid"));
            long dateTime = resultSet.getLong("dateTime");

            GroupSession groupSession = GroupSession.of(uuid, dateTime, groupUUID);
            groupSession.setMessdieners(getAttendances(uuid));

            return Optional.of(groupSession);
        }
        return Optional.empty();

    }
}
