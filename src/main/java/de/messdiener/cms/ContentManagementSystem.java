package de.messdiener.cms;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;
import java.util.UUID;

@SpringBootApplication
public class ContentManagementSystem {


    public static void main(String[] args) {
        SpringApplication.run(ContentManagementSystem.class, args);
        enableMail(false);

    }

    private static void enableMail(boolean b) {
        if (b)
            EmailEntity.generateNew("info@messdiener-knittelsheim.de", "[LOG] Service wurde gestertet! (ID: "
                                    + UUID.randomUUID() + ")", MailOverlay.generate("Text,", "LIN MK"),
                            "https://cms.kath-pfarrei-bellheim.de")
                    .send();
    }
}
