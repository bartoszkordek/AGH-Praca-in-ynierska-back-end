package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.PhotoDocument;
import org.bson.types.Binary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class PhotoDAOTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PhotoDAO photoDAO;
    private PhotoDocument photoDocument;
    private String userId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        Binary binary = new Binary(userId.getBytes(StandardCharsets.UTF_8));
        photoDocument = new PhotoDocument(userId, "Avatar", binary);
        mongoTemplate.save(photoDocument);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(PhotoDocument.class);
    }

    @Test
    void shouldReturnPhotoDocumentByUserId() {
        assertThat(photoDAO.findByUserId(userId)).isEqualTo(photoDocument);
    }

    @Test
    void shouldReturnNullWhenPhotoDocumentByUserIdNotFound() {
        String randomId = UUID.randomUUID().toString();
        assertThat(photoDAO.findByUserId(randomId)).isNull();
    }
}