package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.flow.Flow;
import de.messdiener.cms.app.entities.flow.sub.CashFlow;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.FlowEnums;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FlowService {

    private final DatabaseService databaseService = Cache.getDatabaseService();

    public FlowService(){

        try {

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_flow (uuid VARCHAR(255), date long, type VARCHAR(255), state VARCHAR(255), owner VARCHAR(255))"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS  module_flow_cash (uuid VARCHAR(255), person_uuid VARCHAR(255), card_owner VARCHAR(255), mail VARCHAR(255), iban VARCHAR(255), bic VARCHAR(255), data TEXT, zweck VARCHAR(255),  file_uuid VARCHAR(255))"
            ).executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void saveFlow(Flow flow) throws SQLException{

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_flow (uuid, date, type, state, owner) VALUES (?,?,?,?,?)"
        );

        preparedStatement.setString(1, flow.getUUID().toString());
        preparedStatement.setLong(2, flow.getDate());
        preparedStatement.setString(3, flow.getType().toString());
        preparedStatement.setString(4, flow.getState().toString());
        preparedStatement.setString(5, flow.getOwner().toString());

        preparedStatement.executeUpdate();
    }

    public List<Flow> getFlowsByOwner(UUID owner) throws SQLException {
        List<Flow> flows = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_flow WHERE owner = ?"
        );
        preparedStatement.setString(1, owner.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            long date = resultSet.getLong("date");
            FlowEnums.Type type = FlowEnums.Type.valueOf(resultSet.getString("type"));
            FlowEnums.State state = FlowEnums.State.valueOf(resultSet.getString("state"));

            Flow flow = new Flow(uuid, date, type, state, uuid);
            flows.add(flow);
        }

        return flows;
    }

    public void saveCashFlow(CashFlow cashFlow) throws SQLException {

        databaseService.delete("module_flow_cash", "uuid", cashFlow.getUUID().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_flow_cash (uuid, person_uuid, card_owner, mail, iban, bic, data, zweck, file_uuid) VALUES (?,?,?,?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, cashFlow.getUUID().toString());
        preparedStatement.setString(2, cashFlow.getPerson_uuid().toString());
        preparedStatement.setString(3, cashFlow.getCard_owner());
        preparedStatement.setString(4, cashFlow.getMail());
        preparedStatement.setString(5, cashFlow.getIban());
        preparedStatement.setString(6, cashFlow.getBic());
        preparedStatement.setString(7, cashFlow.getData());
        preparedStatement.setString(8, cashFlow.getZweck());
        preparedStatement.setString(9, cashFlow.getFile_uuid().toString());

        preparedStatement.executeUpdate();
    }

    public Optional<CashFlow> getCashFlowByUUID(UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_flow_cash WHERE uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            UUID person_uuid = UUID.fromString(resultSet.getString("person_uuid"));
            UUID file_uuid = UUID.fromString(resultSet.getString("file_uuid"));

            String card_owner = resultSet.getString("card_owner");
            String mail = resultSet.getString("mail");
            String iban = resultSet.getString("iban");
            String bic = resultSet.getString("bic");
            String data = resultSet.getString("data");
            String zweck = resultSet.getString("zweck");

            CashFlow cashFlow = new CashFlow(uuid, person_uuid, card_owner, mail, iban, bic, data, zweck, file_uuid);
            return Optional.of(cashFlow);
        }
        return Optional.empty();
    }

    public Optional<Flow> getFlowByUUID(Messdiener messdiener, UUID fromString) throws SQLException {
        return getFlowsByOwner(messdiener.getUUID()).stream().filter(f -> f.getUUID().equals(fromString)).findFirst();
    }

    public Optional<Flow> getFlow(UUID uuid) throws SQLException {

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_flow WHERE uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            long date = resultSet.getLong("date");
            FlowEnums.Type type = FlowEnums.Type.valueOf(resultSet.getString("type"));
            FlowEnums.State state = FlowEnums.State.valueOf(resultSet.getString("state"));
            UUID owner = UUID.fromString(resultSet.getString("owner"));

            Flow flow = new Flow(uuid, date, type, state, owner);
            return Optional.of(flow);
        }
        return Optional.empty();

    }
}
