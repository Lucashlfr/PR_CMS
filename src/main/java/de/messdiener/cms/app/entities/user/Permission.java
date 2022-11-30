package de.messdiener.cms.app.entities.user;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Permission {

    private final String name;
    private final String description;

    public static List<Permission> generateByCache(String input){

        List<Permission> permissions = new ArrayList<>();
        permissions.add(new Permission(input, input));
        return permissions;
    }

    public static String generatePermString(List<Permission> input) {

        StringBuilder stringBuilder = new StringBuilder();
        input.forEach(i -> stringBuilder.append(i.getName()).append(";"));
        return stringBuilder.toString();
    }
}
