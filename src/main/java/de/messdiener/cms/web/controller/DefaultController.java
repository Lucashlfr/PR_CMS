package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.ticket.cache.TicketSummery;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String index(Model model) throws SQLException {

        User user = SecurityHelper.getUser();

        model.addAttribute("open_tickets", Cache.TICKET_SERVICE.getTicketsByUser(user.getUser_UUID(), TicketState.OPEN).size());
        model.addAttribute("allTickets", Cache.TICKET_SERVICE.getTickets().size());

        int summ = Cache.TICKET_SERVICE.getTicketsByUser(user.getUser_UUID(), TicketState.CORRECTOR_1).size() + Cache.TICKET_SERVICE.getTicketsByUser(user.getUser_UUID(), TicketState.CORRECTOR_2).size();

        model.addAttribute("open_tickets_corrector", summ);
        model.addAttribute("user", user);

        model.addAttribute("tasks", Cache.TICKET_SERVICE.getTicketsByUser(user.getUser_UUID()));
        model.addAttribute("summery", TicketSummery.create());

        return "index";
    }

    @PostMapping("/search")
    public RedirectView search(@RequestParam("uuid") UUID uuid) throws SQLException {

        Optional<Ticket> ticket = Cache.TICKET_SERVICE.getTicket(uuid);
        if (ticket.isPresent()) {
            return new RedirectView("/ticket/edit?uuid=" + uuid.toString());
        }
        return new RedirectView("/");
    }

    @GetMapping("/public/tickets")
    @ResponseBody
    public String getOverview(Model model) throws SQLException {
        return "<h1>TEST";
    }

    @GetMapping("/public/tickets/search")
    public String getOverview(Model model, @RequestParam("uuid")UUID uuid) throws SQLException {
        model.addAttribute("ticketsPerPerson", Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow());
        return "public/ticketPublicView";
    }


    private static String html1 = "";

    @GetMapping("/editor")
    public String editor(Model model) {

        model.addAttribute("test", html1);

        return "editor";
    }

    @PostMapping("/editor/save")
    public RedirectView editorSave(@RequestParam("html") String html) {

        EmailEntity.generateNew("info@messdiener-knittelsheim.de", "[LOG] Service wurde gestertet! (ID: " + UUID.randomUUID() + ")", html, "https://cms.kath-pfarrei-bellheim.de");

        html1 = html;
        return new RedirectView("/editor");
    }
}
