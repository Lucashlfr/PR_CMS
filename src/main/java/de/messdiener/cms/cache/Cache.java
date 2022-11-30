package de.messdiener.cms.cache;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.*;
import de.messdiener.cms.app.services.mail.EmailService;
import de.messdiener.cms.app.services.sql.DatabaseService;
import de.messdiener.cms.cache.enums.UserGroup;
import de.messdiener.cms.web.security.SecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Cache {

    private Cache(){}

    private static long refresh;
    public static final User SYSTEM_USER = new User(UUID.fromString("9d329917-70db-4742-bd24-d14f9669560e"), "SYSTEM","SYSTEM","", UUID.randomUUID().toString(), UserGroup.ADMIN, Cache.MAIL,new ArrayList<>(), "/img/img_system.png",  Optional.empty());
    public static final User ALFRED_USER = new User(UUID.fromString("fdc9139c-2c1b-40c2-9656-49d4c0a55a6c"), "alfred.gadinger","Alfred","Gadinger", "alfred", UserGroup.LEKTOR2, Cache.MAIL,new ArrayList<>(), "DEFAULT", Optional.empty());

    public static final String PFARRBUERO = "pfarramt.bellheim@bistum-speyer.de";

    public static final String MAIL = "info@messdiener-knittelsheim.de";
    public static final String LINK = "https://cms.code.system.etu.messdiener.com/";

    public static final String MAIL_ACCOUNT = "lucas15.07.2002@gmail.com"; // GMail-Sender
    public static final String MAIL_PASSWORD = "qdlfaljybtinosjv";

    private static DatabaseService DATABASE_SERVICE = new DatabaseService();
    public static final UserService USER_SERVICE = new UserService();
    public static final TicketService TICKET_SERVICE = new TicketService();
    public static final EmailService EMAIL_SERVICE = new EmailService();
    public static final AdminService ADMIN_SERVICE = new AdminService();
    public static final RegisterService REGISTER_SERVICE = new RegisterService();
    public static final CloudService CLOUD_SERVICE = new CloudService();
    public static final WorshipService WORSHIP_SERVICE = new WorshipService();
    public static final MessdienerService MESSDIENER_SERVICE = new MessdienerService();
    public static final GroupService GROUP_SERVICE = new GroupService();
    public static final FinanceService FINANCE_SERVICE = new FinanceService();
    public static final FlowService FLOW_SERVICE = new FlowService();


    public static final SecurityConfiguration SECURITY_CONFIGURATION = SecurityConfiguration.getInstance();
    private static InMemoryUserDetailsManager userDetailsManager;
    private static PasswordEncoder passwordEncoder;

    public static DatabaseService getDatabaseService() {

        if(DATABASE_SERVICE == null) DATABASE_SERVICE = new DatabaseService();
        if(DATABASE_SERVICE.getConnection() == null) DATABASE_SERVICE.reconnect();
        return DATABASE_SERVICE;

    }
    public static void updateRefresh(){
        refresh = System.currentTimeMillis() + 18000000;
    }

    public static void setUserDetailsManager(InMemoryUserDetailsManager userDetailsManager) {
        Cache.userDetailsManager = userDetailsManager;
    }

    public static void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Cache.passwordEncoder = passwordEncoder;
    }

    public static long getRefresh() {
        return refresh;
    }

    public static void setRefresh(long refresh) {
        Cache.refresh = refresh;
    }

    public static InMemoryUserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    public static PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
