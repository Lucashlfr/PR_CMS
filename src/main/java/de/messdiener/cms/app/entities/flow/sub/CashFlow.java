package de.messdiener.cms.app.entities.flow.sub;

import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CashFlow {

    private UUID uuid;
    private UUID person_uuid;
    private UUID file_uuid;

    private String card_owner;
    private String mail;
    private String iban;
    private String bic;
    private String data;
    private String zweck;


    public CashFlow(UUID uuid, UUID person_uuid, String card_owner, String mail, String iban, String bic, String data, String zweck, UUID file_uuid) {
        this.uuid = uuid;
        this.person_uuid = person_uuid;
        this.card_owner = card_owner;
        this.mail = mail;
        this.iban = iban;
        this.bic = bic;
        this.data = data;
        this.file_uuid = file_uuid;
        this.zweck = zweck;
    }

    public void create(MultipartFile file) throws SQLException, IOException {

        FileEntity fileEntity = FileEntity.convert(file, SecurityHelper.getUser().getUser_UUID().toString());
        fileEntity.setName("flow_" + uuid+ "_" + fileEntity.getName());
        fileEntity.setOwner(Cache.SYSTEM_USER.getUser_UUID().toString());
        fileEntity.setUuid(file_uuid);

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
        return Cache.CLOUD_SERVICE.get(file_uuid.toString());
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getPerson_uuid() {
        return person_uuid;
    }

    public void setPerson_uuid(UUID person_uuid) {
        this.person_uuid = person_uuid;
    }

    public String getCard_owner() {
        return card_owner;
    }

    public void setCard_owner(String card_owner) {
        this.card_owner = card_owner;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFile_uuid(UUID file_uuid) {
        this.file_uuid = file_uuid;
    }

    public UUID getFile_uuid() {
        return file_uuid;
    }

    public void setZweck(String zweck) {
        this.zweck = zweck;
    }

    public String getZweck() {
        return zweck;
    }


}
