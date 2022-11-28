package de.messdiener.cms.app.entities.file;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Utils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileEntity {

    private UUID uuid;
    private String owner;

    private String name;
    private String type;

    private byte[] data;

    private long date;

    public FileEntity(UUID uuid, String owner, String name, String type, long date, byte[] data) {
        this.uuid = uuid;
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.data = data;
        this.date = date;
    }

    public static FileEntity convert(MultipartFile multipartFile, String owner) throws IOException {

        if (multipartFile.isEmpty() || multipartFile.getSize() == 0) {
            throw new IOException();
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        return new FileEntity(UUID.randomUUID(), owner, fileName, multipartFile.getContentType(), System.currentTimeMillis(), multipartFile.getBytes());
    }

    public MediaType getMediaType() {
        return MediaType.valueOf(getType());
    }

    public String getGermanDate() {
        return DateUtils.convertLongToDate(getDate(), DateUtils.DateType.GERMAN);
    }

    public String getOwner_read() throws SQLException {
        if(!Utils.isUUID(owner)){
            return owner;
        }
        Optional<User> user = Cache.USER_SERVICE.getUser(UUID.fromString(owner));

        if(user.isEmpty())return owner;

        return user.get().getNameString();
    }

    public File getFile() throws IOException {
        File file = new File(name);
        file.createNewFile();

        OutputStream os = new FileOutputStream(file);
        os.write(data);

        return file;
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.save(this);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "uuid=" + uuid +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", data=" + Arrays.toString(data) +
                ", date=" + date +
                '}';
    }


}
