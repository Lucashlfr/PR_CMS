package de.messdiener.cms.app.entities.user;

import de.messdiener.cms.cache.enums.UserGroup;

import java.util.UUID;

public class User {

    private UUID user_uuid;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private UserGroup group;
    private String email;

    public User(UUID user_uuid, String username, String firstname, String lastname, String password, UserGroup group, String email) {
        this.user_uuid = user_uuid;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.group = group;
        this.email = email;
    }

    public User(String username, String firstname, String lastname, String password, UserGroup group, String email) {
        this.user_uuid = UUID.randomUUID();
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.group = group;
        this.email = email;
    }

    public static User of(UUID user_uuid, String username, String firstname, String lastname, String password, UserGroup group, String email) {
        return new User(user_uuid, username, firstname, lastname, password, group, email);
    }

    public static User empty(){
        return of(UUID.randomUUID(), "", "", "", "", UserGroup.USER, "");
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

    public boolean isAdmin(){
        return group == UserGroup.ADMIN;
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
}
