package de.messdiener.cms.app.entities.flow;

import de.messdiener.cms.app.entities.flow.sub.CashFlow;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.FlowEnums;
import de.messdiener.cms.web.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.UUID;

@AllArgsConstructor
@Data
public class Flow {

    private final UUID id;
    private final long date;
    private final FlowEnums.Type type;
    private final FlowEnums.State state;
    private final UUID owner;

    public void save() throws SQLException {
        Cache.FLOW_SERVICE.saveFlow(this);
    }

    public Messdiener getPerson() throws SQLException {
        return Cache.MESSDIENER_SERVICE.find(owner).orElseThrow();
    }

    public CashFlow getCashFlow() throws SQLException {
        return Cache.FLOW_SERVICE.getCashFlowByUUID(id).orElseThrow();
    }

    public String getDateString() {
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

    public Messdiener getOwnerPerson() throws SQLException {
        return Cache.MESSDIENER_SERVICE.find(owner).orElseThrow();
    }
}