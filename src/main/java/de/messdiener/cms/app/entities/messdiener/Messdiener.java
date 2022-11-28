package de.messdiener.cms.app.entities.messdiener;

import de.messdiener.cms.app.entities.event.WorshipEvent;
import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.services.CloudService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Messdiener {

    private UUID uuid;
    private String firstname;
    private String lastname;
    private String adress;
    private long birthday;
    private String phone;
    private String mobile_child;
    private String mobile_parents;
    private String mail_child;
    private String mail_parents;
    private String notes;

    public Messdiener(UUID uuid, String firstname, String lastname, String adress, long birthday, String phone, String mobile_child, String mobile_parents, String mail_child, String mail_parents, String notes) {
        this.uuid = uuid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.adress = adress;
        this.birthday = birthday;
        this.phone = phone;
        this.mobile_child = mobile_child;
        this.mobile_parents = mobile_parents;
        this.mail_child = mail_child;
        this.mail_parents = mail_parents;
        this.notes = notes;
    }

    public static Messdiener of(UUID uuid, String firstname, String lastname, String adress, long birthday, String phone, String mobile_child, String mobile_parents, String mail_child, String mail_parents, String notes) {
        return new Messdiener(uuid, firstname, lastname, adress, birthday, phone, mobile_child, mobile_parents, mail_child, mail_parents, notes);
    }

    public static Messdiener empty() {
        return Messdiener.of(UUID.randomUUID(), "", "", "", -1, "", "", "", "", "", "");
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile_child() {
        return mobile_child;
    }

    public void setMobile_child(String mobile_child) {
        this.mobile_child = mobile_child;
    }

    public String getMobile_parents() {
        return mobile_parents;
    }

    public void setMobile_parents(String mobile_parents) {
        this.mobile_parents = mobile_parents;
    }

    public String getMail_child() {
        return mail_child;
    }

    public void setMail_child(String mail_child) {
        this.mail_child = mail_child;
    }

    public String getMail_parents() {
        return mail_parents;
    }

    public void setMail_parents(String mail_parents) {
        this.mail_parents = mail_parents;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
            if (pair.getSecond()) i++;
        }
        return i;
    }

    public int getAge() {
        return DateUtils.getAge(birthday);
    }

    public int getServices() throws SQLException {
        return getDuties().size();
    }

    public ArrayList<Pair<WorshipEvent, Boolean>> getAvailability() throws SQLException {
        return Cache.WORSHIP_SERVICE.getAvailabilityByPerson(this);
    }

    public Set<WorshipEvent> getDuties() throws SQLException {
        return Cache.WORSHIP_SERVICE.getEventByDuty(this);
    }

    public void save() throws SQLException {
        Cache.MESSDIENER_SERVICE.save(this);
    }

    public MessdienerGroup getGroup() throws SQLException {
        return Cache.GROUP_SERVICE.getGroupByPerson(uuid).orElse(MessdienerGroup.empty());
    }

    public MessdienerRank getRank() throws SQLException {
        return Cache.GROUP_SERVICE.getRankByPerson(uuid).orElse(MessdienerRank.empty());
    }

    public String getName() {
        return firstname + " " + lastname;
    }

    public ArrayList<FileEntity> getFiles() throws SQLException {
        ArrayList<FileEntity> files = new ArrayList<>();
        for (FileEntity fileEntity : Cache.CLOUD_SERVICE.getFiles()) {
            if (fileEntity.getName().startsWith("p_" + uuid + "_"))
                files.add(fileEntity);
        }
        return files;
    }

    public void addFile(FileEntity file) throws SQLException {
        file.setOwner(Cache.SYSTEM_USER.getUser_UUID().toString());
        file.setName("p_" + uuid + "_" + file.getName());

        file.save();
    }


    public void setImg(FileEntity file) throws SQLException {
        for (FileEntity fileEntity : getFiles()) {
            if (fileEntity.getName().contains("profile_img")) {
                Cache.CLOUD_SERVICE.delete(fileEntity.getUUID());
            }
        }

        file.setOwner(Cache.SYSTEM_USER.getUser_UUID().toString());
        file.setName("p_" + uuid + "_profile_img");


        file.save();

    }

    public String getPath() throws SQLException {

        for (FileEntity fileEntity : getFiles()) {
            if (fileEntity.getName().contains("profile_img")) {
                return CloudService.getPath(UUID.fromString(fileEntity.getUUID().toString()));
            }
        }

        return "/dist/assets/img/illustrations/profiles/profile-1.png";
    }

}

