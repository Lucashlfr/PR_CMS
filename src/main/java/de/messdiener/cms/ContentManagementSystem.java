package de.messdiener.cms;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;
import java.util.UUID;

@SpringBootApplication
public class ContentManagementSystem {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(ContentManagementSystem.class, args);
        enableMail(false);

    }

    private static void enableMail(boolean b) throws Exception {
        if (b)
            EmailEntity.generateNew("lucas.helfer@gmx.net", "Service wurde gestartet",
                    MailOverlay.generate()
                            .addGreeting("Hallo zusammen, ")
                            .addText("Der Service wurde gestartet").addAdoption_Lucas()).send();
    }
}
