package de.messdiener.cms.app.entities.ticket.cache;

import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.Utils;

import java.util.UUID;

public class TicketPerson {

    private UUID ticketPerson_UUID;
    private String lastname;
    private String firstname;
    private String association;
    private String phone;
    private String email;

    public TicketPerson(UUID ticketPerson_UUID, String lastname, String firstname, String association, String phone, String email) {
        this.ticketPerson_UUID = ticketPerson_UUID;
        this.lastname = lastname;
        this.firstname = firstname;
        this.association = association;
        this.phone = phone;
        this.email = email;
    }

    public static TicketPerson empty(){
        return new TicketPerson(UUID.randomUUID(), "X", "X", "X", "X", Cache.MAIL);
    }

    public String getName(){
        return getLastname() + ", " + getFirstname();
    }

    public UUID getTicketPerson_UUID() {
        return ticketPerson_UUID;
    }

    public void setTicketPerson_UUID(UUID ticketPerson_UUID) {
        this.ticketPerson_UUID = ticketPerson_UUID;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
