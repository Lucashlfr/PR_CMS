package de.messdiener.cms.app.entities.flow.sub;

import de.messdiener.cms.web.utils.DateUtils;
import lombok.Data;

@Data
public class CashFlowRow {

    private int id;
    private String englishDate;
    private String bezeichnung;
    private double value;

    public String getGermanDate(){
        long millis = DateUtils.convertDateToLong(englishDate, DateUtils.DateType.ENGLISH);
        return DateUtils.convertLongToDate(millis, DateUtils.DateType.GERMAN);
    }

}
