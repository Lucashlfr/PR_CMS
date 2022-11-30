package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.user.Permission;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
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
                            "firstname VARCHAR(255), lastname VARCHAR(255), password VARCHAR(255), permGroup VARCHAR(255), email VARCHAR(255)," +
                            " permissions TEXT, profile_img VARCHAR(255), person_uuid VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_user_permission (permName VARCHAR(255), permDescr VARCHAR(255), PRIMARY KEY (permName))"
            ).executeUpdate();
            createPermission(new Permission("*", "Alle Rechte -> nur für Admins"));
            createPermission(new Permission("LOGIN", "Benutzer kann sich anmelden"));
            createPermission(new Permission("LEKTOR_1", "Benutzer kann Lektor_1"));
            createPermission(new Permission("LEKTOR_2", "Benutzer kann Lektor_2"));
            createPermission(new Permission("CMS","Main Recht für alle CMS Aufgaben"));
            createPermission(new Permission("Flows","Main Recht für Flows"));
            createPermission(new Permission("MMS","Main Recht für MMS"));
            createPermission(new Permission("MMS_ADMIN","Recht für Gruppenverwaltung"));
            createPermission(new Permission("MMS_CSV_IMPORT","Recht für CSV-Importe"));
            createPermission(new Permission("MMS_FINANCE_DIRECT","Kann direkt Finanzen erfassen"));
            createPermission(new Permission("MMS_FINANCE_DELETE","Kann Finanzen löschen"));
            createPermission(new Permission("mms_display_all", "Kann alle Messdiener anschauen"));
            createPermission(new Permission("CS","Rechte für Cloud-Service"));
            createPermission(new Permission("ADMIN", "Recht für Admin-Pannel"));
            createPermission(new Permission("TEAM", "Benutzer ist Teil des Teams"));

        LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }

        try {

            if(!userExists(Cache.SYSTEM_USER))
                saveUser(Cache.SYSTEM_USER);

            if(!userExists(Cache.ALFRED_USER))
                saveUser(Cache.ALFRED_USER);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void saveUser(User user) throws SQLException {

        if (getUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            getUsers().stream()
                    .filter(u -> u.getUsername().equals(user.getUsername()))
                    .findFirst()
                    .ifPresent(value -> user.setUserID(value.getUserID()));
        }


        deleteUser(user);

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_user (user_uuid, username, firstname, lastname, password, permGroup, email, permissions, profile_img) VALUES (?,?,?,?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, user.getUserID().toString());
        preparedStatement.setString(2, user.getUsername());
        preparedStatement.setString(3, user.getFirstname());
        preparedStatement.setString(4, user.getLastname());
        preparedStatement.setString(5, user.getPassword());
        preparedStatement.setString(6, user.getGroup().toString());
        preparedStatement.setString(7, user.getEmail());
        preparedStatement.setString(8, user.getPermissionString());
        preparedStatement.setString(9, user.getImgPath());


        preparedStatement.executeUpdate();

        LOGGER.finest("User [" + user.getUsername() + "] erfolgreich erstellt.");


    }

    public boolean userExists(User user) throws SQLException {
        return getUser(user).isPresent();
    }

    public void deleteUser(User user) {
        databaseService.delete("module_user", "user_uuid", user.getUserID().toString());
        LOGGER.finest("User [" + user.getUsername() + "] erfolgreich gelöscht.");
    }

    public Optional<User> getUser(User user) throws SQLException {
        return getUsers().stream().filter(u -> u.getUserID().equals(user.getUserID())).findFirst();

    }

    public Optional<User> getUser(UUID uuid) throws SQLException {
        return getUsers().stream().filter(u -> u.getUserID().equals(uuid)).findFirst();

    }

    public ArrayList<User> getUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_user ORDER BY lastname").executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("user_uuid"));
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            UserGroup group = UserGroup.valueOf(resultSet.getString("permGroup"));
            String email = resultSet.getString("email");
            String firstname = resultSet.getString("firstname");
            String lastname = resultSet.getString("lastname");
            ArrayList<Permission> permissions = generateByString(resultSet.getString("permissions"));
            String path = resultSet.getString("profile_img");
            Optional<UUID> person_uuid = resultSet.getString("person_uuid") != null
                    ? Optional.of(UUID.fromString(resultSet.getString("person_uuid")))
                    : Optional.empty();

            if (group != UserGroup.DEAKTIVIERT)
                users.add(User.of(uuid, username, firstname, lastname, password, group, email, permissions, path, person_uuid));
        }
        return users;


    }

    public ArrayList<Permission> generateByString(String input) throws SQLException {

        ArrayList<Permission> permissions = new ArrayList<>();

        String[] s = input.split(";");
        for (String name : s) {
                getPermissions().stream().filter(p -> p.getName().equals(name)).findFirst().ifPresent(permissions::add);
        }
        return permissions;
    }


    public void createPermission(Permission permission) throws SQLException {

        if(databaseService.exists("module_user_permission", "permName", permission.getName()))return;

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_user_permission (permName, permDescr) VALUES (?, ?)"
        );
        preparedStatement.setString(1, permission.getName());
        preparedStatement.setString(2, permission.getDescription());

        preparedStatement.executeUpdate();
    }

    public void deletePermission(Permission permission){
        databaseService.delete("module_user_permission", "permName", permission.getName());
    }

    public ArrayList<Permission> getPermissions() throws SQLException {
        ArrayList<Permission> permissions = new ArrayList<>();
        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_user_permission ORDER BY permName").executeQuery();
        while (resultSet.next()) {

            String name = resultSet.getString("permName");
            String description = resultSet.getString("permDescr");

            Permission permission = new Permission(name, description);
            permissions.add(permission);
        }
        return permissions;
    }

    public void createUserInSecurity(User user){
        UserDetails systemUser = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .passwordEncoder(Cache.getPasswordEncoder()::encode)
                .roles(user.getGroup().toString()).build();

        if(Cache.getUserDetailsManager().userExists(user.getUsername())){
            Cache.getUserDetailsManager().deleteUser(user.getUsername());
        }
        Cache.getUserDetailsManager().createUser(systemUser);
    }

    public Permission getPermission(String permName) throws SQLException {

        return getPermissions().stream().filter(permission -> permission.getName().equals(permName)).findFirst().orElseThrow();
    }

    public ArrayList<User> getTeam() throws SQLException {
        ArrayList<User> team = new ArrayList<>();

        for(User user : getUsers()){
            if(user.userHasPermission("team"))
                team.add(user);
        }

        return team;
    }

    public void addPersonUUID(UUID user, UUID person) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "UPDATE module_user SET person_uuid = ? WHERE user_uuid = ?"
        );

        preparedStatement.setString(1, person.toString());
        preparedStatement.setString(2, user.toString());

        preparedStatement.executeUpdate();
    }

    public void removePersonUUID(UUID user) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "UPDATE module_user SET person_uuid = NULL WHERE user_uuid = ?"
        );

        preparedStatement.setString(1, user.toString());

        preparedStatement.executeUpdate();
    }

}
