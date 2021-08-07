package com.healthy.gym.trainings.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource(value = "classpath:email.properties", ignoreResourceNotFound = true)
public class EmailConfiguration {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private String mailPort;

    @Value("${spring.mail.username:username}")
    private String mailUsername;

    @Value("${spring.mail.password:password}")
    private String mailPassword;

    @Value("${spring.mail.personal:personal}")
    private String emailPersonal;

    public String getMailUsername() {
        return mailUsername;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public String getEmailPersonal() {
        return emailPersonal;
    }

    public String getMailHost() {
        return mailHost;
    }

    public String getMailPort() {
        return mailPort;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(Integer.parseInt(mailPort));
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "3000");
        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.smtp.ssl.trust", mailHost);

        return mailSender;
    }
}
