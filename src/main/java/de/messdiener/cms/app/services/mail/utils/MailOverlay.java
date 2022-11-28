package de.messdiener.cms.app.services.mail.utils;

public class MailOverlay {

    private final StringBuilder stringBuilder;

    private MailOverlay(){
        this.stringBuilder = new StringBuilder();
        stringBuilder.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'");
        stringBuilder.append("'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
        stringBuilder.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
        stringBuilder.append("<head>");
        stringBuilder.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        stringBuilder.append("<title>Demystifying Email Design</title>");
        stringBuilder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
        stringBuilder.append("<style>");
        stringBuilder.append("* {");
        stringBuilder.append("font-family: Arial,Serif;");
        stringBuilder.append("}");
        stringBuilder.append("</style>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body style='margin: 0; padding: 0;'>");
        stringBuilder.append("<br>");
        stringBuilder.append("<table align='center'  cellpadding='0' cellspacing='0'style='max-width: 600px' >");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td align='center' bgcolor='#3a557c' style='padding: 40px 0 30px 0;'>");
        stringBuilder.append("<img src='https://messdiener-knittelsheim.de/wp-content/uploads/2022/11/process.png' style='display: block; max-height: 100px; max-width: 100px;'/>");
        stringBuilder.append("<h1 style='color: white'>Management System der Pfarrei Bellheim</h1>");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>");
        stringBuilder.append("<table cellpadding='0' cellspacing='0' width='100%'>");
    }

    public static MailOverlay generate(){
        return new MailOverlay();
    }

    public MailOverlay addGreeting(String text){
        stringBuilder.append("<tr>");
        stringBuilder.append("<td>");
        stringBuilder.append(text);
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");

        return this;
    }

    public MailOverlay addText(String text){
        stringBuilder.append("<tr>");
        stringBuilder.append("<td style='padding: 20px 0 30px 0;'>");
        stringBuilder.append(text);
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");

        return this;
    }

    public MailOverlay addLink(String text, String url){
        stringBuilder.append("<tr>");
        stringBuilder.append("<td align='center' style='padding: 0px 0 30px 0;'>");
        stringBuilder.append("<a href='").append(url).append("'>").append(text).append("</a>");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");

        return this;
    }

    public MailOverlay addAdoption_Lucas(){

        stringBuilder.append("<tr>");
        stringBuilder.append("<td>");
        stringBuilder.append("Viele Gr&uuml;&szlig;e <br><br><b>i.A. Lucas Helfer</b><br>Content Manager | Pfarei Bellheim<br><br>Pfarrei Hl. Hildegard v. Bingen<br>Hintere Stra&szlig;e 1, 76756 Bellheim<br>Tel. 07272/973050");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");

        return this;
    }

    public MailOverlay addAdoption_System(){

        stringBuilder.append("<tr>");
        stringBuilder.append("<td>");
        stringBuilder.append("Viele Gr&uuml;&szlig;e <br><br>Das Management System <br>Pfarrei Hl. Hildegard v. Bingen");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");

        return this;
    }

    public String getHTML(){
        stringBuilder.append("</table>");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td align='center' style='padding: 20px 0 20px 0; font-style: italic; font-size: 10px'>");
        stringBuilder.append("Diese Nachricht wurde aus unserem System automatisch generiert. Bitte antworten Sie nicht auf diese Nachricht. Bei Fragen wenden Sie sich bitte an Lucas Helfer.");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td align='center'>");
        stringBuilder.append("<img style='max-width: 100px; width: 100px; margin-top: 10px' src='https://messdiener-knittelsheim.de/wp-content/uploads/2022/07/PFARREI_HL_HILDEGARD_Logo_KLEINER.jpg'>");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");

        stringBuilder.append("</table>");
        stringBuilder.append("<br>");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }


}
