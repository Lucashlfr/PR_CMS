package de.messdiener.cms.app.entities.protocol;

import de.messdiener.cms.app.entities.protocol.cache.ProtocolComment;
import de.messdiener.cms.app.entities.protocol.cache.ProtocolElement;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;
import lombok.Data;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Data
public class Protocol {

    private UUID id;
    private UUID ownerUUID;
    private String requestName;
    private long lastUpdate;
    private boolean editable;
    private String description;

    private List<ProtocolComment> comments;
    private List<ProtocolElement> elements;

    public Protocol(UUID id, UUID ownerUUID, String requestName, long lastUpdate, boolean editable, String description) {
        this.id = id;
        this.ownerUUID = ownerUUID;
        this.requestName = requestName;
        this.lastUpdate = lastUpdate;
        this.editable = editable;
        this.description = description;
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.saveProtocol(this);
    }

    public static Protocol empty() throws SQLException {
        return new Protocol(UUID.randomUUID(), SecurityHelper.getUser().getUserID(), "KEIN NAME", System.currentTimeMillis(), true, "");
    }

    public User getOwner() throws SQLException {
        return Cache.USER_SERVICE.getUser(ownerUUID).orElseThrow();
    }

    public String getDate(){
        return DateUtils.convertLongToDate(lastUpdate, DateUtils.DateType.GERMAN);
    }

}
