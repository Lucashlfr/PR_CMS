package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.entities.ticket.Ticket;
import de.messdiener.cms.app.entities.ticket.cache.TicketLink;
import de.messdiener.cms.app.entities.ticket.cache.TicketLog;
import de.messdiener.cms.app.entities.ticket.cache.TicketPerson;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.ContextPaths;
import de.messdiener.cms.cache.enums.TicketState;
import de.messdiener.cms.web.security.SecurityHelper;
import de.messdiener.cms.web.utils.DateUtils;
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
import java.util.Optional;
import java.util.UUID;

@Controller
public class TicketController {

    @GetMapping(ContextPaths.TICKETS)
    public String index(Model model, HttpSession httpSession) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);
        model.addAttribute("tickets", Cache.TICKET_SERVICE.getTickets());

        return "tickets/ticketOverview";
    }

    @GetMapping("/ticket/edit")
    public String edit(Model model, @RequestParam("uuid") UUID uuid) throws SQLException {

        model.addAttribute("user", SecurityHelper.getUser());
        model.addAttribute("users", Cache.USER_SERVICE.getUsers());
        model.addAttribute("currentUser", SecurityHelper.getUser());

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        model.addAttribute("ticket", ticket);

        return "tickets/articlePage";
    }

    @PostMapping("/ticket/save/link")
    public RedirectView saveLink(@RequestParam("uuid") UUID uuid, @RequestParam("name") String name, @RequestParam("link") String link) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        TicketLink ticketLink = new TicketLink(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUserID(), name, link);

        Cache.TICKET_SERVICE.addLink(ticket, ticketLink, true);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @GetMapping("/ticket/delete/link")
    public RedirectView deleteLink(@RequestParam("uuid") UUID uuid, @RequestParam("link") UUID link) throws SQLException {
        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        TicketLink ticketLink = ticket.getTicketLinks().stream().filter(tl -> tl.getId().equals(link)).findFirst().orElseThrow();

        Cache.TICKET_SERVICE.deleteLink(ticket, ticketLink);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @PostMapping("/ticket/save/user")
    public RedirectView saveUser(@RequestParam("uuid") UUID uuid, @RequestParam("useruuid") UUID useruuid) throws Exception {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        Cache.TICKET_SERVICE.delete(ticket);

        System.out.println(useruuid);

        User user = Cache.USER_SERVICE.getUsers().stream().filter(u -> u.getUserID().equals(useruuid)).findAny().orElseThrow();

        ticket.setUserUUID(user.getUserID());
        Cache.TICKET_SERVICE.save(ticket);

        extracted(ticket, user);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    private void extracted(Ticket ticket, User user) throws Exception {
        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUserID(),
                "Ticket wurde " + user.getNameString() + " von " + SecurityHelper.getNameString() + " zugewiesen."
        ));

        EmailEntity email = EmailEntity.generateNew(user.getEmail(), "Neue Zuständigkeit", MailOverlay.generate()
                .addGreeting("Hallo " + user.getFirstname() + ",")
                .addText("Dir wurde das " + "Ticket Nr. " + ticket.getId().toString()
                        + " [" + ticket.getTicketPerson().getName()
                        + " (" + ticket.getTicketPerson().getAssociation() + "), " + ticket.getDates().getGermanDateCreated() + "] zugewiesen. " +
                        "<br>Bitte bearbeite es bis zum " + ticket.getDates().getGermanDateDeadline())
                        .addLink("Zum Ticket", "https://localhost:8081/ticket/edit?uuid=" + ticket.getId().toString())
                .addAdoption_Lucas());

        Cache.EMAIL_SERVICE.sendMail(email);
    }

    @GetMapping("/ticket/create")
    public String addTicket(Model model) throws SQLException {

        model.addAttribute("uuid", UUID.randomUUID().toString());
        model.addAttribute("currentDate", DateUtils.convertLongToDate(System.currentTimeMillis(), DateUtils.DateType.ENGLISH));

        model.addAttribute("persons", Cache.TICKET_SERVICE.getTicketPersons());
        System.out.println(Cache.TICKET_SERVICE.getTicketPersons().size());

        return "tickets/createTicket";
    }


    @GetMapping("/ticket/lektoriat")
    public String correction(Model model, @RequestParam("uuid") UUID uuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        model.addAttribute("ticket", ticket);


        if (!ticket.getUserUUID().equals(SecurityHelper.getUser().getUserID())) {
            ticket.setUserUUID(SecurityHelper.getUser().getUserID());
        }

        if (ticket.getTicketState() == TicketState.OPEN) {
            ticket.setTicketState(TicketState.CORRECTOR_1);
        }
        Cache.TICKET_SERVICE.save(ticket);

        model.addAttribute("disabled", !ticket.isCorrection());

        return "tickets/lektoriat";
    }

    @GetMapping("/ticket/lektoriat/accept")
    public RedirectView correctionAccept(Model model, @RequestParam("uuid") UUID uuid) throws Exception {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        TicketLog ticketLog = TicketLog.create(ticket.getTicketState().getName() + " genehmigt durch " + SecurityHelper.getNameString());

        model.addAttribute("ticket", ticket);

        if (ticket.getTicketState() == TicketState.CORRECTOR_1) {
            ticket.setTicketState(TicketState.CORRECTOR_2);
            ticket.setUserUUID(Cache.ALFRED_USER.getUserID());

            extracted(ticket, Cache.ALFRED_USER);

        } else if (ticket.getTicketState() == TicketState.CORRECTOR_2) {
            ticket.setTicketState(TicketState.AWAITING_PUBLICATION);
            ticket.setUserUUID(Cache.SYSTEM_USER.getUserID());
        }
        Cache.TICKET_SERVICE.save(ticket);
        Cache.TICKET_SERVICE.addLog(ticket, ticketLog);

        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @PostMapping("/ticket/lektoriat/proposeChanges")
    public RedirectView proposeChanges(Model model, @RequestParam("uuid") UUID uuid, @RequestParam("text") String text,
                                       @RequestParam("type") String type) throws Exception {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();

        if (type.equals("reject")) {
            ticket.setTicketState(TicketState.REJECTED);
            Cache.TICKET_SERVICE.save(ticket);
            Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create("Ticket wurde von " + SecurityHelper.getNameString() + " abgelehnt: " + text));


            return new RedirectView("/ticket/edit?uuid=" + uuid);
        }
        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create("Folgender Verbesserungsvorschlag wurde von " + SecurityHelper.getNameString() + " geschickt: " + text));


        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @GetMapping("/ticket/delete")
    public RedirectView delete(@RequestParam("uuid") UUID uuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        Cache.TICKET_SERVICE.delete(ticket);

        return new RedirectView("/tickets");
    }

    @GetMapping("/ticket/close")
    public RedirectView closeTicket(@RequestParam("uuid") UUID uuid) throws Exception {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();

        ticket.setTicketState(TicketState.PUBLISHED);
        ticket.setUserUUID(Cache.SYSTEM_USER.getUserID());
        Cache.TICKET_SERVICE.save(ticket);
        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create("Ticket wurde von " + SecurityHelper.getNameString() + " geschlossen (Veröffentlicht)."));


        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }


    @GetMapping("/public/tickets")
    public String getTicket(Model model, @RequestParam("uuid") Optional<UUID> uuid) throws SQLException {
        model.addAttribute("ticket", Cache.TICKET_SERVICE.getTicket(uuid.orElse(UUID.randomUUID())).orElse(Ticket.empty()));

        model.addAttribute("uuid", uuid);
        return "public/ticketPublicView";
    }

    @GetMapping("/public/createTicket")
    public String create() {
        return "public/createTicketPublic";
    }

    @GetMapping("/public/createTicket/uploadData")
    public String uploadPublic(Model model, @RequestParam("uuid") Optional<String> uuid) {
        model.addAttribute("uuid", uuid);
        return "public/uploadImg";
    }

    @PostMapping("/public/createTicket/uploadData/save")
    public RedirectView saveUploadPublic(@RequestParam("file") MultipartFile file) throws IOException, SQLException {

        FileEntity fileEntity = FileEntity.convert(file, "GAST");
        fileEntity.setName("GastUpload_" + file.getName());
        fileEntity.save();

        return new RedirectView("/public/createTicket/uploadData?uuid=" + fileEntity.getId());
    }

    @PostMapping("/public/createTicket/save")
    public RedirectView saveTicket(@RequestParam("lastname") String lastname, @RequestParam("firstname") String firstname,
                                   @RequestParam("association") String association, @RequestParam("email") String email,
                                   @RequestParam("phone") String phone, @RequestParam("deadline") String deadline,
                                   @RequestParam("text") String text, @RequestParam("path") Optional<String> path
    ) throws SQLException {

        UUID ticketUUID = UUID.randomUUID();

        TicketPerson ticketPerson = new TicketPerson(UUID.randomUUID(), lastname, firstname, association, phone, email);

        long dl = DateUtils.convertDateToLong(deadline, DateUtils.DateType.ENGLISH);
        Ticket ticket = new Ticket(ticketUUID, System.currentTimeMillis(), System.currentTimeMillis(), dl, Cache.SYSTEM_USER.getUserID(), TicketState.OPEN, new ArrayList<>(),
                new ArrayList<>(), ticketPerson, text);


        Cache.TICKET_SERVICE.save(ticket);


        if (path.isPresent() && path.get().equals("ADMIN")) {
            Cache.TICKET_SERVICE.addLog(ticket, new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUserID(),
                    "Ticket wurde von " + firstname + " " + lastname + " angelegt!"));
            return new RedirectView("/ticket/edit?uuid=" + ticketUUID);
        }

        Cache.TICKET_SERVICE.addLog(ticket, new TicketLog(UUID.randomUUID(), System.currentTimeMillis(), Cache.SYSTEM_USER.getUserID(),
                "Ticket wurde von " + firstname + " " + lastname + " (via wizard) angelegt!"));

        return new RedirectView("/");
    }


    @GetMapping("/ticket/assignMe")
    public RedirectView assignMe(@RequestParam("uuid") UUID uuid) throws SQLException {

        Ticket ticket = Cache.TICKET_SERVICE.getTicket(uuid).orElseThrow();
        Cache.TICKET_SERVICE.delete(ticket);

        User user = SecurityHelper.getUser();
        ticket.setUserUUID(user.getUserID());
        Cache.TICKET_SERVICE.save(ticket);

        Cache.TICKET_SERVICE.addLog(ticket, TicketLog.create(UUID.randomUUID(), System.currentTimeMillis(), SecurityHelper.getUser().getUserID(),
                user.getNameString() + " hat sich das Ticket zugewiesen."
        ));
        return new RedirectView("/ticket/edit?uuid=" + uuid);
    }

    @GetMapping("/calendar")
    public String calendar(HttpSession session, Model model) throws SQLException {
        SecurityHelper.addSessionUser(session);


        model.addAttribute("tickets", Cache.TICKET_SERVICE.getTickets());
        return "tickets/calendar";
    }

}
