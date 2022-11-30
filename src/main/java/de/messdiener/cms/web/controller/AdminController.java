package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.user.Permission;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.UserGroup;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.UUID;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String admin(HttpSession httpSession, Model model) throws SQLException {
        SecurityHelper.addSessionUser(httpSession);

        model.addAttribute("users", Cache.USER_SERVICE.getUsers());

        return "admin/adminIndex";
    }

    @GetMapping("/admin/perm")
    public String perm(HttpSession httpSession, Model model) throws SQLException {
        SecurityHelper.addSessionUser(httpSession);

        model.addAttribute("permissions", Cache.USER_SERVICE.getPermissions());

        return "admin/permOverview";
    }

    @GetMapping("/admin/user")
    public String users(HttpSession httpSession, Model model, @RequestParam("user") UUID userUUID) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);

        User user = Cache.USER_SERVICE.getUser(userUUID).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("messdieners", Cache.MESSDIENER_SERVICE.getPersons());

        return "admin/editUser";
    }

    @PostMapping("/admin/saveUser")
    public RedirectView save(@RequestParam("t_uuid") String uuid, @RequestParam("t_username") String username,
                             @RequestParam("t_password") String password, @RequestParam("t_email") String email,
                             @RequestParam("t_group") String group, @RequestParam("t_firstname") String firstname,
                             @RequestParam("t_lastname") String lastname) throws Exception {


        User user = Cache.USER_SERVICE.getUser(UUID.fromString(uuid)).orElse(User.empty());
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setGroup(UserGroup.valueOf(group));
        user.setFirstname(firstname);
        user.setLastname(lastname);

        Cache.USER_SERVICE.saveUser(user);
        Cache.USER_SERVICE.createUserInSecurity(user);

        return new RedirectView("/admin/user?user=" + uuid);
    }

    @PostMapping("/admin/createUser")
    public RedirectView create(@RequestParam("username") String username,
                               @RequestParam("passwort") String password, @RequestParam("email") String email,
                               @RequestParam("firstname") String firstname,
                               @RequestParam("lastname") String lastname) throws Exception {


        User user = User.empty();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setGroup(UserGroup.USER);
        user.setFirstname(firstname);
        user.setLastname(lastname);

        Cache.USER_SERVICE.saveUser(user);
        Cache.USER_SERVICE.createUserInSecurity(user);

        return new RedirectView("/admin/user?user=" + user.getUserID());
    }

    public void addAlertToModel(Model model, String alertClass, String alertText) {
        model.addAttribute("showAlert", true);
        model.addAttribute("alertClass", "alert " + alertClass + " alert-dismissible");
        model.addAttribute("alertText", alertText);
    }

    @PostMapping("/admin/savePerm")
    public RedirectView save(@RequestParam("permName") String permName, @RequestParam("permDesc") String permDesc) throws SQLException {

        Cache.USER_SERVICE.createPermission(new Permission(permName, permDesc));

        return new RedirectView("/admin/perm");
    }


    @GetMapping("/admin/deletePerm")
    public RedirectView delete(@RequestParam("permName") String permName, @RequestParam("permDesc") String permDesc) {

        Cache.USER_SERVICE.deletePermission(new Permission(permName, permDesc));

        return new RedirectView("/admin/perm");
    }

    @GetMapping("/admin/permUser")
    public String permUser(HttpSession httpSession, Model model, @RequestParam("uuid") UUID userUUID) throws SQLException {
        SecurityHelper.addSessionUser(httpSession);

        User user = Cache.USER_SERVICE.getUser(userUUID).orElseThrow();

        model.addAttribute("user", user);

        return "admin/permUser";
    }

    @GetMapping("/admin/permAdd")
    public RedirectView addPerm(@RequestParam("uuid") UUID userUUID, @RequestParam("permName") String permName) throws SQLException {

        User user = Cache.USER_SERVICE.getUser(userUUID).orElseThrow();
        Permission permission = Cache.USER_SERVICE.getPermission(permName);
        user.addPermission(permission);

        user.save();

        return new RedirectView("/admin/permUser?uuid=" + userUUID);

    }

    @GetMapping("/admin/permRemove")
    public RedirectView permRemove(@RequestParam("uuid") UUID userUUID, @RequestParam("permName") String permName) throws SQLException {

        User user = Cache.USER_SERVICE.getUser(userUUID).orElseThrow();
        user.removePermission(permName);

        user.save();

        return new RedirectView("/admin/permUser?uuid=" + userUUID);
    }

    @PostMapping("/admin/saveUser/person")
    public RedirectView addUserToPerson(@RequestParam("uuid") UUID uuid, @RequestParam("person") UUID person) throws SQLException {

        Cache.USER_SERVICE.addPersonUUID(uuid, person);

        return new RedirectView("/admin/user?user=" + uuid);
    }

    @GetMapping("/admin/removeUser/person")
    public RedirectView removePersonFromUser(@RequestParam("uuid") UUID uuid) throws SQLException {

        Cache.USER_SERVICE.removePersonUUID(uuid);

        return new RedirectView("/admin/user?user=" + uuid);
    }


}
