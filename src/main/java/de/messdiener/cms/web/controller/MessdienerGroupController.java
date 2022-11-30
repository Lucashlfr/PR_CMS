package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.messdiener.GroupSession;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.entities.messdiener.MessdienerGroup;
import de.messdiener.cms.cache.Cache;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MessdienerGroupController {

    @GetMapping("/messdiener/group")
    public String group(HttpSession httpSession, Model model, @RequestParam("uuid") Optional<UUID> groupU) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);
        model.addAttribute("dateien", Cache.CLOUD_SERVICE.getYourFiles());

        if (groupU.isPresent()) {
            MessdienerGroup group = Cache.GROUP_SERVICE.getGroup(groupU.get()).orElseThrow();
            model.addAttribute("group", group);
            model.addAttribute("availAblePersons", group.getAvailable());
            model.addAttribute("inGroup", group.getMessdienerList());
            return "messdiener/group/groupPersonMap";
        }

        model.addAttribute("groups", Cache.GROUP_SERVICE.getGroups());
        return "messdiener/group/group";
    }

    @PostMapping("/messdiener/group/create")
    public RedirectView createGroup(@RequestParam("name") String name) throws SQLException {

        MessdienerGroup.of(UUID.randomUUID(), name, "KEINE", new ArrayList<>()).save();

        return new RedirectView("/messdiener/group");
    }

    @GetMapping("/messdiener/group/map")
    public RedirectView map(@RequestParam("group_uuid") UUID group_uuid, @RequestParam("person_uuid") UUID person_uuid) throws SQLException {

        MessdienerGroup group = Cache.GROUP_SERVICE.getGroup(group_uuid).orElseThrow();
        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(person_uuid).orElseThrow();
        Cache.GROUP_SERVICE.map(group, messdiener);

        return new RedirectView("/messdiener/group?uuid=" + group_uuid);
    }

    @GetMapping("/messdiener/group/unmap")
    public RedirectView unmap(@RequestParam("group_uuid") UUID group_uuid, @RequestParam("person_uuid") UUID person_uuid) throws SQLException {

        MessdienerGroup group = Cache.GROUP_SERVICE.getGroup(group_uuid).orElseThrow();
        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(person_uuid).orElseThrow();
        Cache.GROUP_SERVICE.unmap(group, messdiener);

        return new RedirectView("/messdiener/group?uuid=" + group_uuid);
    }

    @GetMapping("/messdiener/group/promote")
    public RedirectView promote(@RequestParam("group_uuid") UUID group_uuid, @RequestParam("person_uuid") UUID person_uuid) throws SQLException {

        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(person_uuid).orElseThrow();
        Cache.GROUP_SERVICE.promote(messdiener);

        return new RedirectView("/messdiener/group?uuid=" + group_uuid);
    }

    @PostMapping("/messdiener/group/logo")
    public RedirectView logo(@RequestParam("group") UUID group_uuid, @RequestParam("file") UUID file_uuid) throws SQLException {

        MessdienerGroup group = Cache.GROUP_SERVICE.getGroup(group_uuid).orElseThrow();

        group.setLogoSvg(file_uuid.toString());
        group.save();

        return new RedirectView("/messdiener/group");
    }

    @GetMapping("/group/session")
    public String session(HttpSession httpSession, Model model, @RequestParam("group") Optional<String> group, @RequestParam("id") Optional<String> id) throws SQLException {
        SecurityHelper.addSessionUser(httpSession);

        if (group.isEmpty()) {
            model.addAttribute("groups", Cache.GROUP_SERVICE.getByUser(SecurityHelper.getUser()));
            return "messdiener/sessions/sessions";
        }

        UUID groupUUID = UUID.fromString(group.get());
        MessdienerGroup messdienerGroup = Cache.GROUP_SERVICE.getGroup(groupUUID).orElseThrow();

        model.addAttribute("group", messdienerGroup);
        model.addAttribute("sessions", Cache.GROUP_SERVICE.getSessions(groupUUID));

        model.addAttribute("id", id);
        if (id.isPresent()) {
            model.addAttribute("event", Cache.GROUP_SERVICE.getSessions(groupUUID).stream().filter(s -> s.getId().toString().equals(id.get())).findFirst().orElseThrow());
        }

        return "messdiener/sessions/sessionsInterface";
    }

    @PostMapping("/messdiener/group/session/create")
    public RedirectView createSession(@RequestParam("uuid") UUID uuid, @RequestParam("date") String date) throws SQLException {

        UUID id = UUID.randomUUID();
        GroupSession.of(id,
                DateUtils.convertDateToLong(date, DateUtils.DateType.ENGLISH_DATETIME),
                uuid).save();


        return new RedirectView("/group/session?group=" + uuid + "&id=" + id);
    }

    @PostMapping("/group/session/import")
    public RedirectView importData(@RequestParam("file") MultipartFile file) throws SQLException, IOException, IllegalAccessException {

        List<List<String>> records = Utils.importFromCSV(file);
        createSessions(records);

        return new RedirectView("/group/session/");
    }


    private void createSessions(List<List<String>> records) throws SQLException {

        for (List<String> session : records) {

            UUID uuid = UUID.randomUUID();
            long date = DateUtils.convertDateToLong(session.get(0), DateUtils.DateType.GERMAN_WITH_TIME);
            UUID group = UUID.fromString(session.get(1));


            GroupSession groupSession = GroupSession.of(uuid, date, group);
            groupSession.save();
        }
    }

    @GetMapping("/group/session/save/mapping")
    public RedirectView map(@RequestParam("uuid") UUID uuid, @RequestParam("date") UUID[] personIds) throws SQLException {
        List<Messdiener> messdieners = new ArrayList<>();
        GroupSession session = Cache.GROUP_SERVICE.getSession(uuid).orElseThrow();

        for (int i = 0; i < personIds.length; i++) {
            UUID person_uuid = personIds[i];
            Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(person_uuid).orElseThrow();

            messdieners.add(messdiener);
        }

        session.setMessdieners(messdieners);
        session.save();

        return new RedirectView("/group/session?group=" + session.getGroup() + "&id=" + uuid);
    }
}
