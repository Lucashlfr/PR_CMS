package de.messdiener.cms;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.cache.enums.UserGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;
import java.util.UUID;

@SpringBootApplication
public class ContentManagementSystem {


    public static void main(String[] args) throws SQLException {

        Cache.USER_SERVICE.saveUser(new User("user", "user", UserGroup.USER, Cache.MAIL));
        Cache.USER_SERVICE.saveUser(new User("xcg5415", "xcg5415", UserGroup.ADMIN, Cache.MAIL));

        SpringApplication.run(ContentManagementSystem.class, args);

        EmailEntity.generateNew("info@messdiener-knittelsheim.de", "[LOG] Service wurde gestertet! (ID: " + UUID.randomUUID() + ")", "<h1>Service wurde gestertet</h1>",
                 "https://cms.kath-pfarrei-bellheim.de");

    }

}
