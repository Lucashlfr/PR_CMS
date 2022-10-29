package de.messdiener.cms.web.controller;

import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;

@Controller
public class SettingsController {

    @GetMapping("/settings")
    public String getSettings(Model model) throws SQLException {

        model.addAttribute("user", SecurityHelper.getUser());

        return "userSettings";
    }

}
