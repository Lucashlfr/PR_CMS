package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.app.entities.event.WorshipEvent;
import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.services.CloudService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Data
public class Messdiener {

    private UUID id;
    private String firstname;
    private String lastname;
    private String adress;
    private long birthday;
    private String phone;
    private String mobilePhoneChild;
    private String mobilePhoneParents;
    private String mailAdressChild;
    private String mailAdressParents;
    private String notes;

    public static Messdiener of(UUID uuid, String firstname, String lastname, String adress, long birthday, String phone, String mobile_child, String mobile_parents, String mail_child, String mail_parents, String notes) {
        return new Messdiener(uuid, firstname, lastname, adress, birthday, phone, mobile_child, mobile_parents, mail_child, mail_parents, notes);
    }

    public static Messdiener empty() {
        return Messdiener.of(UUID.randomUUID(), "", "", "", -1, "", "", "", "", "", "");
    }


    public String getReadName() {
        return lastname + ", " + firstname;
    }

    public String getEnglishDate() {
        return DateUtils.convertLongToDate(birthday, DateUtils.DateType.ENGLISH);
    }

    public String getGermanDate() {
        return DateUtils.convertLongToDate(birthday, DateUtils.DateType.GERMAN);
    }

    public int getAvailableServices() throws SQLException {
        int i = 0;
        for (Pair<WorshipEvent, Boolean> pair : getAvailability()) {
            if (Boolean.TRUE.equals(pair.getSecond())) i++;
        }
        return i;
    }

    public int getAge() {
        return DateUtils.getAge(birthday);
    }

    public int getServices() throws SQLException {
        return getDuties().size();
    }

    public List<Pair<WorshipEvent, Boolean>> getAvailability() throws SQLException {
        return Cache.WORSHIP_SERVICE.getAvailabilityByPerson(this);
    }

    public Set<WorshipEvent> getDuties() throws SQLException {
        return Cache.WORSHIP_SERVICE.getEventByDuty(this);
    }

    public void save() throws SQLException {
        Cache.MESSDIENER_SERVICE.save(this);
    }

    public MessdienerGroup getGroup() throws SQLException {
        return Cache.GROUP_SERVICE.getGroupByPerson(id).orElse(MessdienerGroup.empty());
    }

    public MessdienerRank getRank() throws SQLException {
        return Cache.GROUP_SERVICE.getRankByPerson(id).orElse(MessdienerRank.empty());
    }

    public String getName() {
        return firstname + " " + lastname;
    }

    public ArrayList<FileEntity> getFiles() throws SQLException {
        ArrayList<FileEntity> files = new ArrayList<>();
        for (FileEntity fileEntity : Cache.CLOUD_SERVICE.getFiles()) {
            if (fileEntity.getName().startsWith("p_" + id + "_"))
                files.add(fileEntity);
        }
        return files;
    }

    public void addFile(FileEntity file) throws SQLException {
        file.setOwner(Cache.SYSTEM_USER.getUserID().toString());
        file.setName("p_" + id + "_" + file.getName());

        file.save();
    }


    public void setImg(FileEntity file) throws SQLException {
        for (FileEntity fileEntity : getFiles()) {
            if (fileEntity.getName().contains("profile_img")) {
                Cache.CLOUD_SERVICE.delete(fileEntity.getId());
            }
        }

        file.setOwner(Cache.SYSTEM_USER.getUserID().toString());
        file.setName("p_" + id + "_profile_img");


        file.save();

    }

    public String getPath() throws SQLException {

        for (FileEntity fileEntity : getFiles()) {
            if (fileEntity.getName().contains("profile_img")) {
                return CloudService.getPath(UUID.fromString(fileEntity.getId().toString()));
            }
        }

        return "/dist/assets/img/illustrations/profiles/profile-1.png";
    }

}

