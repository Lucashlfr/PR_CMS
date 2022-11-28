package de.messdiener.cms.app.services.mail;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.services.mail.utils.EmailUtils;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.Cache;
import org.apache.commons.mail.MultiPartEmail;

import java.io.File;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger LOGGER = Logger.getLogger("Manager.TicketService");
    private final DatabaseService databaseService = Cache.getDatabaseService();

    public void sendMail(EmailEntity entity) throws Exception {
        EmailUtils.sendEmail(entity);
    }

}
