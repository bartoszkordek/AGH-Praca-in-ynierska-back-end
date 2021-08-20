package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.UserDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles(value = "test")
@Tags({@Tag("repository"), @Tag("integration")})
class UserDAOTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDAO userDAO;
    private UserDocument janKowalski, mariaNowak, andrzejNowak;
    private String andrzejNowakId;

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

        andrzejNowakId = UUID.randomUUID().toString();

        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                andrzejNowakId
        );

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

    @Nested
    class WhenFindByEmail {
        @Test
        void shouldReturnProperUserDocumentWhenUserExists() {
            UserDocument found = userDAO.findByEmail("jan.kowalski@test.com");
            assertThat(found)
                    .isEqualTo(janKowalski)
                    .hasSameHashCodeAs(janKowalski);
        }

        @Test
        void shouldReturnNullWhenUserDoesNotExist() {
            UserDocument found = userDAO.findByEmail("non.existing@test.com");
            assertThat(found).isNull();
        }

        @Test
        void shouldReturnNullWhenNullAsArgument() {
            UserDocument found = userDAO.findByEmail(null);
            assertThat(found).isNull();
        }
    }

    @Nested
    class WhenFindByUserId {
        @Test
        void shouldReturnProperUserDocumentWhenUserExists() {
            UserDocument foundUser = userDAO.findByUserId(andrzejNowakId);
            assertThat(foundUser).isEqualTo(andrzejNowak);
        }

        @Test
        void shouldReturnNullWhenUserDoesNotExist() {
            String nonExistingUserId = UUID.randomUUID().toString();
            UserDocument foundUser = userDAO.findByUserId(nonExistingUserId);
            assertThat(foundUser).isNull();
        }

        @Test
        void shouldReturnNullWhenNullProvided() {
            UserDocument foundUser = userDAO.findByUserId(null);
            assertThat(foundUser).isNull();
        }
    }

}