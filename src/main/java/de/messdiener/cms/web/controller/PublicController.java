package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.user.RegisterRequestEntity;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PublicController {

    @GetMapping("/register")
    public String register(Model model, @RequestParam("step")Optional<String> stepInput, @RequestParam("username") Optional<String> username,
                           @RequestParam("code") Optional<String> code) throws SQLException {

        String step = stepInput.orElse("1");
        model.addAttribute("step", step);

        if(step.equals("3")){
            RegisterRequestEntity registerRequest = check(code.orElseThrow());
            if(!registerRequest.getUsername().equals(username.orElseThrow())){
                throw new IllegalAccessError("USER ISNT EXISTING");
            }

            model.addAttribute("username", username.orElseThrow());
            model.addAttribute("code", code.orElseThrow());
        }

        return "security/register";
    }

    @PostMapping("/register/saveStep1")
    public RedirectView saveStep1(@RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname,
                                  @RequestParam("email") String email) throws Exception {

        RegisterRequestEntity registerRequest = RegisterRequestEntity.create(firstname, lastname, email);

        if (Cache.REGISTER_SERVICE.getRequests().stream().anyMatch(r -> r.getUsername().equals(registerRequest.getUsername()))) {
            throw new IllegalAccessError("USER ALRADY EXISTS");
        }
        Cache.REGISTER_SERVICE.save(registerRequest);

        EmailEntity emailEntity = EmailEntity.generateNew(email, "Wilkommen im CMS!",
                MailOverlay.generate().addGreeting("Hallo, " + firstname + ", ")
                        .addText("Dein Code lautet: "  + registerRequest.getRequestCode().toString()).addLink("Zum Portal", "https://localhost:8081/register/step2").addAdoption_Lucas());
        Cache.EMAIL_SERVICE.sendMail(emailEntity);

        return new RedirectView("/register?step=2");
    }

    @PostMapping("/register/saveStep2/verify")
    public RedirectView verifyStep2(@RequestParam("code") String code) throws SQLException {

        RegisterRequestEntity registerRequest = check(code);
        return new RedirectView("/register?step=3&username=" + registerRequest.getUsername() + "&code=" + code);
    }

    private RegisterRequestEntity check(String code) throws SQLException {
        Optional<RegisterRequestEntity> request = Cache.REGISTER_SERVICE.getRequests().stream().filter(r -> r.getRequestCode().toString().equals(code)).findFirst();

        if (request.isEmpty()) {
            throw new IllegalAccessError("TOKEN ISNT EXISTING");
        }

        return request.get();
    }

    @PostMapping("/register/saveStep3/save")
    public RedirectView save(@RequestParam("username")String username, @RequestParam("password")String password, @RequestParam("code")String code) throws SQLException {

        RegisterRequestEntity registerRequest = check(code);
        if(!registerRequest.getUsername().equals(username)){
            throw new IllegalAccessError("USER ISNT EXISTING");
        }

        User user = User.of(UUID.randomUUID(), registerRequest.getUsername(), registerRequest.getFirstname(), registerRequest.getLastname(),
                password, UserGroup.REQUESTED, registerRequest.getEmail(), new ArrayList<>(), "DEFAULT", Optional.empty());
        Cache.USER_SERVICE.saveUser(user);

        Cache.USER_SERVICE.createUserInSecurity(user);

        return new RedirectView("/");

    }
}
