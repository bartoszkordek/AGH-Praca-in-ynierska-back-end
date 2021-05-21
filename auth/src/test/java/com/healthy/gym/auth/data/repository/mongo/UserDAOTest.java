package com.healthy.gym.auth.data.repository.mongo;

import com.healthy.gym.auth.data.document.UserDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yaml"})
class UserDAOTest {
    private UserDocument janKowalski, mariaNowak, andrzejNowak;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDAO userDAO;

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
        System.out.println(found);
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
        System.out.println(found);
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