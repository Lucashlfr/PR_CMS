package de.messdiener.cms.app.entities.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class RegisterRequestEntity {

    private UUID id;
    private UUID requestCode;

    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;

    private long requestDate;

    public static RegisterRequestEntity of(UUID uuid, UUID requestCode, String username, String firstname, String lastname, String password, String email, long requestDate){
        return new RegisterRequestEntity(uuid, requestCode, username, firstname,lastname, password, email, requestDate);
    }

    public static RegisterRequestEntity create(String firstname, String lastname, String email){
        return new RegisterRequestEntity(UUID.randomUUID(), UUID.randomUUID(), firstname+"."+lastname, firstname, lastname, "", email, System.currentTimeMillis());
    }
}
