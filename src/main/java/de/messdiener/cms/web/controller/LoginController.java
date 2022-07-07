package de.messdiener.cms.web.controller;

import de.messdiener.cms.cache.Cache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String getDefault(){

        if(System.currentTimeMillis() > Cache.REFLESH){
            Cache.getDatabaseService().reconnect();
        }

        return "security/login";
    }

    // Login form with error
    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "security/login";
    }

}
