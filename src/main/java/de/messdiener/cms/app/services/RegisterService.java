package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.user.RegisterRequestEntity;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterService {

    private static final Logger LOGGER = Logger.getLogger("Manager.TicketService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public RegisterService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_register_requests (uuid VARCHAR(255), username VARCHAR(255), firstname VARCHAR(255), lastname VARCHAR(255), password VARCHAR(255), email VARCHAR(255), requestDate long, requestCode VARCHAR(255))"
            ).executeUpdate();

            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }
    }

    public void save(RegisterRequestEntity registerRequest) throws SQLException {
        databaseService.delete("module_register_requests", "uuid", registerRequest.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_register_requests (uuid, username, firstname, lastname, password, email, requestDate, requestCode) VALUES (?,?,?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, registerRequest.getId().toString());
        preparedStatement.setString(2,registerRequest.getUsername());
        preparedStatement.setString(3,registerRequest.getFirstname());
        preparedStatement.setString(4,registerRequest.getLastname());
        preparedStatement.setString(5,registerRequest.getPassword());
        preparedStatement.setString(6,registerRequest.getEmail());
        preparedStatement.setLong(7,registerRequest.getRequestDate());
        preparedStatement.setString(8,registerRequest.getRequestCode().toString());

        preparedStatement.executeUpdate();
    }

    public Set<RegisterRequestEntity> getRequests() throws SQLException {

        Set<RegisterRequestEntity> requests = new HashSet<>();

        ResultSet resultSet = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_register_requests"
        ).executeQuery();

        while (resultSet.next()) {

            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            UUID requestCode = UUID.fromString(resultSet.getString("requestCode"));

            String username = resultSet.getString("username");
            String firstname = resultSet.getString("firstname");
            String lastname = resultSet.getString("lastname");
            String password = resultSet.getString("password");
            String email = resultSet.getString("email");

            long requestDate = resultSet.getLong("requestDate");

            RegisterRequestEntity registerRequest = RegisterRequestEntity.of(uuid, requestCode, username, firstname, lastname, password, email, requestDate);

            requests.add(registerRequest);
        }
        return requests;
    }

}
