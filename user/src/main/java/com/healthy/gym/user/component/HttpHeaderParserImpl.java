package com.healthy.gym.user.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class HttpHeaderParserImpl implements HttpHeaderParser {

    private final Environment environment;
    private final TokenValidator tokenValidator;

    @Autowired
    public HttpHeaderParserImpl(Environment environment, TokenValidator tokenValidator) {
        this.environment = environment;
        this.tokenValidator = tokenValidator;
    }

    @Override
    public String getAuthenticationToken(HttpServletRequest request) {

        String headerName = getHeaderName();
        String tokenPrefix = getTokenPrefix();

        String token = request.getHeader(headerName);

        if (token == null || !token.startsWith(tokenPrefix)) {
            return null;
        }

        return tokenValidator.purifyToken(token,tokenPrefix);
    }

    private String getHeaderName() {
        return environment.getRequiredProperty("authorization.token.header.name");
    }

    private String getTokenPrefix() {
        return environment.getRequiredProperty("authorization.token.header.prefix");
    }
}
