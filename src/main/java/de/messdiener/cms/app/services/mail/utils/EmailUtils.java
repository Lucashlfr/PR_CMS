package de.messdiener.cms.app.services.mail.utils;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.cache.Cache;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailUtils {

    private static final Logger LOGGER = Logger.getLogger("Manager.TicketService");

    private EmailUtils(){
        LOGGER.setLevel(Level.ALL);
    }


    private static MultiPartEmail prepareMessage(EmailEntity entity) throws Exception{

        String mailserver = "smtp.strato.de";
        String username = "system@cms.code.system.etu.messdiener.com";
        String password = "iPiw2A3YV96E0B6QrFKw9Kbt3nINNtX89XNfaEfk";
        String absender = "system@cms.code.system.etu.messdiener.com";
        String textCharset = "UTF-8";


        MultiPartEmail email = new MultiPartEmail();
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setStartTLSEnabled(true);

        email.setFrom(absender, "CMS Portal Pfarrei Bellheim");
        email.setHostName(mailserver);
        email.addTo(entity.getReciver());
        email.setCharset(textCharset);
        email.setSubject(entity.getTopic());
        email.setMsg(entity.getText());

        // Multipart-Message ("Wrapper") erstellen
        Multipart multipart = new MimeMultipart();
        // Body-Part setzen:
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setHeader("Content-Type", "text/html");

        // Textteil des Body-Parts
        messageBodyPart.setContent(MailOverlay.generate(entity.getText(), entity.getLink()), "text/html");
        // Body-Part dem Multipart-Wrapper hinzufügen
        multipart.addBodyPart(messageBodyPart);
        // Message fertigstellen, indem sie mit dem Multipart-Content ausgestattet wird
        email.setContent(multipart, "text/html; charset=ISO-8859-1");

        return email;
    }

    public static void sendEmail(EmailEntity entity){
        try {
            LOGGER.info("E-Mail wird versand!");
            MultiPartEmail message = prepareMessage(entity);
            message.send();
            LOGGER.info("E-Mail erfolgreich versendet!");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
