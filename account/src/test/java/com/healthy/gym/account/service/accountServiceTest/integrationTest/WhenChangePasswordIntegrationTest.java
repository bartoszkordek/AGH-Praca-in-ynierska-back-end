package com.healthy.gym.account.service.accountServiceTest.integrationTest;

import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.dto.UserDTO;
import com.healthy.gym.account.enums.GymRole;
import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class WhenChangePasswordIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        String encryptedPassword = bCryptPasswordEncoder.encode("password4576");
        UserDocument andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                encryptedPassword,
                userId
        );
        Collection<GymRole> gymRoles = new HashSet<>();
        gymRoles.add(GymRole.USER);
        andrzejNowak.setGymRoles(gymRoles);

        mongoTemplate.save(andrzejNowak);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        String randomId = UUID.randomUUID().toString();
        assertThatThrownBy(
                () -> accountService.changePassword(randomId, "password4576", "password45768")
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenOldPasswordDoesNotMatch() {
        assertThatThrownBy(
                () -> accountService.changePassword(userId, "password457", "password4576")
        ).isInstanceOf(OldPasswordDoesNotMatchException.class);
    }

    @Test
    void shouldThrowExceptionWhenNewPasswordIsEqualToOldPassword() {
        assertThatThrownBy(
                () -> accountService.changePassword(userId, "password4576", "password4576")
        ).isInstanceOf(IdenticalOldAndNewPasswordException.class);
    }

    @Test
    void shouldUpdatePassword()
            throws OldPasswordDoesNotMatchException, IdenticalOldAndNewPasswordException {
        UserDTO userDTOUpdated = accountService
                .changePassword(userId, "password4576", "password45768");
        String updatedPassword = userDTOUpdated.getEncryptedPassword();
        assertThat(bCryptPasswordEncoder.matches("password45768", updatedPassword)).isTrue();
        assertThat(userDTOUpdated.getGymRoles()).contains(GymRole.USER);
    }
}
