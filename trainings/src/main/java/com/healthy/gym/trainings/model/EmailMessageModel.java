package com.healthy.gym.trainings.model;

import java.util.List;

public class EmailMessageModel {

    private List<String> toEmails;
    private String subject;
    private String body;
    private String filePath;

    public EmailMessageModel(List<String> toEmails,
                          String subject,
                          String body,
                          String filePath){

        this.toEmails = toEmails;
        this.subject = subject;
        this.body = body;
        this.filePath = filePath;
    }

    public List<String> getToEmails() {
        return toEmails;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setToEmails(List<String> toEmails) {
        this.toEmails = toEmails;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
