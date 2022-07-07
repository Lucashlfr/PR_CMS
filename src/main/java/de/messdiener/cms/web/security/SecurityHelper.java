package de.messdiener.cms.web.security;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SecurityHelper {

    private static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUsername(){
        return getAuthentication().getName();
    }

    public static User getUser() throws SQLException {
        return Cache.USER_SERVICE.getUsers().stream().filter(user -> user.getUsername().equals(getAuthentication().getName())).findFirst().orElseThrow();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
