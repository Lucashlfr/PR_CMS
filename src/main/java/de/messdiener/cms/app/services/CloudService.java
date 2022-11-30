package de.messdiener.cms.app.services;

import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.entities.protocol.Protocol;
import de.messdiener.cms.app.entities.protocol.cache.ProtocolComment;
import de.messdiener.cms.app.entities.protocol.cache.ProtocolElement;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.security.SecurityHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudService {

    private static final Logger LOGGER = Logger.getLogger("Manager.Cloud");
    private static final String MODULE_CLOUD = "module_cloud";
    private static final String MODULE_PROTOCOLS = "module_protocols";
    private static final String MODULE_COMMENTS = "module_protocols_comments";
    private static final String MODULE_ELEMENTS = "module_protocols_elements";
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public CloudService() {
        LOGGER.setLevel(Level.ALL);
        try {
            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_cloud (uuid VARCHAR(255), owner_uuid VARCHAR(255), filename VARCHAR(255), type VARCHAR(255), " +
                            "date long, visible BOOLEAN, data LONGBLOB)"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_protocols (uuid VARCHAR(255), owner_uuid VARCHAR(255), request_name VARCHAR(255), lastUpdate long, editable boolean, description LONGTEXT)"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_protocols_comments (uuid VARCHAR(255), protocol_uuid VARCHAR(255), creator_uuid VARCHAR(255), date long, text LONGTEXT)"
            ).executeUpdate();

            databaseService.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS module_protocols_elements (uuid VARCHAR(255), protocol_uuid VARCHAR(255), color_class VARCHAR(255), icon_class VARCHAR(255), headline VARCHAR(255), text LONGTEXT)"
            ).executeUpdate();

            LOGGER.finest("Database erfolgreich erstellt.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }
    }


    public void save(FileEntity file) throws SQLException {
        databaseService.delete(MODULE_CLOUD, "uuid", file.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_cloud (uuid, owner_uuid, filename, type, date, visible, data) VALUES (?,?,?,?,?,?,?)"
        );
        preparedStatement.setString(1, file.getId().toString());
        preparedStatement.setString(2, file.getOwner());
        preparedStatement.setString(3, file.getName());
        preparedStatement.setString(4, file.getType());
        preparedStatement.setLong(5, file.getDate());
        preparedStatement.setBoolean(6, true);
        preparedStatement.setBytes(7, file.getData());

        preparedStatement.executeUpdate();
    }

    public Set<FileEntity> getFiles() throws SQLException {
        HashSet<FileEntity> fileEntities = new HashSet<>();

        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_cloud WHERE visible").executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String owner = resultSet.getString("owner_uuid");
            String filename = resultSet.getString("filename");
            String type = resultSet.getString("type");
            long date = resultSet.getLong("date");
            byte[] data = resultSet.getBytes("data");

            FileEntity file = new FileEntity(uuid, owner, filename, type, date, data);
            fileEntities.add(file);
        }
        return fileEntities;
    }

    public FileEntity get(String uuid) throws SQLException {
        return getFiles().stream().filter(files -> files.getId().toString().equals(uuid)).findFirst().orElseThrow();
    }

    public static String getPath(UUID uuid) {
        return "/dc/cloud/file?uuid=" + uuid;
    }

    public Set<FileEntity> getYourFiles() throws SQLException {

        HashSet<FileEntity> fileEntities = new HashSet<>();
        for (FileEntity file : getFiles()) {
            if (file.getOwner().equals(SecurityHelper.getUser().getUserID().toString())) {
                fileEntities.add(file);
            }
        }
        return fileEntities;
    }

    public void delete(UUID uuid) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "UPDATE module_cloud SET visible = false WHERE uuid = ?"
        );
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.executeUpdate();
    }

    public void saveProtocol(Protocol protocol) throws SQLException {
        databaseService.delete(MODULE_PROTOCOLS, "uuid", protocol.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_protocols (uuid, owner_uuid, request_name, lastUpdate, editable, description) VALUES (?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, protocol.getId().toString());
        preparedStatement.setString(2, protocol.getOwnerUUID().toString());
        preparedStatement.setString(3, protocol.getRequestName());
        preparedStatement.setLong(4, protocol.getLastUpdate());
        preparedStatement.setBoolean(5, protocol.isEditable());
        preparedStatement.setString(6, protocol.getDescription());

        preparedStatement.executeUpdate();
    }

    public Set<Protocol> getProtocols() throws SQLException {
        Set<Protocol> protocols = new HashSet<>();

        ResultSet resultSet = databaseService.getConnection().prepareStatement("SELECT * FROM module_protocols").executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            UUID ownerUuid = UUID.fromString(resultSet.getString("owner_uuid"));
            String requestName = resultSet.getString("request_name");
            long lastUpdate = resultSet.getLong("lastUpdate");
            boolean editable = resultSet.getBoolean("editable");
            String description = resultSet.getString("description");

            Protocol protocol = new Protocol(uuid, ownerUuid, requestName, lastUpdate, editable, description);
            protocol.setComments(getComments(protocol));
            protocol.setElements(getElements(protocol));

            protocols.add(protocol);
        }
        return protocols;
    }

    public void addComment(ProtocolComment comment) throws SQLException {
        databaseService.delete(MODULE_COMMENTS, "uuid", comment.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_protocols_comments (uuid, protocol_uuid, creator_uuid, date, text) VALUES (?,?,?,?,?)"
        );

        preparedStatement.setString(1, comment.getId().toString());
        preparedStatement.setString(2, comment.getProtocol().toString());
        preparedStatement.setString(3, comment.getCreator().toString());
        preparedStatement.setLong(4, comment.getDate());
        preparedStatement.setString(5, comment.getText());

        preparedStatement.executeUpdate();
    }

    public List<ProtocolComment> getComments(Protocol protocol) throws SQLException {
        List<ProtocolComment> comments = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_protocols_comments WHERE protocol_uuid = ? ORDER BY date");
        preparedStatement.setString(1, protocol.getId().toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            UUID creatorUuid = UUID.fromString(resultSet.getString("creator_uuid"));
            long date = resultSet.getLong("date");
            String text = resultSet.getString("text");

            ProtocolComment protocolComment = new ProtocolComment(uuid, protocol.getId(), creatorUuid, date, text);
            comments.add(protocolComment);
        }
        return comments;
    }

    public void addElement(ProtocolElement element) throws SQLException {
        databaseService.delete(MODULE_ELEMENTS, "uuid", element.getId().toString());

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "INSERT INTO module_protocols_elements (uuid, protocol_uuid, color_class, icon_class, headline, text) VALUES (?,?,?,?,?,?)"
        );

        preparedStatement.setString(1, element.getId().toString());
        preparedStatement.setString(2, element.getProtocolUUID().toString());
        preparedStatement.setString(3, element.getColorClass());
        preparedStatement.setString(4, element.getIconClass());
        preparedStatement.setString(5, element.getHeadline());
        preparedStatement.setString(6, element.getText());

        preparedStatement.executeUpdate();
    }

    public List<ProtocolElement> getElements(Protocol protocol) throws SQLException {
        List<ProtocolElement> elements = new ArrayList<>();

        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "SELECT * FROM module_protocols_elements WHERE protocol_uuid = ?");
        preparedStatement.setString(1, protocol.getId().toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String colorClass = resultSet.getString("color_class");
            String iconClass = resultSet.getString("icon_class");
            String headline = resultSet.getString("headline");
            String text = resultSet.getString("text");

            ProtocolElement protocolComment = new ProtocolElement(uuid, protocol.getId(), colorClass, iconClass, headline, text);
            elements.add(protocolComment);
        }
        return elements;
    }

    public void deleteProtocol(Protocol protocol) {
        databaseService.delete(MODULE_PROTOCOLS, "uuid", protocol.getId().toString());
        databaseService.delete(MODULE_COMMENTS, "protocol_uuid", protocol.getId().toString());
        databaseService.delete(MODULE_ELEMENTS, "protocol_uuid", protocol.getId().toString());
    }

    public void deleteElement(UUID protocol, UUID element) throws SQLException {
        PreparedStatement preparedStatement = databaseService.getConnection().prepareStatement(
                "DELETE FROM module_protocols_elements WHERE protocol_uuid = ? AND uuid = ?"
        );
        preparedStatement.setString(1, protocol.toString());
        preparedStatement.setString(2, element.toString());

        preparedStatement.executeUpdate();
    }
}
