package de.messdiener.cms.app.entities.messdiener;

import com.sun.source.tree.LambdaExpressionTree;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupSession {

    private final UUID uuid;
    private final long dateTime;
    private final UUID group;

    private List<Messdiener> messdieners;

    public GroupSession(UUID uuid, long dateTime, UUID group) {
        this.uuid = uuid;
        this.dateTime = dateTime;
        this.group = group;
        this.messdieners = new ArrayList<>();
    }

    public static GroupSession of(UUID uuid, long dateTime, UUID group) {
        return new GroupSession(uuid, dateTime, group);
    }

    public String getExportDateTime(){
        return DateUtils.convertLongToDate(dateTime, DateUtils.DateType.ENGLISH_DATETIME);
    }

    public List<Pair<Messdiener, Boolean>> getAttendances() throws SQLException {
        List<Pair<Messdiener, Boolean>> pairList = new ArrayList<>();
        for(Messdiener messdiener : Cache.MESSDIENER_SERVICE.getPersons()){
            pairList.add(new Pair<>(messdiener, messdieners.contains(messdiener)));
        }
        return pairList;
    }

    public String getDate(){
        return DateUtils.convertLongToDate(dateTime, DateUtils.DateType.ENGLISH);
    }

    public String getGerman(){
        return DateUtils.convertLongToDate(dateTime, DateUtils.DateType.GERMAN_WITH_TIME);
    }

    public void save() throws SQLException {
        Cache.GROUP_SERVICE.saveGroupSession(this);
    }

    public String createList(){
        StringBuilder stringBuilder = new StringBuilder();
        messdieners.forEach(messdiener -> stringBuilder.append(messdiener.getName()).append(", "));

        return stringBuilder.toString();
    }

    public String createListMissing() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();

        for(Messdiener messdiener : Cache.MESSDIENER_SERVICE.getPersons()){
            if(messdieners.stream().anyMatch(m -> m.getUUID().equals(messdiener.getUUID())))continue;

            stringBuilder.append(messdiener.getName()).append(", ");
        }

        return stringBuilder.toString();
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getDateTime() {
        return dateTime;
    }

    public UUID getGroup() {
        return group;
    }

    public void setMessdieners(List<Messdiener> messdieners) {
        this.messdieners = messdieners;
    }

    public List<Messdiener> getMessdieners() {
        return messdieners;
    }
}
