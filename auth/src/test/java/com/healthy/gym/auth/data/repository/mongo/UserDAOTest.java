package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.enums.GymRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@Tags({@Tag("repository"), @Tag("integration")})
class UserDAOTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    private UserDocument janKowalski, mariaNowak, andrzejNowak;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDAO userDAO;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        janKowalski = new UserDocument("Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString()
        );

        mariaNowak = new UserDocument(
                "Maria",
                "Nowak",
                "maria.nowak@test.com",
                "686 777 888",
                bCryptPasswordEncoder.encode("password3456"),
                UUID.randomUUID().toString()
        );

        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                UUID.randomUUID().toString()
        );
        Set<GymRole> userRoles = new HashSet<>();
        userRoles.add(GymRole.USER);
        userRoles.add(GymRole.ADMIN);
        andrzejNowak.setGymRoles(userRoles);

        mongoTemplate.save(janKowalski);
        mongoTemplate.save(mariaNowak);
        mongoTemplate.save(andrzejNowak);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(janKowalski);
        mongoTemplate.remove(mariaNowak);
        mongoTemplate.remove(andrzejNowak);
    }

    @Test
    void shouldReturnProperUserDocumentWhenFindByEmailIsCalled() {
        UserDocument found = userDAO.findByEmail("jan.kowalski@test.com");
        assertThat(found)
                .isEqualTo(janKowalski)
                .hasSameHashCodeAs(janKowalski);
    }

    @Test
    void shouldReturnNullForIfUserNonExists() {
        UserDocument found = userDAO.findByEmail("non.existing@test.com");
        assertThat(found).isNull();
    }

    @Test
    void shouldReturnAllUsersInDatabase() {
        List<UserDocument> found = userDAO.findAll();
        assertThat(found)
                .isNotNull()
                .hasSize(3)
                .extracting(UserDocument::getUserId)
                .contains(
                        andrzejNowak.getUserId(),
                        mariaNowak.getUserId(),
                        janKowalski.getUserId()
                );
    }
}