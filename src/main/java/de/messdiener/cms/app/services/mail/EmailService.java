package de.messdiener.cms.app.services.mail;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.services.mail.utils.EmailUtils;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;

import java.util.logging.Logger;

public class EmailService {

    private static final Logger LOGGER = Logger.getLogger("Manager.TicketService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public EmailService(){

    }

    public void sendMail(EmailEntity entity){
        EmailUtils.sendEmail(entity);
    }

}
