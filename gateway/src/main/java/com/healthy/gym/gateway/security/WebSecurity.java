package com.healthy.gym.gateway.security;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment environment;

    @Autowired
    public WebSecurity(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
        http.headers().frameOptions().disable();

        http.authorizeRequests()
                .antMatchers("/user-service/users/status").authenticated()
                .antMatchers("/user-service/**").permitAll()
                .and()
                .addFilter(new AuthenticationFilter(authenticationManager(), environment));

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(ImmutableList.of("*"));
        corsConfiguration.setAllowedHeaders(ImmutableList.of("*"));
        corsConfiguration.setAllowedMethods(ImmutableList.of("*"));
        corsConfiguration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}