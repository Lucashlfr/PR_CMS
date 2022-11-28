package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.entities.finance.FinanceEntry;
import de.messdiener.cms.app.entities.flow.Flow;
import de.messdiener.cms.app.entities.flow.sub.CashFlow;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.FlowEnums;
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
import java.util.Optional;
import java.util.UUID;

@Controller
public class FlowController {

    @GetMapping("/flows")
    public String flowOverview(HttpSession httpSession, Model model, @RequestParam("type") Optional<String> type, @RequestParam("uuid") Optional<String> flow_uuid) throws SQLException {
        SecurityHelper.addSessionUser(httpSession);

        User user = SecurityHelper.getUser();

        model.addAttribute("user", user);


        if ((type.isEmpty() || user.getPerson().isEmpty()) && flow_uuid.isEmpty()) {
            if(user.getPerson().isPresent())
                model.addAttribute("flows", Cache.FLOW_SERVICE.getFlowsByOwner(user.getPerson().get().getUUID()));
            return "flows/flowsOverview";
        }
        model.addAttribute("person", user.getPerson().get());

        if(flow_uuid.isEmpty()) {
            return "flows/createCashFlow";
        }

        model.addAttribute("flow", Cache.FLOW_SERVICE.getFlowByUUID(user.getPerson().get(), UUID.fromString(flow_uuid.get())).orElseThrow());
        model.addAttribute("cashflow", Cache.FLOW_SERVICE.getCashFlowByUUID(UUID.fromString(flow_uuid.get())).orElseThrow());
        return "flows/flowInterfaceCash";
    }

    @PostMapping("/flows/create")
    public RedirectView create(@RequestParam("zweck") String zweck, @RequestParam("uuid") UUID personUUID,
                               @RequestParam("owner") String owner, @RequestParam("mail") String mail,
                               @RequestParam("iban") String iban, @RequestParam("bic") String bic,
                               @RequestParam("data") String data, @RequestParam("file") MultipartFile file) throws Exception {
        if (file.getSize() > 1048576)
            throw new IOException();

        UUID uuid = UUID.randomUUID();
        Messdiener messdiener = Cache.MESSDIENER_SERVICE.find(personUUID).orElseThrow();

        CashFlow cashFlow = new CashFlow(uuid, personUUID, owner, mail, iban, bic, data, zweck, UUID.randomUUID());
        cashFlow.create(file);

        Flow flow = new Flow(uuid, System.currentTimeMillis(), FlowEnums.Type.CASH_FLOW, FlowEnums.State.OPEN, personUUID);
        flow.save();

        EmailEntity.generateNew(mail, "[MS] Antrag erstellt", MailOverlay.generate().addGreeting("Hallo " + owner + ",")
                .addText("F&uuml;r Sie wurde ein neuer Workflow erstellt: " + zweck  + "<br> Wir informieren Sie, sobald er bearbeitet wurde.")
                        .addAdoption_System())
                .send();

        EmailEntity.generateNew(Cache.PFARRBUERO, "[MS] Antrag erstellt", MailOverlay.generate().addGreeting("Hallo zusammen,")
                        .addText("F&uuml;r die Messdiener-Gruppe " + messdiener.getGroup().getName() + " wurde von " + messdiener.getName() +  " ein neuer Workflow erstellt: "
                                + zweck)
                        .addLink("Bitte bearbeitet ihn unter diesem Link", "https://cms.code.system.etu.messdiener.com/public/flow?uuid=" + uuid)
                        .addAdoption_Lucas())
                .send();

        FinanceEntry financeEntry = new FinanceEntry(UUID.randomUUID(), messdiener.getGroup().getUUID(), System.currentTimeMillis(),
                cashFlow.getRevenue(), cashFlow.getExpenditures(), zweck, messdiener.getName(), "", "Erstellt über CashFlow");
        Cache.FINANCE_SERVICE.saveEntity(financeEntry);

        return new RedirectView("/flows", true);
    }

    @GetMapping("/public/flow")
    public String getPublicFlow(Model model, @RequestParam("uuid")UUID uuid) throws SQLException {

        Flow flow = Cache.FLOW_SERVICE.getFlow(uuid).orElseThrow();

        model.addAttribute("person", flow.getOwner_Person());
        model.addAttribute("flow", flow);
        model.addAttribute("cashflow", Cache.FLOW_SERVICE.getCashFlowByUUID(uuid).orElseThrow());
        return "public/flowPublicView";
    }

    @GetMapping("/flows/resend")
    public RedirectView resend(@RequestParam("uuid")UUID uuid) throws Exception {

        Flow flow = Cache.FLOW_SERVICE.getFlow(uuid).orElseThrow();
        Messdiener messdiener = flow.getOwner_Person();
        CashFlow cashFlow = Cache.FLOW_SERVICE.getCashFlowByUUID(uuid).orElseThrow();

        EmailEntity.generateNew(Cache.PFARRBUERO, "[MS] Erinnerung zum Antrag", MailOverlay.generate().addGreeting("Hallo zusammen,")
                        .addText("F&uuml;r die Messdiener-Gruppe " + messdiener.getGroup().getName() + " wurde von " + messdiener.getName() +  " ein neuer Workflow erstellt: "
                                + cashFlow.getZweck())
                        .addLink("Bitte bearbeitet ihn unter diesem Link", "https://cms.code.system.etu.messdiener.com/public/flow?uuid=" + uuid)
                        .addAdoption_Lucas())
                .send();

        return new RedirectView("/flows?type=CASH_FLOW&uuid=" + uuid);
    }

}
