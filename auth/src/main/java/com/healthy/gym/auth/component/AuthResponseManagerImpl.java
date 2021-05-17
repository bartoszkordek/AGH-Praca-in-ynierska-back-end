package com.healthy.gym.auth.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.auth.pojo.response.LogoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthResponseManagerImpl implements AuthResponseManager {

    private final Translator translator;

    @Autowired
    public AuthResponseManagerImpl(Translator translator) {
        this.translator = translator;
    }

    @Override
    public void handleTokenExpiredLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.token.expired");
        Map<String, String> errors = new HashMap<>();

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, true));

        handleResponse(response, HttpStatus.OK, bodyAsString);
    }

    @Override
    public void handleSuccessfulLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.success");
        Map<String, String> errors = new HashMap<>();

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, true));

        handleResponse(response, HttpStatus.OK, bodyAsString);
    }

    @Override
    public void handleUnsuccessfulLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.fail");
        Map<String, String> errors = new HashMap<>();
        errors.put("token", translator.toLocale("user.logout.invalid.token"));

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, false));

        handleResponse(response, HttpStatus.UNAUTHORIZED, bodyAsString);
    }

    private void handleResponse(HttpServletResponse response, HttpStatus status, String body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setLocale(LocaleContextHolder.getLocale());
        response.getWriter().println(body);
    }
}
