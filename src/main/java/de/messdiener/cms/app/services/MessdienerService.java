package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.OGroup;
import de.messdiener.cms.cache.enums.PersonRank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessdienerService {

    private static final Logger LOGGER = Logger.getLogger("Manager.PersonService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public MessdienerService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_persons (uuid VARCHAR(255), firstname VARCHAR(255), " +
                            "lastname VARCHAR(255), adress VARCHAR(255), birthday long, " +
                            "phone VARCHAR(255), mobil_child VARCHAR(255), mobile_parents VARCHAR(255), " +
                            "mail_child VARCHAR(255), mail_parents VARCHAR(255), notes TEXT)"
            ).executeUpdate();
            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }

    }

    public void save(Messdiener messdiener) throws SQLException {
        databaseService.delete("module_persons", "uuid", messdiener.getUUID().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_persons (uuid, firstname, lastname,adress, birthday, phone, mobil_child, mobile_parents, mail_child, mail_parents, notes) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, messdiener.getUUID().toString());
        preparedStatement.setString(2, messdiener.getFirstname());
        preparedStatement.setString(3, messdiener.getLastname());
        preparedStatement.setString(4, messdiener.getAdress());
        preparedStatement.setLong(5, messdiener.getBirthday());
        preparedStatement.setString(6, messdiener.getPhone());
        preparedStatement.setString(7, messdiener.getMobile_child());
        preparedStatement.setString(8, messdiener.getMobile_parents());
        preparedStatement.setString(9, messdiener.getMail_child());
        preparedStatement.setString(10, messdiener.getMail_parents());
        preparedStatement.setString(11, messdiener.getNotes());

        preparedStatement.executeUpdate();
    }

    public ArrayList<Messdiener> getPersons() throws SQLException {
        ArrayList<Messdiener> messdienerSet = new ArrayList<>();
        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_persons ORDER BY lastname").executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String firstname = resultSet.getString("firstname");
            String lastname = resultSet.getString("lastname");
            String adress = resultSet.getString("adress");
            long birthday = resultSet.getLong("birthday");
            String phone = resultSet.getString("phone");
            String mobile_child = resultSet.getString("mobil_child");
            String mobile_parents = resultSet.getString("mobile_parents");
            String mail_child = resultSet.getString("mail_child");
            String mail_parents = resultSet.getString("mail_parents");
            String notes = resultSet.getString("notes");

            Messdiener messdiener = Messdiener.of(uuid, firstname, lastname, adress, birthday, phone, mobile_child, mobile_parents, mail_child, mail_parents, notes);
            messdienerSet.add(messdiener);

        }
        return messdienerSet;
    }

    public ArrayList<Messdiener> getPersonsWhoUserCanEdit(User user) throws SQLException {
        if(user.userHasPermission("*") || user.userHasPermission("mms_display_all"))
            return getPersons();
        if(user.getPerson().isEmpty())
            return new ArrayList<>();
        return user.getPerson().get().getGroup().inGroup();
    }

    public Optional<Messdiener> find(UUID uuid) throws SQLException {
        return getPersons().stream().filter(person -> person.getUUID().equals(uuid)).findFirst();
    }

    public void delte(Messdiener messdiener) {
        databaseService.delete("module_persons", "uuid", messdiener.getUUID().toString());
    }



}
