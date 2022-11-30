package de.messdiener.cms.app.entities.protocol.cache;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import lombok.Data;

import java.sql.SQLException;
import java.util.UUID;

@Data
public class ProtocolComment {

    private final UUID id;
    private final UUID protocol;
    private final UUID creator;
    private final long date;
    private final String text;

    public User getUser() throws SQLException {
        return Cache.USER_SERVICE.getUser(creator).orElseThrow();
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.addComment(this);
    }

    public String getDateGerman(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

}
