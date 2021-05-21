package com.healthy.gym.auth;

import com.healthy.gym.auth.component.Translator;
import com.healthy.gym.auth.controller.UserController;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import com.healthy.gym.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class AuthApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserController userController;

    @Autowired
    private Environment environment;

    @Test
    @WithMockUser
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @Test
    void shouldReturnComponentsInTheCurrentScope() {
        assertThat(applicationContext.getBean(UserController.class)).isNotNull();
        assertThat(applicationContext.getBean(Translator.class)).isNotNull();
        assertThat(applicationContext.getBean(UserService.class)).isNotNull();
        assertThat(applicationContext.getBean(UserDAO.class)).isNotNull();
        assertThat(applicationContext.getBean(BCryptPasswordEncoder.class)).isNotNull();
    }

    @Nested
    class ApplicationProperties {

        @BeforeEach
        void setUp() {
        }

        @Test
        void shouldHaveSpringApplicationName() {
            assertThat(environment.getProperty("spring.application.name")).isNotNull();
        }

        @Test
        void shouldHaveSpringDevToolsRestartEnabled() {
            assertThat(environment.getProperty("spring.devtools.restart.enabled")).isNotNull();
        }

        @Test
        void shouldHaveSpringDataSourceUsername() {
            assertThat(environment.getProperty("spring.datasource.username")).isNotNull();
        }

        @Test
        void shouldHaveSpringDataSourcePassword() {
            assertThat(environment.getProperty("spring.datasource.password")).isNotNull();
        }

        @Test
        void shouldHaveSpringDataSourceUrl() {
            assertThat(environment.getProperty("spring.datasource.url")).isNotNull();
        }

        @Test
        void shouldHaveSpringH2ConsoleEnabled() {
            assertThat(environment.getProperty("spring.h2.console.enabled")).isNotNull();
        }

        @Test
        void shouldHaveSpringH2ConsolePath() {
            assertThat(environment.getProperty("spring.h2.console.path")).isNotNull();
        }

        @Test
        void shouldHaveSpringH2ConsoleWebAllowOthers() {
            assertThat(environment.getProperty("spring.h2.console.settings.web-allow-others")).isNotNull();
        }

        @Test
        void shouldHaveSpringH2JpaDatabasePlatform() {
            assertThat(environment.getProperty("spring.h2.jpa.database-platform")).isNotNull();
        }

        @Test
        void shouldHaveSpringProfilesActive() {
            assertThat(environment.getProperty("spring.profiles.active")).isNotNull();
        }

        @Test
        void shouldHaveSpringRedisDatabase() {
            assertThat(environment.getProperty("spring.redis.database")).isNotNull();
        }

        @Test
        void shouldHaveSpringRedisHost() {
            assertThat(environment.getProperty("spring.redis.host")).isNotNull();
        }

        @Test
        void shouldHaveSpringRedisPort() {
            assertThat(environment.getProperty("spring.redis.port")).isNotNull();
        }

        @Test
        void shouldHaveSpringRedisPassword() {
            assertThat(environment.getProperty("spring.redis.password")).isNotNull();
        }

        @Test
        void shouldHaveSpringRedisTimeout() {
            assertThat(environment.getProperty("spring.redis.timeout")).isNotNull();
        }

        @Test
        void shouldHaveServerPort() {
            assertThat(environment.getProperty("server.port"))
                    .isNotNull()
                    .isEqualTo("0");
        }

        @Test
        void shouldHaveEurekaClientServiceUrlDefaultZone() {
            assertThat(environment.getProperty("eureka.client.service-url.defaultZone")).isNotNull();
        }

        @Test
        void shouldHaveEurekaInstanceInstanceId() {
            assertThat(environment.getProperty("eureka.instance.instance-id")).isNotNull();
        }

        @Test
        void shouldHaveTokenSecret() {
            assertThat(environment.getProperty("token.secret")).isNotNull();
        }

        @Test
        void shouldHaveTokenExpirationTime() {
            assertThat(environment.getProperty("token.expiration-time")).isNotNull();
        }

        @Test
        void shouldHaveAuthorizationTokenHeaderName() {
            assertThat(environment.getProperty("authorization.token.header.name"))
                    .isNotNull()
                    .isEqualTo("Authorization");
        }

        @Test
        void shouldHaveAuthorizationTokenHeaderPrefix() {
            assertThat(environment.getProperty("authorization.token.header.prefix"))
                    .isNotNull()
                    .isEqualTo("Bearer");
        }
    }
}
