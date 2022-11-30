package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.event.WorshipEvent;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.EventType;
import de.messdiener.cms.cache.enums.OGroup;
import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Controller
public class WorshipController {

    @GetMapping("/worship")
    public String index(HttpSession httpSession, Model model, @RequestParam("uuid") Optional<UUID> uuid) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);

        model.addAttribute("events", Cache.WORSHIP_SERVICE.getEvents());
        model.addAttribute("oGroups", OGroup.values());

        if(uuid.isPresent()){
            model.addAttribute("event", Cache.WORSHIP_SERVICE.getEvents().stream().filter(event -> event.getId().equals(uuid.get())).findFirst().orElseThrow());

            return "worship/worshipInterface";
        }

        return "worship/worshipOverview";
    }


    @PostMapping("/worship/create")
    public RedirectView create(@RequestParam("date")String date, @RequestParam("oGroup")String oGroup, @RequestParam("eventType")String eventType) throws SQLException {

        UUID uuid = UUID.randomUUID();
        WorshipEvent event = new WorshipEvent(uuid, DateUtils.convertDateToLong(date, DateUtils.DateType.ENGLISH), OGroup.valueOf(oGroup),
                EventType.valueOf(eventType), new HashSet<>());
        event.save();

        return new RedirectView(("/worship?uuid="+uuid));
    }

    @PostMapping("/worship/save")
    public RedirectView save(@RequestParam("uuid") UUID uuid, @RequestParam("date")String date, @RequestParam("oGroup")String oGroup, @RequestParam("eventType")String eventType) throws SQLException {

        WorshipEvent event = Cache.WORSHIP_SERVICE.find(uuid).orElseThrow();
        event.setDate(DateUtils.convertDateToLong(date, DateUtils.DateType.ENGLISH));
        event.setOGroup(OGroup.valueOf(oGroup.toUpperCase()));
        event.setEventType(EventType.valueOf(eventType));

        event.save();

        return new RedirectView("/worship?uuid="+uuid);
    }

    @GetMapping("/worship/save/mapping")
    public RedirectView map(@RequestParam("uuid")UUID uuid, @RequestParam("persons")UUID[] persons) throws SQLException {

        WorshipEvent event = Cache.WORSHIP_SERVICE.find(uuid).orElseThrow();
        Cache.WORSHIP_SERVICE.clearDutyByEvent(event);

        for (UUID person : persons) {
            Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(person).orElseThrow();
            Cache.WORSHIP_SERVICE.dutyPerson(messdiener, event);
        }

        return new RedirectView("/worship?uuid=" + uuid);
    }

}
