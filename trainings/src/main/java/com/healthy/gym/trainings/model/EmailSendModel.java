package com.healthy.gym.trainings.model;

public class EmailSendModel {

    private String fromEmail;
    private String personal;
    private String toEmail;
    private String password;
    private String subject;
    private String body;
    private String filePath;

    public EmailSendModel(String fromEmail,
                          String personal,
                          String toEmail,
                          String password,
                          String subject,
                          String body,
                          String filePath){

        this.fromEmail = fromEmail;
        this.personal = personal;
        this.toEmail = toEmail;
        this.password = password;
        this.subject = subject;
        this.body = body;
        this.filePath = filePath;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getPersonal() {
        return personal;
    }

    public String getToEmail() {
        return toEmail;
    }

    public String getPassword() {
        return password;
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

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public void setPassword(String password) {
        this.password = password;
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
