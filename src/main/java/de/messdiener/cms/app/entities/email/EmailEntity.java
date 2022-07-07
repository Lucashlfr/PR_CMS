package de.messdiener.cms.app.entities.email;

import de.messdiener.cms.cache.Cache;

public class EmailEntity {

    private String reciver;
    private String topic;
    private String text;
    private String link;

    public EmailEntity(String reciver, String topic, String text, String link) {
        this.reciver = reciver;
        this.topic = topic;
        this.text = text;
        this.link = link;
    }

    public static EmailEntity generateNew(String reciver, String topic, String text, String link){
        return new EmailEntity(reciver, topic, text, link);
    }

    public void send(){
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String generateMail() {
        return getText() + "\n" + getLink();
    }
}
