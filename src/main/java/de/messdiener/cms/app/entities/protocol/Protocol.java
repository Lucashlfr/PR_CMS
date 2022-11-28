package de.messdiener.cms.app.entities.protocol;

import de.messdiener.cms.app.entities.protocol.cache.ProtocolComment;
import de.messdiener.cms.app.entities.protocol.cache.ProtocolElement;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Protocol {

    private UUID uuid;
    private UUID owner_uuid;
    private String request_name;
    private long lastUpdate;
    private boolean editable;
    private String description;

    private ArrayList<ProtocolComment> comments;
    private ArrayList<ProtocolElement> elements;

    public Protocol(UUID uuid, UUID owner_uuid, String request_name, long lastUpdate, boolean editable, String description) {
        this.uuid = uuid;
        this.owner_uuid = owner_uuid;
        this.request_name = request_name;
        this.lastUpdate = lastUpdate;
        this.editable = editable;
        this.description = description;
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.saveProtocol(this);
    }

    public static Protocol empty() throws SQLException {
        return new Protocol(UUID.randomUUID(), SecurityHelper.getUser().getUser_UUID(), "KEIN NAME", System.currentTimeMillis(), true, "");
    }

    public User getOwner() throws SQLException {
        return Cache.USER_SERVICE.getUser(owner_uuid).orElseThrow();
    }

    public String getDate(){
        return DateUtils.convertLongToDate(lastUpdate, DateUtils.DateType.GERMAN);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getOwner_uuid() {
        return owner_uuid;
    }

    public void setOwner_uuid(UUID owner_uuid) {
        this.owner_uuid = owner_uuid;
    }

    public String getRequest_name() {
        return request_name;
    }

    public void setRequest_name(String request_name) {
        this.request_name = request_name;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setComments(ArrayList<ProtocolComment> comments) {
        this.comments = comments;
    }

    public ArrayList<ProtocolComment> getComments() {
        return comments;
    }

    public void setElements(ArrayList<ProtocolElement> elements) {
        this.elements = elements;
    }

    public ArrayList<ProtocolElement> getElements() {
        return elements;
    }
}
