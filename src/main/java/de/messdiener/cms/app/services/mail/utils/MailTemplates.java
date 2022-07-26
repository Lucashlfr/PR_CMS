package de.messdiener.cms.app.services.mail.utils;

import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.app.entities.user.User;

public class MailTemplates {

    private static String createGreeting(User user){
        return "Hallo " + user.getUsername() + ", <br><br>";
    }

    private static String createAdoption(){
        return "Viele Gr&uuml;&szlig;e <br><br><b>i.A. Lucas Helfer</b><br>Content Manager | Pfarei Bellheim<br><br>Pfarrei Hl. Hildegard v. Bingen<br>Hintere Stra&szlig;e 1, 76756 Bellheim<br>Tel. 07272/973050";
    }

    private static String createTicketLink(Ticket ticket){
        return "Ticket Nr. " + ticket.getUUID().toString()
                + " [" + ticket.getTicketPerson().getName()
                + " (" + ticket.getTicketPerson().getAssociation() + "), " + ticket.getDates().getGermanDate_CREATED() + "]";
    }

    public static String createTemplate_newEditorToEditor(User user, Ticket ticket, String by){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(createGreeting(user)).append("dir wurde das ").append(createTicketLink(ticket))
                .append("von ").append(by).append(" zugewiesen.<br> Bitte bearbeite das Ticket bis zum ").append(ticket.getDates().getGermanDateWithDay_DEADLINE())
                .append("<br><br>").append(createAdoption());

        return stringBuilder.toString();
    }

    public static String createTemplate_newEditorToCustomer(){
        return "1";
    }

}
