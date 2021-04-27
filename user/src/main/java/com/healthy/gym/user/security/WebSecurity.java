package com.healthy.gym.user.security;

import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
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
    private final Environment environment;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Translator translator;

    @Autowired
    public WebSecurity(
            UserService userService,
            Environment environment,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            Translator translator
    ) {
        this.userService = userService;
        this.environment = environment;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.translator = translator;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().disable();

        http.authorizeRequests().antMatchers("/users/status").authenticated()
                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), environment))
                .addFilter(getAuthenticationFilter());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(userService, environment, translator);
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
