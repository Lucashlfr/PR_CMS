package de.messdiener.cms.app.entities.finance;

import de.messdiener.cms.web.utils.DateUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class FinanceEntry {

    private final UUID id;
    private final UUID oGroup;
    private final long date;
    private final double revenue;
    private final double expenditures;
    private final String type;
    private final String editor;
    private final String costCenter;
    private final String note;

    public static FinanceEntry of(UUID uuid,UUID oGroup, long date, double revenue, double expenditures, String type, String editor, String costCenter, String note){
        return new FinanceEntry(uuid, oGroup, date, revenue, expenditures,type, editor, costCenter, note);
    }

    public static FinanceEntry empty() {
        return of(UUID.randomUUID(), UUID.randomUUID(), System.currentTimeMillis(), 0, 0, "", "", "", "");
    }


    public String getGermandate(){
        return DateUtils.convertLongToDate(date, DateUtils.DateType.GERMAN);
    }

    public boolean noteIsLink(){
        return getNote().startsWith("https://") || getNote().startsWith("http://");
    }

    public double getValue() {
        return getRevenue() - getExpenditures();
    }
}
