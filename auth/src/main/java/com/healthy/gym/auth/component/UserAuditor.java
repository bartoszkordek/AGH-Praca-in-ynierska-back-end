package com.healthy.gym.auth.component;

import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuditor implements AuditorAware<UserDocument> {

    private final UserDAO userDAO;
    private final Environment environment;

    @Autowired
    public UserAuditor(UserDAO userDAO, Environment environment) {
        this.userDAO = userDAO;
        this.environment = environment;
    }

    @Override
    public Optional<UserDocument> getCurrentAuditor() {
        UserDocument user = userDAO.findByEmail("root");
        if (user == null) {
            user = createRoot();
        }

        return Optional.of(user);
    }

    private UserDocument createRoot() {
        String name = environment.getProperty("system.name");
        var root = new UserDocument(name, "", "root");
        return userDAO.save(root);
    }

}
