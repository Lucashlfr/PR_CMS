package de.messdiener.cms.cache;

import de.messdiener.cms.app.entities.user.Permission;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.AdminService;
import de.messdiener.cms.app.services.RegisterService;
import de.messdiener.cms.app.services.UserService;
import de.messdiener.cms.app.services.TicketService;
import de.messdiener.cms.app.services.mail.EmailService;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.enums.UserGroup;
import de.messdiener.cms.web.security.SecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.UUID;

public class Cache {

    public static long REFLESH;
    public static final User SYSTEM_USER = new User(UUID.fromString("9d329917-70db-4742-bd24-d14f9669560e"), "SYSTEM","SYSTEM","", UUID.randomUUID().toString(), UserGroup.ADMIN, Cache.MAIL);
    public static final User ALFRED_USER = new User(UUID.fromString("fdc9139c-2c1b-40c2-9656-49d4c0a55a6c"), "alfred.gadinger","Alfred","Gadinger", UUID.randomUUID().toString(), UserGroup.LEKTOR2, Cache.MAIL);

    public static final String MAIL = "info@messdiener-knittelsheim.de";
    public static final String LINK = "https://cms.code.system.etu.messdiener.com/";

    public static final String MAIL_ACCOUNT = "lucas15.07.2002@gmail.com"; // GMail-Sender
    public static final String MAIL_PASSWORD = "qdlfaljybtinosjv";

    private static DatabaseService DATABASE_SERVICE = new DatabaseService();
    public final static UserService USER_SERVICE = new UserService();
    public final static TicketService TICKET_SERVICE = new TicketService();
    public final static EmailService EMAIL_SERVICE = new EmailService();
    public final static AdminService ADMIN_SERVICE = new AdminService();
    public final static RegisterService REGISTER_SERVICE = new RegisterService();

    public final static SecurityConfiguration SECURITY_CONFIGURATION = SecurityConfiguration.getInstance();
    public static InMemoryUserDetailsManager userDetailsManager;
    public static PasswordEncoder passwordEncoder;

    public static DatabaseService getDatabaseService() {

        if(DATABASE_SERVICE == null) DATABASE_SERVICE = new DatabaseService();
        if(DATABASE_SERVICE.getConnection() == null) DATABASE_SERVICE.reconnect();
        return DATABASE_SERVICE;

    }
    public static void updateReflesh(){
        //
        REFLESH = System.currentTimeMillis() + 18000000;
    }

    public static void setUserDetailsManager(InMemoryUserDetailsManager userDetailsManager) {
        Cache.userDetailsManager = userDetailsManager;
    }

    public static void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Cache.passwordEncoder = passwordEncoder;
    }

}
