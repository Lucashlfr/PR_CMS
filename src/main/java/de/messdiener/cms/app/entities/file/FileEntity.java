package de.messdiener.cms.app.entities.file;

import de.messdiener.cms.app.entities.user.User;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.utils.DateUtils;
import de.messdiener.cms.web.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FileEntity {

    private UUID id;
    private String owner;
    private String name;
    private String type;
    private long date;
    private byte[] data;

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

    public String getOwnerRead() throws SQLException {
        if(!Utils.isUUID(owner)){
            return owner;
        }
        Optional<User> user = Cache.USER_SERVICE.getUser(UUID.fromString(owner));

        if(user.isEmpty())return owner;

        return user.get().getNameString();
    }

    public File getFile() throws IOException {


        Path path = Path.of(name);
        File file = path.toFile();

        OutputStream os = new FileOutputStream(file);
        os.write(data);

        return file;
    }

    public void save() throws SQLException {
        Cache.CLOUD_SERVICE.save(this);
    }

}
