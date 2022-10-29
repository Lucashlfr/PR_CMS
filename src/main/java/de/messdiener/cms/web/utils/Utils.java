package de.messdiener.cms.web.utils;

import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class Utils {

    public static String getMailClose(){
        return "Viele Grüße \nfür das Communications-Team\n" + SecurityHelper.getUsername();
    }

    public static ResponseEntity<byte[]> createFile(MultipartFile multipartFile, UUID uuid) throws IOException {
        String fileName = uuid.toString();
        byte[] data = multipartFile.getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".html" + "\"")
                .body(data);
    }

}
