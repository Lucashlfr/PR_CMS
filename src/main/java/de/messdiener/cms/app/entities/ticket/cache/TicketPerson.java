package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TicketPerson {

    private UUID id;
    private String lastname;
    private String firstname;
    private String association;
    private String phone;
    private String email;

    public static TicketPerson empty(){
        return new TicketPerson(UUID.randomUUID(), "X", "X", "X", "X", Cache.MAIL);
    }

    public String getName(){
        return getLastname() + ", " + getFirstname();
    }

    public String getGreeting(){
        return "Hallo " + firstname + " " + lastname + ", ";
    }

    public String getClose(){
        return Utils.getMailClose();
    }

    public String getSearchString(){
        return lastname + "/" + firstname + "/" + association + "/" + email + "/" + phone;
    }
}
