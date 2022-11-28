package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.app.entities.finance.FinanceIndex;
import de.messdiener.cms.app.services.CloudService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessdienerGroup {

    private UUID uuid;
    private String name;
    private String logo_svg;

    private List<Pair<Messdiener, MessdienerRank>> messdienerList;

    public MessdienerGroup(UUID uuid, String name, String logo_svg, List<Pair<Messdiener, MessdienerRank>> messdienerList) {
        this.uuid = uuid;
        this.logo_svg = logo_svg;
        this.name = name;
        this.messdienerList = messdienerList;
    }

    public static MessdienerGroup of(UUID uuid, String name, String logo_svg, List<Pair<Messdiener, MessdienerRank>> messdienerList) {
        return new MessdienerGroup(uuid, name, logo_svg, messdienerList);
    }

    public static MessdienerGroup empty(){
        return of(UUID.randomUUID(),  "Nicht zugeordnet","KEINE", new ArrayList<>());
    }

    public void save() throws SQLException {
        Cache.GROUP_SERVICE.saveGroup(this);
    }

    public ArrayList<Messdiener> inGroup() {
        ArrayList<Messdiener> list = new ArrayList<>();
        getMessdienerList().forEach(p -> list.add(p.getFirst()));

        return list;
    }

    public ArrayList<Messdiener> defaultMessdiener() {
        ArrayList<Messdiener> list = new ArrayList<>();

        getMessdienerList()
                .stream()
                .filter(messdienerMessdienerRankPair -> messdienerMessdienerRankPair.getSecond().getPower() == 0)
                .forEach(m -> list.add(m.getFirst()));

        return list;
    }

    public String listDefaultMessdiener(){
        StringBuilder stringBuilder = new StringBuilder();
        defaultMessdiener().forEach(m -> stringBuilder.append(m.getFirstname()).append(" ").append(m.getLastname()).append(", "));

        return stringBuilder.toString();
    }

    public ArrayList<Messdiener> oberMessdiener() {
        ArrayList<Messdiener> list = new ArrayList<>();

        getMessdienerList()
                .stream()
                .filter(messdienerMessdienerRankPair -> messdienerMessdienerRankPair.getSecond().getPower() == 10)
                .forEach(m -> list.add(m.getFirst()));

        return list;
    }

    public String listOberMessdiener(){
        StringBuilder stringBuilder = new StringBuilder();
        oberMessdiener().forEach(m -> stringBuilder.append(m.getFirstname()).append(" ").append(m.getLastname()).append(", "));

        return stringBuilder.toString();
    }

    public ArrayList<Messdiener> ltMessdiener() {
        ArrayList<Messdiener> list = new ArrayList<>();

        getMessdienerList()
                .stream()
                .filter(messdienerMessdienerRankPair -> messdienerMessdienerRankPair.getSecond().getPower() == 5)
                .forEach(m -> list.add(m.getFirst()));

        return list;
    }

    public String listLTMessdiener(){
        StringBuilder stringBuilder = new StringBuilder();
        ltMessdiener().forEach(m -> stringBuilder.append(m.getFirstname()).append(" ").append(m.getLastname()).append(", "));

        return stringBuilder.toString();
    }


    public List<Messdiener> getAvailable() throws SQLException {
        ArrayList<Messdiener> list = new ArrayList<>();

        for (Messdiener messdiener : Cache.MESSDIENER_SERVICE.getPersons()){
            if(!Cache.GROUP_SERVICE.personIsInGroup(messdiener.getUUID()))
                list.add(messdiener);
        }
        return list;
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

    public List<Pair<Messdiener, MessdienerRank>> getMessdienerList() {
        return messdienerList;
    }

    public void setMessdienerList(List<Pair<Messdiener, MessdienerRank>> messdienerList) {
        this.messdienerList = messdienerList;
    }

    public String getLogo_svg() {
        return logo_svg;
    }

    public void setLogo_svg(String logo_svg) {
        this.logo_svg = logo_svg;
    }

    public String getImgAdress() throws IllegalAccessException {
        if (logo_svg.isEmpty() || logo_svg.equals("KEINE")) {
            return "";
        } else {
            return CloudService.getPath(UUID.fromString(logo_svg));
        }
        //throw new IllegalAccessException("");
    }

    public FinanceIndex getFinanceIndex() throws SQLException {
        return new FinanceIndex(uuid);
    }
}
