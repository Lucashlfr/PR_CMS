package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.app.entities.ticket.cache.TicketLink;
import de.messdiener.cms.app.entities.ticket.cache.TicketLog;
import de.messdiener.cms.app.entities.ticket.cache.TicketPerson;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.TicketState;
import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@Controller
public class TicketController {

    @GetMapping("/tickets")
    public String index(Model model) throws SQLException {


        model.addAttribute("tickets", Cache.TICKET_SERVICE.getTickets());

        return "ticketOverview";
    }

    @GetMapping("/ticket/edit")
    public String edit(Model model, @RequestParam("uuid") UUID uuid) throws SQLException {

        model.addAttribute("user", SecurityHelper.getUser());
        model.addAttribute("users", Cache.USER_SERVICE.getUsers());
        model.addAttribute("currentUser", SecurityHelper.getUser());

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        model.addAttribute("ticket", ticket);

        return "articlePage";
    }

    @PostMapping("/ticket/save/link")
    public RedirectView saveLink(@RequestParam("uuid") UUID uuid, @RequestParam("name") String name, @RequestParam("link") String link) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        TicketLink ticketLink = new TicketLink(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUser_UUID(), name, link);

        Cache.TICKET_SERVICE.addLink(ticket, ticketLink, true);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @GetMapping("/ticket/delete/link")
    public RedirectView deleteLink(@RequestParam("uuid") UUID uuid, @RequestParam("link") UUID link) throws SQLException {
        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        TicketLink ticketLink = ticket.getTicketLinks().stream().filter(tl -> tl.getLink_UUID().equals(link)).findFirst().orElseThrow();

        Cache.TICKET_SERVICE.deleteLink(ticket, ticketLink);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @PostMapping("/ticket/save/user")
    public RedirectView saveUser(@RequestParam("uuid") UUID uuid, @RequestParam("useruuid") UUID useruuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        Cache.TICKET_SERVICE.delete(ticket);

        System.out.println(useruuid);

        User user = Cache.USER_SERVICE.getUsers().stream().filter(u -> u.getUser_UUID().equals(useruuid)).findAny().orElseThrow();

        ticket.setUser_UUID(user.getUser_UUID());
        Cache.TICKET_SERVICE.save(ticket);

        extracted(ticket, user);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    private void extracted(Ticket ticket, User user) throws SQLException {
        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUser_UUID(),
                "Ticket wurde " + user.getUsername() + " von " + SecurityHelper.getUsername() + " zugewiesen."
        ));

        EmailEntity email = new EmailEntity(user.getEmail(), "Neue Zuständigkeit", "Hallo " + user.getUsername() + ",\n\ndir wurde das Ticket Nr. " + ticket.getUUID().toString()
                + " [" + ticket.getTicketPerson().getName()
                + " (" + ticket.getTicketPerson().getAssociation() + "), " + ticket.getDates().getGermanDate_CREATED() + "] \nvon " + SecurityHelper.getUsername() + " zugewiesen. \n" +
                "Bitte bearbeite das Ticket bis zum " + ticket.getDates().getGermanDate_DEADLINE() + ".\n\n", "https://localhost:8081/ticket/edit?uuid=" + ticket.getUUID().toString());

        Cache.EMAIL_SERVICE.sendMail(email);
    }

    @GetMapping("/ticket/create")
    public String addTicket(Model model) throws SQLException {

        model.addAttribute("uuid", UUID.randomUUID().toString());
        model.addAttribute("currentDate", DateUtils.convertLongToDate(System.currentTimeMillis(), DateUtils.DateType.ENGLISH));

        model.addAttribute("persons", Cache.TICKET_SERVICE.getTicketPersons());

        return "createTicket";
    }

    @PostMapping("/ticket/create/save")
    public RedirectView createTicket(@RequestParam("uuid") UUID uuid, @RequestParam("currentDate") String currentDateString,
                                     @RequestParam("deadline") String deadlineString, @RequestParam("name") String name,
                                     @RequestParam("association") String association, @RequestParam("email") String email,
                                     @RequestParam("phone") String phone) throws SQLException {

        String firstname = name.split(", ")[1];
        String lastname = name.split(", ")[0];

        TicketPerson ticketPerson = new TicketPerson(UUID.randomUUID(), lastname, firstname, association, phone, email);

        Ticket ticket = new Ticket(uuid, System.currentTimeMillis(), System.currentTimeMillis(), DateUtils.convertDateToLong(deadlineString, DateUtils.DateType.ENGLISH),
                Cache.SYSTEM_USER.getUser_UUID(), TicketState.OPEN, new ArrayList<>(), new ArrayList<>(), ticketPerson);

        Cache.TICKET_SERVICE.save(ticket);
        Cache.TICKET_SERVICE.addLog(ticket, new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUser_UUID(),
                "Ticket wurde von " + SecurityHelper.getUsername() + " angelegt!"));

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @GetMapping("/ticket/jobs")
    public String getYourJobs(Model model, @RequestParam("state")String state) throws SQLException {

        if(state.equals(TicketState.OPEN.toString())) {
            model.addAttribute("tickets", Cache.TICKET_SERVICE.getTicketsByUser(SecurityHelper.getUser().getUser_UUID(), TicketState.OPEN));
        }else {
            model.addAttribute("tickets", Cache.TICKET_SERVICE.getTicketsByUser(SecurityHelper.getUser().getUser_UUID()));
        }

        return "deineAuftraege";
    }

    @GetMapping("/ticket/lektoriat")
    public String correction(Model model, @RequestParam("uuid")UUID uuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();

        model.addAttribute("ticket", ticket);

        if(ticket.getTicketState() == TicketState.OPEN){
            ticket.setTicketState(TicketState.CORRECTOR_1);
        }
        Cache.TICKET_SERVICE.save(ticket);

        return "lektoriat";
    }

    @GetMapping("/ticket/lektoriat/accept")
    public RedirectView correctionAccept(Model model, @RequestParam("uuid")UUID uuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        TicketLog ticketLog = TicketLog.create(ticket.getTicketState().getName() + " genehmigt durch " + SecurityHelper.getUsername());

        model.addAttribute("ticket", ticket);

        if(ticket.getTicketState() == TicketState.CORRECTOR_1){
            ticket.setTicketState(TicketState.CORRECTOR_2);
            ticket.setUser_UUID(Cache.ALFRED_USER.getUser_UUID());

            extracted(ticket, Cache.ALFRED_USER);

        }else if(ticket.getTicketState() == TicketState.CORRECTOR_2){
            ticket.setTicketState(TicketState.AWAITING_PUBLICATION);
        }
        Cache.TICKET_SERVICE.save(ticket);
        Cache.TICKET_SERVICE.addLog(ticket, ticketLog);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @PostMapping("/ticket/lektoriat/proposeChanges")
    public RedirectView proposeChanges(Model model, @RequestParam("uuid")UUID uuid, @RequestParam("text")String text,
                                       @RequestParam("type")String type) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();

        if(type.equals("reject")){
            ticket.setTicketState(TicketState.REJECTED);
            Cache.TICKET_SERVICE.save(ticket);
            Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create("Ticket wurde von " + SecurityHelper.getUsername() + " abgelehnt: " + text));

            EmailEntity email = new EmailEntity(ticket.getTicketPerson().getEmail(), "Abgelehnt",

                    ticket.getTicketPerson().getGreeting() + "\n\nDein Ticket wurde abgelehnt: \n" + text + "\n\n" + Utils.getMailClose(), ""

            );
            Cache.EMAIL_SERVICE.sendMail(email);

            return new RedirectView("/ticket/edit?uuid=" + uuid);
        }
        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create("Folgender Verbesserungsvorschlag wurde von " + SecurityHelper.getUsername() + " geschickt: " + text));

        EmailEntity email = new EmailEntity(ticket.getTicketPerson().getEmail(), "Verbersserungsvorschlag",

                ticket.getTicketPerson().getGreeting() + "\n\n" + text + "\n\n" + Utils.getMailClose(), ""

        );
        Cache.EMAIL_SERVICE.sendMail(email);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @GetMapping("/ticket/delete")
    public RedirectView delete(@RequestParam("uuid")UUID uuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        Cache.TICKET_SERVICE.delete(ticket);

        return new RedirectView("/tickets");
    }

    @GetMapping("/ticket/close")
    public RedirectView closeTicket(@RequestParam("uuid")UUID uuid) throws SQLException{

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();

        ticket.setTicketState(TicketState.PUBLISHED);
        ticket.setUser_UUID(SecurityHelper.getUser().getUser_UUID());
        Cache.TICKET_SERVICE.save(ticket);
        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create("Ticket wurde von " + SecurityHelper.getUsername() + " geschlossen"));

        EmailEntity email = new EmailEntity(ticket.getTicketPerson().getEmail(), "Abgelehnt",

                ticket.getTicketPerson().getGreeting() + "\n\nDein Ticket wurde geschlossen! Vielen Dank für die Nutzung des Services"+ "\n\n" + Utils.getMailClose(), ""

        );
        Cache.EMAIL_SERVICE.sendMail(email);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }
}
