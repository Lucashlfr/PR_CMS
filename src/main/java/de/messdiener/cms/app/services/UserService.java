package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserService {

    private static final Logger LOGGER = Logger.getLogger("Manager.UserService");
    private final DatabaseService databaseService = Cache.getDatabaseService();


    public UserService() {

        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_user (user_uuid VARCHAR(255), username VARCHAR(255), " +
                            "password VARCHAR(255), permGroup VARCHAR(255), email VARCHAR(255))"
            ).executeUpdate();
            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }

        try {
            saveUser(Cache.SYSTEM_USER);
            saveUser(Cache.ALFRED_USER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) throws SQLException {

        if (getUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            getUsers().stream()
                    .filter(u -> u.getUsername().equals(user.getUsername()))
                    .findFirst()
                    .ifPresent(value -> user.setUser_uuid(value.getUser_UUID()));
        }


        deleteUser(user);

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_user (user_uuid, username, password, permGroup, email) VALUES (?,?,?,?,?)"
        );

        preparedStatement.setString(1, user.getUser_UUID().toString());
        preparedStatement.setString(2, user.getUsername());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.setString(4, user.getGroup().toString());
        preparedStatement.setString(5, user.getEmail());

        preparedStatement.executeUpdate();

        LOGGER.finest("User [" + user.getUsername() + "] erfolgreich erstellt.");

    }

    public boolean userExists(User user) throws SQLException {
        return getUser(user).isPresent();
    }

    public void deleteUser(User user) {
        databaseService.delete("module_user", "user_uuid", user.getUser_UUID().toString());
        LOGGER.finest("User [" + user.getUsername() + "] erfolgreich gelöscht.");
    }

    public Optional<User> getUser(User user) throws SQLException {
        return getUsers().stream().filter(u -> u.getUser_UUID().equals(user.getUser_UUID())).findFirst();

    }

    public Optional<User> getUser(UUID uuid) throws SQLException {
        return getUsers().stream().filter(u -> u.getUser_UUID().equals(uuid)).findFirst();

    }

    public ArrayList<User> getUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_user ORDER BY username").executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("user_uuid"));
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            UserGroup group = UserGroup.valueOf(resultSet.getString("permGroup"));
            String email = resultSet.getString("email");

            if (group != UserGroup.DEAKTIVIERT)
                users.add(new User(uuid, username, password, group, email));
        }
        return users;


    }

}
