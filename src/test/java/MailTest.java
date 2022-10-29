import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.jupiter.api.Test;


public class MailTest {

    @Test
    void mail() throws EmailException {

        String mailserver = "smtp.strato.de";
        String username = "system@cms.code.system.etu.messdiener.com";
        String password = "iPiw2A3YV96E0B6QrFKw9Kbt3nINNtX89XNfaEfk";
        String absender = "system@cms.code.system.etu.messdiener.com";
        String empfaenger = "lucas.helfer@gmx.net";
        String textCharset = "UTF-8";
        String betreff = "Test";
        String text = "Test";

        assert username != null && password != null;
        MultiPartEmail email = new MultiPartEmail();
        email.setAuthenticator(new DefaultAuthenticator(username, password));

        email.setStartTLSEnabled(true);
        email.setHostName(mailserver);
        email.setFrom(absender);
        email.addTo(empfaenger);
        email.setCharset(textCharset);
        email.setSubject(betreff);
        email.setMsg(text);

        // email.send();
    }

    @Test
    void name() {
        //  EmailUtils.sendEmail(new EmailEntity("lucas.helfer@gmx.net", "Test", "Test", "x.de"));
    }
}
