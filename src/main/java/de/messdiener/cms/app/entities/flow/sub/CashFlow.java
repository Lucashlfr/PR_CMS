package de.messdiener.cms.app.entities.flow.sub;

import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.security.SecurityHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class CashFlow {

    private UUID id;
    private UUID personUUID;
    private UUID fileUUID;
    private String cardOwner;
    private String mail;
    private String iban;
    private String bic;
    private String data;
    private String zweck;

    public void create(MultipartFile file) throws SQLException, IOException {

        FileEntity fileEntity = FileEntity.convert(file, SecurityHelper.getUser().getUserID().toString());
        fileEntity.setName("flow_" + id+ "_" + fileEntity.getName());
        fileEntity.setOwner(Cache.SYSTEM_USER.getUserID().toString());
        fileEntity.setId(fileUUID);

        Cache.CLOUD_SERVICE.save(fileEntity);
        Cache.FLOW_SERVICE.saveCashFlow(this);
    }

    public List<CashFlowRow> getDataAsArray(){
        List<CashFlowRow> list = new ArrayList<>();

        String[] listData = data.split(";#");
        for(String s : listData){
            String[] row = s.split(";");

            CashFlowRow cashFlowRow = new CashFlowRow();
            cashFlowRow.setId(Integer.parseInt(row[0]));
            cashFlowRow.setEnglishDate(row[1]);
            cashFlowRow.setBezeichnung(row[2]);
            cashFlowRow.setValue(Double.parseDouble(row[3].replace("€","")));

            list.add(cashFlowRow);
        }
        return list;
    }

    public double getRevenue() {
        return 0;
    }

    public double getExpenditures() {
        return getSumm();
    }

    public double getSumm(){
        double summ = 0;
        for(CashFlowRow row : getDataAsArray()){
            summ += row.getValue();
        }

        return summ;
    }

    public FileEntity getFile() throws SQLException {
        return Cache.CLOUD_SERVICE.get(fileUUID.toString());
    }

}
