package de.messdiener.cms.app.entities.flow.sub;

import de.messdiener.cms.web.utils.DateUtils;

public class CashFlowRow {

    private int id;
    private String englishDate;
    private String bezeichnung;
    private double value;

    public CashFlowRow(){}

    public String getGermanDate(){
        long millis = DateUtils.convertDateToLong(englishDate, DateUtils.DateType.ENGLISH);
        return DateUtils.convertLongToDate(millis, DateUtils.DateType.GERMAN);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEnglishDate(String englishDate) {
        this.englishDate = englishDate;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getEnglishDate() {
        return englishDate;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public double getValue() {
        return value;
    }
}
