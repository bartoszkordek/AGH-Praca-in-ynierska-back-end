package com.healthy.gym.trainings.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EmailConfig {

    @Autowired
    private Environment environment;

    public String getEmailName(){
        return environment.getProperty("microservice.email.name");
    };

    public String getEmailPassword(){
        return environment.getProperty("microservice.email.password");
    }

    public String getEmailPersonal(){
        return environment.getProperty("microservice.email.personal");
    }

    public String getSmtpHost(){
        return environment.getProperty("microservice.smtp.host");
    }

    public String getSmtpPort(){
        return environment.getProperty("microservice.smtp.port");
    }
}
