package de.messdiener.cms.app.services.mail.utils;

public class MailOverlay {

    public static String generate(String text, String link){


        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<div style='background-color: lightgray; text-align: center; max-width: 440px;'>");

        stringBuilder.append("<div style='background-color: white; '>");
        stringBuilder.append("<img src='https://messdiener-knittelsheim.de/wp-content/uploads/2022/07/PFARREI_HL_HILDEGARD_Logo_KLEINER.jpg' style='max-width: 250px; width: 250px; margin-top: 10px'>");

        stringBuilder.append("<h1 style='font-family: Arial'>");
        stringBuilder.append("<div style=''>CMS der Pfarrei Bellheim</div>");
        stringBuilder.append("</h1>");
        stringBuilder.append("<div style='margin: 15px'>");
        stringBuilder.append("<p style='font-family: Arial; text-align: justify; margin: 7px'>");
        stringBuilder.append(text);
        stringBuilder.append("</p>");
        stringBuilder.append("<br/>");
        stringBuilder.append("<a style='font-family: Arial'' href='");
        stringBuilder.append(link);
        stringBuilder.append("'><h1 style=''>Weitere informationen</h1></a>");
        stringBuilder.append("</div>");

        return stringBuilder.toString();


    }

}
