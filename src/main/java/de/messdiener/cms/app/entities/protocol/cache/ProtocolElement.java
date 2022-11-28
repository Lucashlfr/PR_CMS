package de.messdiener.cms.app.entities.protocol.cache;

import de.messdiener.cms.cache.Cache;

import java.sql.SQLException;
import java.util.UUID;

public class ProtocolElement {

    private UUID uuid;
    private UUID protocol_uuid;
    private String color_class;
    private String icon_class;
    private String headline;
    private String text;

    public ProtocolElement(UUID uuid, UUID protocolUUID, String color_class, String icon_class, String headline, String text) {
        this.uuid = uuid;
        this.protocol_uuid = protocolUUID;
        this.color_class = color_class;
        this.icon_class = icon_class;
        this.headline = headline;
        this.text = text;
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.addElement(this);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getColor_class() {
        return color_class;
    }

    public void setColor_class(String color_class) {
        this.color_class = color_class;
    }

    public String getIcon_class() {
        return icon_class;
    }

    public void setIcon_class(String icon_class) {
        this.icon_class = icon_class;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getProtocol_uuid() {
        return protocol_uuid;
    }

    public void setProtocol_uuid(UUID protocol_uuid) {
        this.protocol_uuid = protocol_uuid;
    }


}
