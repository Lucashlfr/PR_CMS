package de.messdiener.cms.app.services;


import de.messdiener.cms.app.entities.finance.FinanceEntry;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.OGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinanceService {

    private static final Logger LOGGER = Logger.getLogger("Manager.FinanceService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public FinanceService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_finance (uuid VARCHAR(255), oGroup VARCHAR(255), date long, revenue double," +
                            "expenditures double, type VARCHAR(255), editor VARCHAR(255), costCenter VARCHAR(255), note TEXT);"
            ).executeUpdate();
            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }
    }

    public void saveEntity(FinanceEntry financeEntry) throws SQLException {
        databaseService.delete("module_finance", "uuid", financeEntry.getUUID().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_finance (uuid, oGroup, date, revenue, expenditures, type, editor, costCenter, note) VALUES (?,?,?,?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, financeEntry.getUUID().toString());
        preparedStatement.setString(2, financeEntry.getoGroup().toString());
        preparedStatement.setLong(3, financeEntry.getDate());
        preparedStatement.setDouble(4, financeEntry.getRevenue());
        preparedStatement.setDouble(5, financeEntry.getExpenditures());
        preparedStatement.setString(6, financeEntry.getType());
        preparedStatement.setString(7, financeEntry.getEditor());
        preparedStatement.setString(8, financeEntry.getCostCenter());
        preparedStatement.setString(9, financeEntry.getNote());

        preparedStatement.executeUpdate();
    }

    public ArrayList<FinanceEntry> getAllEntities() throws SQLException {
        ArrayList<FinanceEntry> entryArrayList = new ArrayList<>();

        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_finance ORDER BY date").executeQuery();

        extracted(entryArrayList, resultSet);
        return entryArrayList;
    }

    private static void extracted(ArrayList<FinanceEntry> entryArrayList, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            UUID oGroup = UUID.fromString(resultSet.getString("oGroup"));
            long date = resultSet.getLong("date");
            double revenue = resultSet.getDouble("revenue");
            double expenditures = resultSet.getDouble("expenditures");
            String type = resultSet.getString("type");
            String editor = resultSet.getString("editor");
            String costCenter = resultSet.getString("costCenter");
            String note = resultSet.getString("note");

            FinanceEntry financeEntry = FinanceEntry.of(uuid, oGroup, date, revenue, expenditures, type, editor, costCenter, note);
            entryArrayList.add(financeEntry);
        }
    }

    public ArrayList<FinanceEntry> getEntities(UUID oGroup) throws SQLException {
        ArrayList<FinanceEntry> entryArrayList = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement("SELECT * FROM module_finance WHERE oGroup = ? ORDER BY date");
        preparedStatement.setString(1, oGroup.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        extracted(entryArrayList, resultSet);
        return entryArrayList;
    }


    public Optional<FinanceEntry> find(UUID uuid) throws SQLException {
        return getAllEntities().stream().filter(entry -> entry.getUUID().equals(uuid)).findFirst();
    }

    public void delete(UUID uuid) {
        databaseService.delete("module_finance", "uuid", uuid.toString());
    }


}
