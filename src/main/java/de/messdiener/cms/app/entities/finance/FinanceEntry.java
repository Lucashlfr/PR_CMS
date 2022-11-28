package de.messdiener.cms.app.entities.finance;

import de.messdiener.cms.web.utils.DateUtils;

import java.util.UUID;

public class FinanceEntry {

    private final UUID uuid;
    private final UUID oGroup;
    private final long date;
    private final double revenue;
    private final double expenditures;
    private final String type;
    private final String editor;
    private final String costCenter;
    private final String note;

    public FinanceEntry(UUID uuid, UUID oGroup, long date, double revenue, double expenditures, String type, String editor, String costCenter, String note) {
        this.uuid = uuid;
        this.oGroup = oGroup;
        this.date = date;
        this.revenue = revenue;
        this.expenditures = expenditures;
        this.type = type;
        this.editor = editor;
        this.costCenter = costCenter;
        this.note = note;
    }

    public static FinanceEntry of(UUID uuid,UUID oGroup, long date, double revenue, double expenditures, String type, String editor, String costCenter, String note){
        return new FinanceEntry(uuid, oGroup, date, revenue, expenditures,type, editor, costCenter, note);
    }

    public static FinanceEntry empty() {
        return of(UUID.randomUUID(), UUID.randomUUID(), System.currentTimeMillis(), 0, 0, "", "", "", "");
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getDate() {
        return date;
    }

    public double getRevenue() {
        return revenue;
    }

    public double getExpenditures() {
        return expenditures;
    }

    public String getEditor() {
        return editor;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public String getNote() {
        return note;
    }

    public String getType() {
        return type;
    }

    public String getGermandate(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

    public boolean noteIsLink(){
        return getNote().startsWith("https://") || getNote().startsWith("http://");
    }

    public UUID getoGroup() {
        return oGroup;
    }

    public double getValue() {
        return getRevenue() - getExpenditures();
    }
}
