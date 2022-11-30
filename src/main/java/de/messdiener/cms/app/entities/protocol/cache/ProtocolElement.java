package de.messdiener.cms.app.entities.protocol.cache;

import de.messdiener.cms.cache.Cache;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.UUID;

@AllArgsConstructor
@Data
public class ProtocolElement {

    private UUID id;
    private UUID protocolUUID;
    private String colorClass;
    private String iconClass;
    private String headline;
    private String text;

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.addElement(this);
    }

}
