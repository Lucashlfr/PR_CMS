package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.app.entities.finance.FinanceIndex;
import de.messdiener.cms.app.services.CloudService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class MessdienerGroup {

    private UUID id;
    private String name;
    private String logoSvg;
    private List<Pair<Messdiener, MessdienerRank>> messdienerList;

    public static MessdienerGroup of(UUID uuid, String name, String logoSvg, List<Pair<Messdiener, MessdienerRank>> messdienerList) {
        return new MessdienerGroup(uuid, name, logoSvg, messdienerList);
    }

    public static MessdienerGroup empty(){
        return of(UUID.randomUUID(),  "Nicht zugeordnet","KEINE", new ArrayList<>());
    }

    public void save() throws SQLException {
        Cache.GROUP_SERVICE.saveGroup(this);
    }

    public List<Messdiener> inGroup() {
        List<Messdiener> list = new ArrayList<>();
        getMessdienerList().forEach(p -> list.add(p.getFirst()));

        return list;
    }

    public List<Messdiener> defaultMessdiener() {
        List<Messdiener> list = new ArrayList<>();

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

    public List<Messdiener> oberMessdiener() {
        List<Messdiener> list = new ArrayList<>();

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

    public List<Messdiener> ltMessdiener() {
        List<Messdiener> list = new ArrayList<>();

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
            if(!Cache.GROUP_SERVICE.personIsInGroup(messdiener.getId()))
                list.add(messdiener);
        }
        return list;
    }

    public String getImgAdress() {
        if (logoSvg.isEmpty() || logoSvg.equals("KEINE")) {
            return "";
        } else {
            return CloudService.getPath(UUID.fromString(logoSvg));
        }
    }

    public FinanceIndex getFinanceIndex() throws SQLException {
        return new FinanceIndex(id);
    }
}
