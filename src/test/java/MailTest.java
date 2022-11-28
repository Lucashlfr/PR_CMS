import de.messdiener.cms.app.entities.email.EmailEntity;
import de.messdiener.cms.app.services.mail.utils.EmailUtils;
import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import de.messdiener.cms.web.utils.Utils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;


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
        String text = "Dieser Test wurde beim Bauen der JAR Automatisch gestartet";

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

        email.send();
    }

    @Test
    void name() throws Exception {
        EmailUtils.sendEmail(EmailEntity.generateNew("lucas.helfer@gmx.net", "Test", MailOverlay.generate()
                .addGreeting("Hallo Owner")
                .addText("Test")
                .addAdoption_Lucas()));
    }

    @Test
    void nam1e() {
        System.out.println(Utils.isUUID("X"));
    }
}
