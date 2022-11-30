package de.messdiener.cms.app.entities.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AdminToggleEntity {

    public enum Type {
        BOOLEAN, STRING, NUMBER, NULL
    }

    private UUID id;
    private String name;
    private String value;
    private Type type;

    public static AdminToggleEntity of(UUID uuid, String name, String value, Type type) {
        return new AdminToggleEntity(uuid, name, value, type);
    }

    public static AdminToggleEntity create(String name, String value, Type type) {
        return new AdminToggleEntity(UUID.randomUUID(), name, value, type);
    }

    public static AdminToggleEntity empty() {
        return new AdminToggleEntity(UUID.randomUUID(), "", "", Type.NULL);
    }

    public boolean getValueB() {
        return type == Type.BOOLEAN && Boolean.parseBoolean(value);
    }
}
