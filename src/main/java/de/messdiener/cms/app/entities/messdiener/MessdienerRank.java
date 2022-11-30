package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.app.services.GroupService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class MessdienerRank {

    private UUID id;
    private String name;
    private int power;

    public static MessdienerRank of(UUID uuid, String name, int power) {
        return new MessdienerRank(uuid, name, power);
    }

    public static MessdienerRank empty() {
        return GroupService.DEFAULT;
    }
}
