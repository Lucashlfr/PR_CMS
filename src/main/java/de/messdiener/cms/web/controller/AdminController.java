package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.cache.enums.UserGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.UUID;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String index(Model model, @RequestParam("state")String state) throws SQLException {

        model.addAttribute("users", Cache.USER_SERVICE.getUsers());
        model.addAttribute("groups", UserGroup.values());

        if(state.equals("ok")){
            addAlertToModel(model, "alert-success", "Nutzer erfolgreich gespeichert");
        }

        return "admin";
    }


    @PostMapping("/admin/saveUser")
    public RedirectView save(@RequestParam("t_uuid") String uuid, @RequestParam("t_username")String username,
                             @RequestParam("t_password")String password, @RequestParam("t_email")String email,
                             @RequestParam("t_group")String group) throws SQLException {



        User user = uuid.equals("new UUID") ? User.empty() : Cache.USER_SERVICE.getUser(UUID.fromString(uuid)).orElse(User.empty());
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setGroup(UserGroup.valueOf(group));

        if(uuid.equals("new UUID")){

            EmailEntity.generateNew(email, "[CMS] Neuer Account angelegt",
                    "Hallo " + username +  ", \nfür dich wurde ein neuer Account angelegt: \n\nBenutzername: " + username + "\nPasswort: " + password
                            + " \n\nViele Grüße \nDas CMS Team \n\nDiese Mail wurde automatisch generiert", Cache.LINK)
                    .send();
        }

        Cache.USER_SERVICE.saveUser(user);

        return new RedirectView("/admin?state=ok");
    }

    public void addAlertToModel(Model model, String alertClass, String alertText){
        model.addAttribute("showAlert", true);
        model.addAttribute("alertClass", "alert " + alertClass +  " alert-dismissible");
        model.addAttribute("alertText", alertText);
    }
}
