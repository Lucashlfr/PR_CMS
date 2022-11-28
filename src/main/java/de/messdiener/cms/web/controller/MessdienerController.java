package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.event.WorshipEvent;
import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.entities.messdiener.MessdienerGroup;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.OGroup;
import de.messdiener.cms.cache.enums.PersonRank;
import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

@Controller
public class MessdienerController {

    @GetMapping("/messdiener")
    public String index(HttpSession session, Model model, @RequestParam("uuid") Optional<UUID> uuid) throws SQLException {

        SecurityHelper.addSessionUser(session);

        model.addAttribute("persons", Cache.MESSDIENER_SERVICE.getPersonsWhoUserCanEdit(SecurityHelper.getUser()));
        model.addAttribute("ranks", PersonRank.values());
        model.addAttribute("oGroups", OGroup.values());

        if (uuid.isPresent()) {
            model.addAttribute("person",
                    Cache.MESSDIENER_SERVICE.getPersons().stream().filter(p -> p.getUUID().equals(uuid.get())).findFirst().orElseThrow());
            return "messdiener/messdienerInterface";
        }

        return "messdiener/messdienerOverview";
    }

    @PostMapping("/messdiener/create")
    public RedirectView create(@RequestParam("firstname") String firstname,
                               @RequestParam("lastname") String lastname,
                               @RequestParam("adress") String adress, @RequestParam("birthday") String birthday,
                               @RequestParam("phone") String phone, @RequestParam("mobile_child") String mobile_child,
                               @RequestParam("mobile_parents") String mobile_parents,
                               @RequestParam("email_child") String email_child,
                               @RequestParam("email_parents") String email_parents) throws SQLException {

        long birth = DateUtils.convertDateToLong(birthday, DateUtils.DateType.ENGLISH);

        Messdiener messdiener = Messdiener.of(UUID.randomUUID(), firstname, lastname,
                adress, birth, phone, mobile_child, mobile_parents, email_child, email_parents, "");
        messdiener.save();

        return new RedirectView("/messdiener");
    }


    @PostMapping("/messdiener/save")
    public RedirectView save(@RequestParam("uuid") UUID uuid, @RequestParam("firstname") String firstname,
                             @RequestParam("lastname") String lastname,
                             @RequestParam("adress") String adress, @RequestParam("birthday") String birthday,
                             @RequestParam("phone") String phone, @RequestParam("mobile_child") String mobile_child,
                             @RequestParam("mobile_parents") String mobile_parents,
                             @RequestParam("email_child") String email_child,
                             @RequestParam("email_parents") String email_parents,
                             @RequestParam("notes") String notes) throws SQLException {

        long birth = DateUtils.convertDateToLong(birthday, DateUtils.DateType.ENGLISH);

        Messdiener messdiener = Messdiener.of(uuid, firstname, lastname, adress, birth, phone, mobile_child, mobile_parents, email_child, email_parents, notes);
        messdiener.save();

        return new RedirectView("/messdiener?uuid=" + uuid);
    }

    @PostMapping("/messdiener/save/file")
    public RedirectView save(@RequestParam("uuid") UUID uuid, @RequestParam("file") MultipartFile file) throws SQLException, IOException {

        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(uuid).orElseThrow();

        if (file.getSize() < 1048576) {
            FileEntity fileEntity = FileEntity.convert(file, SecurityHelper.getUser().getUser_UUID().toString());
            messdiener.addFile(fileEntity);
        }

        return new RedirectView("/messdiener?uuid=" + uuid);
    }

    @PostMapping("/messdiener/save/img")
    public RedirectView saveImg(@RequestParam("uuid") UUID uuid, @RequestParam("file") MultipartFile file) throws SQLException, IOException {
        System.out.println("save");
        System.out.println(file.getSize());
        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(uuid).orElseThrow();

        if (file.getSize() < 1048576) {
            FileEntity fileEntity = FileEntity.convert(file, SecurityHelper.getUser().getUser_UUID().toString());
            messdiener.setImg(fileEntity);

            System.out.println("save");
        }

        return new RedirectView("/messdiener?uuid=" + uuid);
    }

    @GetMapping("/messdiener/delete/file")
    public RedirectView deleteFile(@RequestParam("person")UUID person, @RequestParam("uuid")UUID file) throws SQLException {
        FileEntity fileEntity = Cache.CLOUD_SERVICE.get(file.toString());
        Cache.CLOUD_SERVICE.delete(fileEntity.getUUID());
        return new RedirectView("/messdiener?uuid=" + person);
    }

    @GetMapping("/messdiener/save/mapping")
    public RedirectView map(@RequestParam("uuid") UUID uuid, @RequestParam("date") UUID[] dateUUIDS) throws SQLException {

        Messdiener messdiener = Cache.MESSDIENER_SERVICE.getPersons().stream().filter(messdiener1 -> messdiener1.getUUID().equals(uuid)).findFirst().orElseThrow();

        Cache.WORSHIP_SERVICE.clearAvailability(messdiener);

        for (int i = 0; i < dateUUIDS.length; i++) {
            UUID dateUUID = dateUUIDS[i];
            WorshipEvent event = Cache.WORSHIP_SERVICE.getEvents().stream().filter(event1 -> event1.getUUID().equals(dateUUID)).findFirst().orElseThrow();

            Cache.WORSHIP_SERVICE.addAvailability(messdiener, event);
        }
        return new RedirectView("/messdiener?uuid=" + uuid);
    }

    @GetMapping("/messdiener/delete")
    public RedirectView delete(@RequestParam("uuid") String uuid) throws SQLException {
        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(UUID.fromString(uuid)).orElseThrow();
        Cache.MESSDIENER_SERVICE.delte(messdiener);

        return new RedirectView("/messdiener");
    }

    @PostMapping("/messdiener/import")
    public RedirectView importData(@RequestParam("file") MultipartFile file) throws SQLException, IOException, IllegalAccessException {

        List<List<String>> records = Utils.importFromCSV(file);
        createPersons(records);

        return new RedirectView("/messdiener");
    }

    private void createPersons(List<List<String>> records) throws SQLException {

        for (List<String> person : records) {

            UUID uuid = UUID.randomUUID();
            String firstname = person.get(0);
            String lastname = person.get(1);
            String adress = person.get(2);
            long birthday = DateUtils.convertDateToLong(person.get(3), DateUtils.DateType.GERMAN);
            String phone = person.get(4);
            String mobile_child = person.get(5);
            String mobile_parents = person.get(6);
            String mail_child = person.get(7);
            String mail_parents = person.get(8);

            Messdiener messdiener = new Messdiener(uuid, firstname, lastname, adress, birthday, phone, mobile_child, mobile_parents, mail_child, mail_parents, "/");
            messdiener.save();
        }

    }


}
