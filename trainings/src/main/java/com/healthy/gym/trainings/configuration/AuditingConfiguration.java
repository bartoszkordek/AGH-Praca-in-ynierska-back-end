package com.healthy.gym.trainings.configuration;

import com.healthy.gym.trainings.component.UserAuditor;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.UserDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class AuditingConfiguration {

    @Bean
    public AuditorAware<UserDocument> getAuditorAwareProvider(UserDAO userDAO) {
        return new UserAuditor(userDAO);
    }
}
