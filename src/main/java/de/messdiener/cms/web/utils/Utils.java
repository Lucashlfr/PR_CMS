package de.messdiener.cms.web.utils;

import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class Utils {

    public static String getMailClose() {
        return "Viele Grüße \nfür das Communications-Team\n" + SecurityHelper.getUsername();
    }

    public static ResponseEntity<byte[]> createFile(MultipartFile multipartFile, UUID uuid) throws IOException {
        String fileName = uuid.toString();
        byte[] data = multipartFile.getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".html" + "\"")
                .body(data);
    }

    public static boolean isUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static List<List<String>> importFromCSV(MultipartFile file) throws SQLException, IOException, IllegalAccessException {

        if (file.getSize() > 1048576) {
            throw new IllegalAccessException();
        }

        FileEntity fileEntity = FileEntity.convert(file, "GAST");

        if (!fileEntity.getName().endsWith(".csv")) {
            throw new IllegalAccessException("");
        }

        List<List<String>> records = new ArrayList<>();

        File outputFile = File.createTempFile("import", "csv");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(fileEntity.getData());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                records.add(Arrays.asList(values));
            }
        }
        outputFile.delete();

        return records;

    }
}
