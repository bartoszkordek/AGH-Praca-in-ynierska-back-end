package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
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
class RegistrationTokenDAOTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    private UserDocument janKowalski, andrzejNowak;
    private RegistrationTokenDocument registrationToken;
    private String testToken;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RegistrationTokenDAO registrationTokenDAO;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        janKowalski = new UserDocument(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
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

        mongoTemplate.save(janKowalski);
        mongoTemplate.save(andrzejNowak);

        testToken = UUID.randomUUID().toString();

        registrationToken = new RegistrationTokenDocument(testToken, janKowalski);

        mongoTemplate.save(registrationToken);
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteAll();
        registrationTokenDAO.deleteAll();
    }

    @Nested
    class WhenFindByTokenIsCalled {

        @Test
        void shouldReturnNullWhenProvidedTokenDoesNotExists() {
            RegistrationTokenDocument foundToken = registrationTokenDAO.findByToken("sampleToken");
            assertThat(foundToken).isNull();
        }

        @Nested
        class WhenProvidedTokenExists {
            private RegistrationTokenDocument foundToken;

            @BeforeEach
            void setUp() {
                foundToken = registrationTokenDAO.findByToken(testToken);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperUserEntity() {
                UserDocument user = foundToken.getUserDocument();
                assertThat(user.getEmail()).isEqualTo(janKowalski.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalski.getUserId());
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(registrationToken.getExpiryDate());
            }
        }
    }

    @Nested
    class WhenFindByUserDocumentIsCalled {
        @Test
        void shouldReturnNullWhenProvidedUserIsNotAssociatedWithAnyToken() {
            RegistrationTokenDocument foundToken = registrationTokenDAO.findByUserDocument(andrzejNowak);
            assertThat(foundToken).isNull();
        }

        @Nested
        class WhenProvidedUserDocumentIsAssociatedWithARegistrationTokenDocument {
            private RegistrationTokenDocument foundToken;

            @BeforeEach
            void setUp() {
                foundToken = registrationTokenDAO.findByUserDocument(janKowalski);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperUserEntity() {
                UserDocument user = foundToken.getUserDocument();
                assertThat(user.getEmail()).isEqualTo(janKowalski.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalski.getUserId());
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(registrationToken.getExpiryDate());
            }
        }
    }

    @Nested
    class WhenSaveIsCalled {

        @Test
        void shouldSaveRegisterTokenInDB() {
            RegistrationTokenDocument saveToken = registrationTokenDAO
                    .save(new RegistrationTokenDocument(testToken, andrzejNowak));

            List<RegistrationTokenDocument> allTokens = registrationTokenDAO.findAll();

            for (RegistrationTokenDocument document : allTokens) {
                System.out.println(document);
            }

            System.out.println(saveToken);

            assertThat(allTokens.contains(saveToken)).isTrue();
        }
    }
}