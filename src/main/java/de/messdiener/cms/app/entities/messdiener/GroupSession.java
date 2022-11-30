package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Pair;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupSession {

    @Getter
    private final UUID id;

    @Getter
    private final long dateTime;

    @Getter
    private final UUID group;

    @Getter @Setter
    private List<Messdiener> messdieners;

    public GroupSession(UUID id, long dateTime, UUID group) {
        this.id = id;
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
            if(messdieners.stream().anyMatch(m -> m.getId().equals(messdiener.getId())))continue;

            stringBuilder.append(messdiener.getName()).append(", ");
        }

        return stringBuilder.toString();
    }

}
