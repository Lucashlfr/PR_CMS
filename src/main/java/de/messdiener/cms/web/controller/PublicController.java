package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.user.RegisterRequest;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.mail.utils.MailTemplates;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PublicController {

    @GetMapping("/register")
    public String register() {
        return "security/register";
    }

    @PostMapping("/register/saveStep1")
    public RedirectView saveStep1(@RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname,
                                  @RequestParam("email") String email) throws SQLException {

        RegisterRequest registerRequest = RegisterRequest.create(firstname, lastname, email);

        if (Cache.REGISTER_SERVICE.getRequests().stream().anyMatch(r -> r.getUsername().equals(registerRequest.getUsername()))) {
            throw new IllegalAccessError("USER ALRADY EXISTS");
        }
        Cache.REGISTER_SERVICE.save(registerRequest);

        EmailEntity emailEntity = new EmailEntity(email, "Wilkommen im CMS!", MailTemplates.createTemplate_Verification(registerRequest), "https://localhost:8081/register/step2");
        Cache.EMAIL_SERVICE.sendMail(emailEntity);

        return new RedirectView("/register/step2");
    }

    @GetMapping("/register/step2")
    public String registerStep2() {
        return "security/registerStep2";
    }

    @PostMapping("/register/saveStep2/verify")
    public RedirectView verifyStep2(@RequestParam("code") String code) throws SQLException {

        RegisterRequest registerRequest = check(code);
        return new RedirectView("/register/step3?username=" + registerRequest.getUsername() + "&code=" + code);
    }

    @GetMapping("/register/step3")
    public String step3(Model model, @RequestParam("username") String username, @RequestParam("code") String code) throws SQLException {

        RegisterRequest registerRequest = check(code);
        if(!registerRequest.getUsername().equals(username)){
            throw new IllegalAccessError("USER ISNT EXISTING");
        }

        model.addAttribute("username", username);
        model.addAttribute("code", code);

        return "security/registerStep3";
    }

    private RegisterRequest check(String code) throws SQLException {
        Optional<RegisterRequest> request = Cache.REGISTER_SERVICE.getRequests().stream().filter(r -> r.getRequestCode().toString().equals(code)).findFirst();

        if (request.isEmpty()) {
            throw new IllegalAccessError("TOKEN ISNT EXISTING");
        }

        return request.get();
    }

    @PostMapping("/register/saveStep3/save")
    public RedirectView save(@RequestParam("username")String username, @RequestParam("password")String password, @RequestParam("code")String code) throws SQLException {

        RegisterRequest registerRequest = check(code);
        if(!registerRequest.getUsername().equals(username)){
            throw new IllegalAccessError("USER ISNT EXISTING");
        }

        User user = User.of(UUID.randomUUID(), registerRequest.getUsername(), registerRequest.getFirstname(), registerRequest.getLastname(),
                password, UserGroup.REQUESTED, registerRequest.getEmail(), new ArrayList<>());
        Cache.USER_SERVICE.saveUser(user);

        Cache.USER_SERVICE.createUserInSecurity(user);

        return new RedirectView("/");

    }
}
