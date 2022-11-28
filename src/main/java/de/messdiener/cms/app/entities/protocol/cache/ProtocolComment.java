package de.messdiener.cms.app.entities.protocol.cache;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;

import java.sql.SQLException;
import java.util.UUID;

public class ProtocolComment {

    private final UUID uuid;
    private final UUID protocol;
    private final UUID creator;
    private final long date;
    private final String text;

    public ProtocolComment(UUID uuid, UUID protocol, UUID creator, long date, String text) {
        this.uuid = uuid;
        this.protocol = protocol;
        this.creator = creator;
        this.date = date;
        this.text = text;
    }

    public User getUser() throws SQLException {
        return Cache.USER_SERVICE.getUser(creator).orElseThrow();
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.addComment(this);
    }

    public String getDateGerman(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getProtocol() {
        return protocol;
    }

    public UUID getCreator() {
        return creator;
    }

    public long getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
