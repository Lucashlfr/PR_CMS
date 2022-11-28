package de.messdiener.cms.app.entities.email;

import de.messdiener.cms.app.services.mail.utils.MailOverlay;
import de.messdiener.cms.cache.Cache;

public class EmailEntity {

    String reciver;
    private String topic;
    private String html;

    public EmailEntity(String reciver, String topic, String html) {
        this.reciver = reciver;
        this.topic = topic;
        this.html = html;
    }

    public static EmailEntity generateNew(String reciver, String topic, MailOverlay mailOverlay){
        return new EmailEntity(reciver, topic, mailOverlay.getHTML());
    }

    public void send() throws Exception {
        Cache.EMAIL_SERVICE.sendMail(this);
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }
}
