package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class SettingsController {

    @GetMapping("/settings")
    public String getSettings(HttpSession httpSession, Model model) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);

        model.addAttribute("user", SecurityHelper.getUser());

        return "user/user";
    }

    @PostMapping("/settings/save/user")
    public RedirectView save(HttpSession httpSession, @RequestParam("username")String username, @RequestParam("firstname") String firstname,
                             @RequestParam("lastname") String lastname, @RequestParam("email") String email) throws SQLException {


        User user = SecurityHelper.getUser();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);

        user.save();

        httpSession.setAttribute("sessionUser", user);

        return new RedirectView("/settings");
    }

    @PostMapping("/settings/save/img")
    public RedirectView saveImg(HttpSession httpSession, @RequestParam("file") MultipartFile file) throws SQLException, IOException {

        FileEntity fileEntity = FileEntity.convert(file, SecurityHelper.getUser().getUserID().toString());
        fileEntity.setName("Profilbild_" + SecurityHelper.getUsername());
        fileEntity.save();

        User user = SecurityHelper.getUser();
        user.setImgPath(fileEntity.getId().toString());

        user.save();

        httpSession.setAttribute("sessionUser", user);

        return new RedirectView("/settings");
    }

}
