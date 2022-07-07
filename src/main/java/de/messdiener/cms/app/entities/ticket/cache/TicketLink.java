package de.messdiener.cms.app.entities.ticket.cache;

import java.util.UUID;

public class TicketLink {

    private UUID link_uuid;
    private long date;
    private UUID creator_uuid;
    private String name;
    private String link;

    public TicketLink(UUID link_uuid, long date, UUID creator_uuid, String name, String link) {
        this.link_uuid = link_uuid;
        this.date = date;
        this.creator_uuid = creator_uuid;
        this.name = name;
        this.link = link;
    }

    public UUID getLink_UUID() {
        return link_uuid;
    }

    public void setLink_uuid(UUID link_uuid) {
        this.link_uuid = link_uuid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public UUID getCreator_UUID() {
        return creator_uuid;
    }

    public void setCreator_uuid(UUID creator_uuid) {
        this.creator_uuid = creator_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
