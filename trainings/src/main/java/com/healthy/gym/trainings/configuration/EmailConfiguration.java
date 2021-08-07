package com.healthy.gym.trainings.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:email.properties", ignoreResourceNotFound = true)
public class EmailConfiguration {

    @Value("${spring.mail.username:username}")
    private String emailName;

    @Value("${spring.mail.password:password}")
    private String emailPassword;

    @Value("${spring.mail.personal:personal}")
    private String emailPersonal;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${spring.mail.port:587}")
    private String smtpPort;

    public String getEmailName() {
        return emailName;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public String getEmailPersonal() {
        return emailPersonal;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }
}
