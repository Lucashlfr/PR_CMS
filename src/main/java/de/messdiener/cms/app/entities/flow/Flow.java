package de.messdiener.cms.app.entities.flow;

import de.messdiener.cms.app.entities.flow.sub.CashFlow;
import de.messdiener.cms.app.entities.messdiener.Messdiener;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.cache.enums.FlowEnums;
import de.messdiener.cms.web.utils.DateUtils;

import java.sql.SQLException;
import java.util.UUID;

public class Flow {

    private UUID uuid;
    private long date;
    private FlowEnums.Type type;
    private FlowEnums.State state;
    private UUID owner;

    public Flow(UUID uuid, long date, FlowEnums.Type type, FlowEnums.State state, UUID owner) {
        this.uuid = uuid;
        this.date = date;
        this.type = type;
        this.state = state;
        this.owner = owner;
    }

    public void save() throws SQLException {
        Cache.FLOW_SERVICE.saveFlow(this);
    }

    public Messdiener getPerson() throws SQLException {
        return Cache.MESSDIENER_SERVICE.find(owner).orElseThrow();
    }

    public CashFlow getCashFlow() throws SQLException {
        return Cache.FLOW_SERVICE.getCashFlowByUUID(uuid).orElseThrow();
    }

    public String getDateString(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getDate() {
        return date;
    }

    public FlowEnums.Type getType() {
        return type;
    }

    public FlowEnums.State getState() {
        return state;
    }

    public UUID getOwner() {
        return owner;
    }

    public Messdiener getOwner_Person() throws SQLException {
        return Cache.MESSDIENER_SERVICE.find(owner).orElseThrow();
    }
}