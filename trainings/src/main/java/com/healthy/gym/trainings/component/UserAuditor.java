package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuditor implements AuditorAware<UserDocument> {

    private final UserDAO userDAO;

    @Autowired
    public UserAuditor(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<UserDocument> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = (String) authentication.getPrincipal();
        if (userId == null) throw new AccessDeniedException("Invalid user");

        UserDocument user = userDAO.findByUserId(userId);
        if (user == null) throw new AccessDeniedException("Invalid user");

        return Optional.of(user);
    }
}
