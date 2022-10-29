package de.messdiener.cms.app.entities.user;

import de.messdiener.cms.app.entities.ticket.cache.TicketSummery;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;

import java.sql.SQLException;
import java.util.*;

public class User {

    private UUID user_uuid;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private UserGroup group;
    private String email;
    private ArrayList<Permission> permissions;

    public User(UUID user_uuid, String username, String firstname, String lastname, String password, UserGroup group, String email, ArrayList<Permission> permissions) {
        this.user_uuid = user_uuid;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.group = group;
        this.email = email;
        this.permissions = permissions;
    }

    public User(String username, String firstname, String lastname, String password, UserGroup group, String email) {
        this.user_uuid = UUID.randomUUID();
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.group = group;
        this.email = email;
        this.permissions = new ArrayList<>();
    }

    public User(UUID fromString, String username, String firstname, String lastname, String password, UserGroup userGroup, String mail) {
        this.user_uuid = fromString;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.group = userGroup;
        this.email = mail;
        this.permissions = new ArrayList<>();
    }


    public static User of(UUID user_uuid, String username, String firstname, String lastname, String password, UserGroup group, String email, ArrayList<Permission> permissions) {
        return new User(user_uuid, username, firstname, lastname, password, group, email, permissions);
    }

    public static User empty(){
        return of(UUID.randomUUID(), "", "", "", "", UserGroup.USER, "", new ArrayList<>());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getUser_UUID() {
        return user_uuid;
    }

    public void setUser_uuid(UUID user_uuid) {
        this.user_uuid = user_uuid;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public UserGroup getGroup() {
        return group;
    }

    public String[] getGroups(){
        UserGroup[] groups = UserGroup.values();
        String[] strings = new String[groups.length];

        for (int i = 0; i < groups.length; i++) {
            strings[i] = groups[i].toString();
        }
        return strings;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminString(){
        return user_uuid + "/" + username + "/" + password + "/" + email + "/" + group.toString() + "/" + firstname + "/" + lastname;
    }

    public String getNameString(){
        return firstname + " " + lastname + " (" + username + ")";
    }

    public String getName(){
        return firstname + " " + lastname;
    }

    public boolean isAdmin(){
        return userHasPermission("admin");
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isEnabled(){
        return !(group == UserGroup.DEAKTIVIERT || group == UserGroup.REQUESTED);
    }

    public ArrayList<Permission> getPermissions() {

        permissions.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));

        return permissions;
    }

    public String getPermissionString(){
        return Permission.generatePermString(permissions);
    }

    public ArrayList<Permission> getOtherPermission() throws SQLException {


        ArrayList<Permission> perms = new ArrayList<>();

        for(Permission perm : Cache.USER_SERVICE.getPermissions()){
            if(permissions.stream().anyMatch(p -> p.getName().equals(perm.getName())))continue;
            perms.add(perm);
        }
        return perms;

    }

    public void addPermission(Permission permission){
        permissions.add(permission);
    }

    public void save() throws SQLException {
        Cache.USER_SERVICE.saveUser(this);
    }

    public void removePermission(String permName) {
        for (int i = 0; i < permissions.size(); i++) {
            if(permissions.get(i).getName().equals(permName))
                permissions.remove(i);
        }
    }

    public boolean userHasPermission(String permission){
        for(Permission perm : permissions){
            if(perm.getName().equals("*") || perm.getName().equalsIgnoreCase(permission))
                return true;
        }
        return false;
    }

    public TicketSummery getSummeray() throws SQLException {
        return new TicketSummery(user_uuid);
    }

    public String getSortName(){
        return lastname + " " + firstname + " (" + username + ")";
    }
}
