package de.messdiener.cms.app.entities.admin;

import de.messdiener.cms.app.services.AdminService;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.UUID;

public class AdminToggleEntity {

    public enum Type{
        BOOLEAN, STRING, NUMBER, NULL
    }

    private UUID uuid;
    private String name;
    private String value;
    private Type type;

    private AdminToggleEntity(UUID uuid, String name, String value, Type type) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public static AdminToggleEntity of(UUID uuid, String name, String value, Type type){
        return new AdminToggleEntity(uuid, name, value, type);
    }

    public static AdminToggleEntity create(String name, String value, Type type){
        return new AdminToggleEntity(UUID.randomUUID(), name, value, type);
    }

    public static AdminToggleEntity empty(){
        return new AdminToggleEntity(UUID.randomUUID(), "","", Type.NULL);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean getValueB(){
        return type == Type.BOOLEAN && Boolean.parseBoolean(value);
    }
}
