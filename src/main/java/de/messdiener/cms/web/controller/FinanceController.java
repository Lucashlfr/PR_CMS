package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.finance.FinanceEntry;
import de.messdiener.cms.app.entities.messdiener.MessdienerGroup;
import de.messdiener.cms.cache.Cache;
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
import java.util.Optional;
import java.util.UUID;

@Controller
public class FinanceController {

    @GetMapping("/finance")
    public String index(HttpSession httpSession, Model model, @RequestParam("tenant") Optional<String> inputT) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);

        String input = inputT.isEmpty() ? "" : inputT.get();

        model.addAttribute("availAbleTenants", Cache.GROUP_SERVICE.getByUser(SecurityHelper.getUser()));

        if (input.isEmpty()) {
            return "finance/financeOverview";
        }
        UUID tenant = UUID.fromString(input);
        MessdienerGroup group = Cache.GROUP_SERVICE.getGroup(tenant).orElseThrow();

        model.addAttribute("group", group);
        model.addAttribute("user", SecurityHelper.getUser());
        model.addAttribute("entries", Cache.FINANCE_SERVICE.getEntities(tenant));
        model.addAttribute("initial", group.getFinanceIndex().getInitial().orElse(FinanceEntry.empty()));
        model.addAttribute("intialValue", group.getFinanceIndex().getInitialValue());
        model.addAttribute("value", group.getFinanceIndex().getValue());

        model.addAttribute("revenue", group.getFinanceIndex().getRevenues());
        model.addAttribute("expenditures", group.getFinanceIndex().getExpenditures());

        model.addAttribute("dates", group.getFinanceIndex().getDates());
        model.addAttribute("cashData", group.getFinanceIndex().getCashData());
        return "finance/finance";

    }

    @PostMapping("/finance/save")
    public RedirectView save(@RequestParam("group") UUID group, @RequestParam("date") String englishDate, @RequestParam("revenue") double revenue,
                             @RequestParam("expenditures") double expenditures, @RequestParam("type") String type,
                             @RequestParam("editor") String editor, @RequestParam("costCenter") String costCenter,
                             @RequestParam("note") String note) throws SQLException {

        if (expenditures == 0 && revenue == 0) return new RedirectView("/finance?tenant=" + group);

        FinanceEntry financeEntry = FinanceEntry.of(UUID.randomUUID(), group, DateUtils.convertDateToLong(englishDate, DateUtils.DateType.ENGLISH),
                revenue, expenditures, type, editor, costCenter, note);
        Cache.FINANCE_SERVICE.saveEntity(financeEntry);

        return new RedirectView("/finance?tenant=" + group);
    }

    @GetMapping("/finance/delete")
    public RedirectView delete(@RequestParam("uuid") UUID uuid, @RequestParam("tenant") UUID oGroup) throws SQLException {

        Cache.FINANCE_SERVICE.delete(uuid);

        return new RedirectView("/finance?tenant=" + oGroup);
    }
}
