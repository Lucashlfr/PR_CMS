package de.messdiener.cms.app.entities.finance;

import de.messdiener.cms.cache.Cache;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FinanceIndex {

    private UUID oGroup;
    private List<FinanceEntry> entities;
    
    public FinanceIndex(UUID group) throws SQLException {
        this.oGroup = group;
        this.entities = Cache.FINANCE_SERVICE.getEntities(group);
    }

    public Optional<FinanceEntry> getIntial() throws SQLException {
        if(entities.size() == 0)return Optional.empty();
        return Optional.ofNullable(entities.get(0));
    }

    public double getIntialvalue() throws SQLException {
        return getIntial().map(financeEntry -> 0 + financeEntry.getRevenue() - financeEntry.getExpenditures()).orElse(0.0);
    }

    public double getValue() throws SQLException {
        double value = 0;

        for (FinanceEntry financeEntry : entities) {
            value = value + financeEntry.getRevenue() - financeEntry.getExpenditures();
        }

        return round(value);
    }

    public double getRevenues() throws SQLException {
        double revenue = 0;
        for(FinanceEntry financeEntry : entities){
            if(financeEntry.getRevenue() > 0){
                revenue += financeEntry.getRevenue();
            }
        }
        return round(revenue);
    }



    public double getExpenditures() throws SQLException {
        double expenditures = 0;
        for(FinanceEntry financeEntry : entities){
            if(financeEntry.getExpenditures() > 0){
                expenditures -= financeEntry.getExpenditures();
            }
        }
        return round(expenditures);
    }

    public double round(double d){
        DecimalFormat df = new DecimalFormat("#.####");
        return Double.parseDouble(df.format(d).replace(",","."));
    }

    public String getDates() throws SQLException {
        StringBuilder export = new StringBuilder();
        for(FinanceEntry financeEntry : entities){
            export.append(financeEntry.getGermandate()).append(",");
        }
        export.append("#");
        return export.toString().replace(",#","");
    }

    public String getCashData() throws SQLException {
        StringBuilder export = new StringBuilder();
        double d = 0;
        boolean b = false;
        for(FinanceEntry financeEntry : entities){
            if(!b){
                d = financeEntry.getValue();
                export.append(d).append(",");
                b = true;
                continue;
            }
            d += financeEntry.getValue();
            export.append(d).append(",");
        }
        export.append("#");
        return export.toString().replace(",#","");
    }

}
