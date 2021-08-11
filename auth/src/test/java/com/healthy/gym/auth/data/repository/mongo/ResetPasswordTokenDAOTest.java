package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@Tags({@Tag("repository"), @Tag("integration")})
class ResetPasswordTokenDAOTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    private UserDocument janKowalski, andrzejNowak;
    private ResetPasswordTokenDocument resetPasswordToken;
    private String testToken;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ResetPasswordTokenDAO resetPasswordTokenDAO;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
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

        resetPasswordToken = new ResetPasswordTokenDocument(testToken, janKowalski);
        mongoTemplate.save(resetPasswordToken);
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteAll();
        resetPasswordTokenDAO.deleteAll();
    }

    @Nested
    class WhenFindByTokenIsCalled {
        @Test
        void shouldReturnNullWhenProvidedTokenDoesNotExists() {
            ResetPasswordTokenDocument foundToken = resetPasswordTokenDAO.findByToken("sampleToken");
            assertThat(foundToken).isNull();
        }

        @Nested
        class WhenProvidedTokenExists {
            private ResetPasswordTokenDocument foundToken;

            @BeforeEach
            void setUp() {
                foundToken = resetPasswordTokenDAO.findByToken(testToken);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnResetPasswordTokenDocumentWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnResetPasswordTokenDocumentWithProperUserDocument() {
                UserDocument user = foundToken.getUserDocument();
                assertThat(user.getEmail()).isEqualTo(janKowalski.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalski.getUserId());
            }

            @Test
            void shouldReturnResetPasswordTokenDocumentWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(resetPasswordToken.getExpiryDate());
            }
        }
    }

    @Nested
    class WhenFindByUserDocumentIsCalled {
        @Test
        void shouldReturnNullWhenProvidedUserIsNotAssociatedWithAnyToken() {
            ResetPasswordTokenDocument foundToken = resetPasswordTokenDAO.findByUserDocument(andrzejNowak);
            assertThat(foundToken).isNull();
        }

        @Nested
        class WhenProvidedUserDocumentIsAssociatedWithAResetPasswordToken {
            private ResetPasswordTokenDocument foundToken;

            @BeforeEach
            void setUp() {
                foundToken = resetPasswordTokenDAO.findByUserDocument(janKowalski);
            }

            @Test
            void shouldNotReturnNull() {
                assertThat(foundToken).isNotNull();
            }

            @Test
            void shouldReturnResetPasswordDocumentWithProperToken() {
                assertThat(foundToken.getToken()).isEqualTo(testToken);
            }

            @Test
            void shouldReturnResetPasswordTokenDocumentWithProperUserDocument() {
                UserDocument user = foundToken.getUserDocument();
                assertThat(user.getEmail()).isEqualTo(janKowalski.getEmail());
                assertThat(user.getUserId()).isEqualTo(janKowalski.getUserId());
            }

            @Test
            void shouldReturnRegistrationTokenDocumentWithProperExpiryDate() {
                assertThat(foundToken.getExpiryDate()).isEqualTo(resetPasswordToken.getExpiryDate());
            }
        }
    }

    @Nested
    class WhenSaveIsCalled {
        @Test
        void shouldSaveResetPasswordTokenInDB() {
            ResetPasswordTokenDocument savedToken = resetPasswordTokenDAO
                    .save(new ResetPasswordTokenDocument(testToken, andrzejNowak));

            List<ResetPasswordTokenDocument> allTokens = resetPasswordTokenDAO.findAll();

            assertThat(allTokens.contains(savedToken)).isTrue();
        }
    }
}