package de.messdiener.cms.app.entities.user;

import java.util.UUID;

public class RegisterRequestEntity {

    private UUID uuid;
    private UUID requestCode;

    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;

    private long requestDate;

    public RegisterRequestEntity(UUID uuid, UUID requestCode, String username, String firstname, String lastname, String password, String email, long requestDate) {
        this.uuid = uuid;
        this.requestCode = requestCode;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.requestDate = requestDate;
    }

    public static RegisterRequestEntity of(UUID uuid, UUID requestCode, String username, String firstname, String lastname, String password, String email, long requestDate){
        return new RegisterRequestEntity(uuid, requestCode, username, firstname,lastname, password, email, requestDate);
    }

    public static RegisterRequestEntity create(String firstname, String lastname, String email){
        return new RegisterRequestEntity(UUID.randomUUID(), UUID.randomUUID(), firstname+"."+lastname, firstname, lastname, "", email, System.currentTimeMillis());
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(UUID requestCode) {
        this.requestCode = requestCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(long requestDate) {
        this.requestDate = requestDate;
    }

}
