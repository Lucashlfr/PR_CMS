package de.messdiener.cms.app.services.mail.utils;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.cache.Cache;

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
    private final static Session session = createSession();

    private EmailUtils(){
        LOGGER.setLevel(Level.ALL);
    }

    public static Session createSession(){
        //return createStratoSession();
       return createGMAILSession();
    }

    private static Session createGMAILSession(){
        Properties properties = new Properties();

        properties.put("mail.smtp.auth",  "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Cache.MAIL_ACCOUNT, Cache.MAIL_PASSWORD);
            }
        });
        LOGGER.finest("Session wurde erfolgreich erstellt!");
        return session;
    }

    private static Session createStratoSession(){
        Properties properties = new Properties();

        properties.put("mail.smtp.auth",  "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.strato.de");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.port", "465");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(" system@cms.code.system.etu.messdiener.com", Cache.MAIL_PASSWORD);
            }
        });
        LOGGER.finest("Session wurde erfolgreich erstellt!");
        return session;
    }

    private static Message prepareMessage(EmailEntity entity) throws Exception{

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(Cache.MAIL_ACCOUNT, "CMS Pfarrei Bellheim"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(entity.getReciver()));
        message.setRecipient(Message.RecipientType.CC, new InternetAddress("lucas.helfer@messdiener-knittelsheim.de"));
        message.setSubject(entity.getTopic());

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
        message.setContent(multipart, "text/html; charset=ISO-8859-1");

        return message;
    }

    public static void sendEmail(EmailEntity entity){
        try {
            LOGGER.info("E-Mail wird versand!");
            Message message = prepareMessage(entity);
            Transport.send(message);
            LOGGER.info("E-Mail erfolgreich versendet!");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
