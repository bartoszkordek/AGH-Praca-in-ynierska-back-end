package com.healthy.gym.trainings.model.other;

import java.util.List;
import java.util.Objects;

public class EmailSendModel {

    private String fromEmail;
    private String personal;
    private List<String> toEmails;
    private String password;
    private String subject;
    private String body;
    private String filePath;

    public EmailSendModel(
            String fromEmail,
            String personal,
            List<String> toEmails,
            String password,
            String subject,
            String body,
            String filePath
    ) {
        this.fromEmail = fromEmail;
        this.personal = personal;
        this.toEmails = toEmails;
        this.password = password;
        this.subject = subject;
        this.body = body;
        this.filePath = filePath;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public List<String> getToEmails() {
        return toEmails;
    }

    public void setToEmails(List<String> toEmails) {
        this.toEmails = toEmails;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailSendModel that = (EmailSendModel) o;
        return Objects.equals(fromEmail, that.fromEmail)
                && Objects.equals(personal, that.personal)
                && Objects.equals(toEmails, that.toEmails)
                && Objects.equals(password, that.password)
                && Objects.equals(subject, that.subject)
                && Objects.equals(body, that.body)
                && Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromEmail, personal, toEmails, password, subject, body, filePath);
    }

    @Override
    public String toString() {
        return "EmailSendModel{" +
                "fromEmail='" + fromEmail + '\'' +
                ", personal='" + personal + '\'' +
                ", toEmails=" + toEmails +
                ", password='" + password + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
