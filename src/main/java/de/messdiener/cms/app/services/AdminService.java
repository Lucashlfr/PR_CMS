package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.admin.AdminToggleEntity;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class AdminService {

    private static final Logger LOGGER = Logger.getLogger("Manager.AdminService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public AdminService(){



        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_admin (uuid VARCHAR(255), name VARCHAR(255), toggle_value VARCHAR(255), toggle_type VARCHAR(255))"
            ).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void save(AdminToggleEntity adminToggleEntity) throws SQLException {
        databaseService.delete("module_admin", "uuid", adminToggleEntity.getUUID().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_admin (uuid, name, toggle_value, toggle_type) VALUES (?, ?,?, ?)"
        );
        preparedStatement.setString(1, adminToggleEntity.getUUID().toString());
        preparedStatement.setString(2, adminToggleEntity.getName());
        preparedStatement.setString(3, adminToggleEntity.getValue());
        preparedStatement.setString(4, adminToggleEntity.getType());

        preparedStatement.executeUpdate();
    }

    public ArrayList<AdminToggleEntity> getToggles() throws SQLException {
        ArrayList<AdminToggleEntity> toggles = new ArrayList<>();
        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_admin ORDER BY name").executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String name = resultSet.getString("name");
            String value = resultSet.getString("toggle_value");
            AdminToggleEntity.Type type = AdminToggleEntity.Type.valueOf(resultSet.getString("toggle_type"));

            toggles.add(AdminToggleEntity.of(uuid, name, value, type));
        }
        return toggles;
    }

    public AdminToggleEntity get(String name) throws SQLException {
        return getToggles().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(AdminToggleEntity.empty());
    }

}