package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.app.services.GroupService;

import java.util.UUID;

public class MessdienerRank {

    private UUID uuid;
    private String name;
    private int power;

    public MessdienerRank(UUID uuid, String name, int power) {
        this.uuid = uuid;
        this.name = name;
        this.power = power;
    }

    public static MessdienerRank of(UUID uuid, String name, int power) {
        return new MessdienerRank(uuid, name, power);
    }

    public static MessdienerRank empty() {
        return GroupService.DEFAULT;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
