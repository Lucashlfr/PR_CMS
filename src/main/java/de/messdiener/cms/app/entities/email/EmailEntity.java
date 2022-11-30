package de.messdiener.cms.app.entities.email;

import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import de.messdiener.cms.cache.Cache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class EmailEntity {

    private String receiver;
    private String topic;
    private String html;

    public static EmailEntity generateNew(String receiver, String topic, MailOverlay mailOverlay){
        return new EmailEntity(receiver, topic, mailOverlay.getHTML());
    }

    public void send() throws Exception {
        Cache.EMAIL_SERVICE.sendMail(this);
    }

}
