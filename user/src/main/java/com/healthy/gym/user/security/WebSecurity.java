package com.healthy.gym.user.security;

import com.healthy.gym.user.component.HttpHeaderParser;
import com.healthy.gym.user.component.TokenValidator;
import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Profile("production")
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Translator translator;
    private final RedisLogoutHandler redisLogoutHandler;
    private final TokenValidator tokenValidator;
    private final HttpHeaderParser headerParser;
    private final TokenManager tokenManager;

    @Autowired
    public WebSecurity(
            UserService userService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            Translator translator,
            RedisLogoutHandler redisLogoutHandler,
            TokenValidator tokenValidator,
            HttpHeaderParser headerParser,
            TokenManager tokenManager
    ) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.translator = translator;
        this.redisLogoutHandler = redisLogoutHandler;
        this.tokenValidator = tokenValidator;
        this.headerParser = headerParser;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().disable();

        http.authorizeRequests().antMatchers("/users/status").authenticated()
                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .addFilter(getJWTAuthenticationFilter())
                .addFilter(getAuthenticationFilter())
                .logout()
                .logoutSuccessHandler(redisLogoutHandler)
                .permitAll();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        var authenticationFilter =
                new AuthenticationFilter(userService, translator, tokenManager);
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    private JWTAuthenticationFilter getJWTAuthenticationFilter() throws Exception {
        return new JWTAuthenticationFilter(authenticationManager(), tokenValidator, headerParser, tokenManager);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
