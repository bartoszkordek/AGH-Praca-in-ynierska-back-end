package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.document.UserPrivacyDocument;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@Tags({@Tag("repository"), @Tag("integration")})
class UserPrivacyDAOTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserPrivacyDAO userPrivacyDAO;
    private UserDocument andrzejNowak;
    private UserPrivacyDocument userPrivacyDocument;
    private String andrzejNowakId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        andrzejNowakId = UUID.randomUUID().toString();
        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                andrzejNowakId
        );
        userPrivacyDocument = new UserPrivacyDocument();
        userPrivacyDocument.setUserDocument(andrzejNowak);

        mongoTemplate.save(andrzejNowak);
        mongoTemplate.save(userPrivacyDocument);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(UserPrivacyDocument.class);
    }

    @Nested
    class WhenFindByUserDocument {
        @Test
        void shouldReturnProperUserPrivacyDocumentWhenUserExists() {
            UserPrivacyDocument document = userPrivacyDAO.findByUserDocument(andrzejNowak);
            assertThat(document).isEqualTo(userPrivacyDocument);
        }

        @Test
        void shouldReturnNullWhenUserDoesNotExist() {
            mongoTemplate.dropCollection(UserPrivacyDocument.class);
            UserPrivacyDocument document = userPrivacyDAO.findByUserDocument(andrzejNowak);
            assertThat(document).isNull();
        }

        @Test
        void shouldReturnNullWhenNullProvided() {
            UserPrivacyDocument document = userPrivacyDAO.findByUserDocument(null);
            assertThat(document).isNull();
        }
    }
}