package de.messdiener.cms.app.entities.user;

import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.entities.ticket.cache.TicketSummery;
import de.messdiener.cms.app.services.CloudService;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
@Data
public class User {

    private UUID userID;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private UserGroup group;
    private String email;
    private List<Permission> permissions;
    private String imgPath;
    private Optional<UUID> personUUID;


    public static User of(UUID uuid, String username, String firstname, String lastname, String password, UserGroup group, String email,
                          List<Permission> permissions, String imgPath, Optional<UUID> personUUID) {
        return new User(uuid, username, firstname, lastname, password, group, email, permissions, imgPath, personUUID);
    }

    public static User empty() {
        return of(UUID.randomUUID(), "", "", "", "", UserGroup.USER, "", new ArrayList<>(), "DEFAULT", Optional.empty());
    }

    public String[] getGroups() {
        UserGroup[] groups = UserGroup.values();
        String[] strings = new String[groups.length];

        for (int i = 0; i < groups.length; i++) {
            strings[i] = groups[i].toString();
        }
        return strings;
    }

    public String getAdminString() {
        return userID + "/" + username + "/" + password + "/" + email + "/" + group.toString() + "/" + firstname + "/" + lastname;
    }

    public String getNameString() {
        return firstname + " " + lastname + " (" + username + ")";
    }

    public String getName() {
        return firstname + " " + lastname;
    }

    public boolean isAdmin() {
        return userHasPermission("admin");
    }

    public boolean isEnabled() {
        return !(group == UserGroup.DEAKTIVIERT || group == UserGroup.REQUESTED);
    }

    public List<Permission> getPermissions() {

        permissions.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));

        return permissions;
    }

    public String getPermissionString() {
        return Permission.generatePermString(permissions);
    }

    public List<Permission> getOtherPermission() throws SQLException {


        List<Permission> perms = new ArrayList<>();

        for (Permission perm : Cache.USER_SERVICE.getPermissions()) {
            if (permissions.stream().anyMatch(p -> p.getName().equals(perm.getName()))) continue;
            perms.add(perm);
        }
        return perms;

    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void save() throws SQLException {
        Cache.USER_SERVICE.saveUser(this);
    }

    public void removePermission(String permName) {
        for (int i = 0; i < permissions.size(); i++) {
            if (permissions.get(i).getName().equals(permName)) {
                permissions.remove(i);
            }
        }
    }

    public boolean userHasPermission(String permission) {
        for (Permission perm : permissions) {
            if (perm.getName().equals("*") || perm.getName().equalsIgnoreCase(permission))
                return true;
        }
        return false;
    }

    public TicketSummery getSummeray() throws SQLException {
        return new TicketSummery(userID);
    }

    public String getSortName() {
        return lastname + " " + firstname + " (" + username + ")";
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgAdress() {
        if (getImgPath().equals("DEFAULT")) {
            return "/dist/assets/img/illustrations/profiles/profile-1.png";
        } else if (getImgPath().startsWith("/")) {
            return getImgPath();
        } else {
            return CloudService.getPath(UUID.fromString(getImgPath()));
        }
    }

    public Optional<Messdiener> getPerson() throws SQLException {
        if (personUUID.isPresent())
            return Cache.MESSDIENER_SERVICE.find(personUUID.get());
        return Optional.empty();
    }

    public String personUUID(){
        return personUUID.isPresent() ? personUUID.get().toString() : "";
    }
}
