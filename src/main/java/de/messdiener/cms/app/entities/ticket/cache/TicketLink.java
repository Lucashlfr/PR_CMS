package de.messdiener.cms.app.entities.ticket.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TicketLink {

    private UUID id;
    private long date;
    private UUID creatorUUID;
    private String name;
    private String link;
}
