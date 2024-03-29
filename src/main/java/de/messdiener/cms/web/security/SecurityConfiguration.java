package de.messdiener.cms.web.security;

import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = Logger.getLogger("Manager.SecurityConfiguration");

    @Bean
    @Override
    public InMemoryUserDetailsManager userDetailsService() {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        LOGGER.setLevel(Level.ALL);

        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        try {
            Cache.USER_SERVICE.getUsers().forEach(u -> {
                LOGGER.config(u.getUsername() + " erstellt!");
                UserDetails createUser = User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .passwordEncoder(passwordEncoder::encode)
                        .roles(u.getGroup().toString()).build();
                userDetailsManager.createUser(createUser);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cache.setPasswordEncoder(passwordEncoder);
        Cache.setUserDetailsManager(userDetailsManager);
        return userDetailsManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/login")
                .sessionFixation().migrateSession();
        http.
                requiresChannel(channel ->
                channel.anyRequest().requiresSecure())
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/static/**", "/dist/**", "/img/**", "/css/**", "/script/**", "/download").permitAll() // "/index", "/", "/login"
                .and()
                .authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers("/dc/cloud/file").permitAll()
                .antMatchers("/register/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/ticket/**").hasAnyRole(de.messdiener.cms.app.entities.user.User.empty().getGroups())
                .antMatchers("/").hasAnyRole(de.messdiener.cms.app.entities.user.User.empty().getGroups())
                .antMatchers("/admin/**").hasAnyRole(UserGroup.ADMIN.toString())
                .antMatchers("/finance/**").hasAnyRole(de.messdiener.cms.app.entities.user.User.empty().getGroups())
                .anyRequest().authenticated().and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error")
                .loginPage("/login")
                .permitAll()
                .and().logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?success")
                        .addLogoutHandler(new SecurityContextLogoutHandler())).csrf().disable();



    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/static/**"); // #3
    }

    public static SecurityConfiguration getInstance() {
        return new SecurityConfiguration();
    }


}
