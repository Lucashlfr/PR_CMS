package de.messdiener.cms.app.entities.user;

import de.messdiener.cms.cache.Cache;
import org.springframework.security.core.parameters.P;

import java.sql.SQLException;
import java.util.ArrayList;

public class Permission {

    private final String name;
    private final String description;

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static ArrayList<Permission> generateByCache(String input){

        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(new Permission(input, input));
        return permissions;
    }

    public static String generatePermString(ArrayList<Permission> input) {

        StringBuilder stringBuilder = new StringBuilder();
        input.forEach(i -> stringBuilder.append(i.getName()).append(";"));
        return stringBuilder.toString();
    }
}
