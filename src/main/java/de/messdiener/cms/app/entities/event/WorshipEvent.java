package de.messdiener.cms.app.entities.event;


import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.EventType;
import de.messdiener.cms.cache.enums.OGroup;
import de.messdiener.cms.web.utils.DateUtils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorshipEvent {

    private UUID uuid;
    private long date;
    private OGroup oGroup;
    private EventType eventType;
    private Set<Attendance> attendances;

    public WorshipEvent(UUID uuid, long date, OGroup oGroup, EventType eventType, Set<Attendance> attendances) {
        this.uuid = uuid;
        this.date = date;
        this.oGroup = oGroup;
        this.eventType = eventType;
        this.attendances = attendances;
    }

    public static WorshipEvent of(UUID uuid, long date, OGroup oGroup, EventType eventType, Set<Attendance> attendances){
        return new WorshipEvent(uuid, date, oGroup, eventType, attendances);
    }

    public static WorshipEvent create(long date){
        return of(UUID.randomUUID(), date, OGroup.DEFAULT, EventType.DEFAULT, new HashSet<>());
    }

    public static WorshipEvent empty(){
        return of(UUID.randomUUID(), 0,OGroup.DEFAULT, EventType.DEFAULT, new HashSet<>());
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getDate() {
        return date;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Set<Messdiener> getDutyPersons() throws SQLException {
        return Cache.WORSHIP_SERVICE.getPersonByDuty(this);
    }

    public Set<Attendance> getAttendances() {
        return attendances;
    }

    public String getEnglishDate(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.ENGLISH);
    }

    public int getDutyCounter() throws SQLException {
        return getDutyPersons().size();
    }

    public String getGermanDate(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

    public Set<Messdiener> getAvailablePersons() throws SQLException {
        return Cache.WORSHIP_SERVICE.getAvailablePersons(this);
    }

    public String getPersons() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Messdiener availableMessdiener : getDutyPersons()) {
            stringBuilder.append(availableMessdiener.getFirstname()).append(" ").append(availableMessdiener.getLastname()).append(", ");
        }
        stringBuilder.append("#");

        return stringBuilder.toString().equals("#") ? "Keine Messdiener eingeteilt" : stringBuilder.toString().replace(", #", "");
    }

    public String getDayDate(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN_WITH_DAY_NAME);
    }

    public String getAvailablePersonsString() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Messdiener availableMessdiener : getAvailablePersons()) {

            if(getDutyPersons().stream().anyMatch(p -> p.getUUID().equals(availableMessdiener.getUUID())))continue;

            stringBuilder.append(availableMessdiener.getFirstname()).append(" ").append(availableMessdiener.getLastname()).append(", ");
        }
        stringBuilder.append("#");

        return stringBuilder.toString().equals("#") ? "Keine Messdiener verfügbar" : stringBuilder.toString().replace(", #", "");
    }

    public OGroup getoGroup() {
        return oGroup;
    }

    public void save() throws SQLException {
        Cache.WORSHIP_SERVICE.saveEvent(this);
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setoGroup(OGroup oGroup) {
        this.oGroup = oGroup;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
